package com.andzz.music.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andzz.music.data.model.*
import com.andzz.music.data.repository.MusicRepository
import com.andzz.music.service.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val repo: MusicRepository,
    val player: PlayerController
) : ViewModel() {

    // ── Library data ─────────────────────────────────────────────────────────

    val allSongs      = repo.getAllSongs().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val favorites     = repo.getFavorites().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val mostPlayed    = repo.getMostPlayed().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val recentlyAdded = repo.getRecentlyAdded().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allAlbums     = repo.getAllAlbums().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allArtists    = repo.getAllArtists().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allPlaylists  = repo.getAllPlaylists().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // ── Player state ─────────────────────────────────────────────────────────

    val isPlaying       = player.isPlaying
    val currentSong     = player.currentSong
    val progress        = player.progress
    val duration        = player.duration
    val repeatMode      = player.repeatMode
    val shuffleEnabled  = player.shuffleEnabled
    val queue           = player.queue

    // ── Search ───────────────────────────────────────────────────────────────

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val searchResults = _searchQuery
        .debounce(300)
        .flatMapLatest { q -> if (q.isBlank()) flowOf(emptyList()) else repo.searchSongs(q) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // ── Equalizer ────────────────────────────────────────────────────────────

    private val _equalizerState = MutableStateFlow(EqualizerState())
    val equalizerState = _equalizerState.asStateFlow()

    // ── Actions ──────────────────────────────────────────────────────────────

    fun syncMusic() = viewModelScope.launch { repo.syncLocalMusic() }

    fun playSong(song: Song, queue: List<Song> = allSongs.value) {
        val idx = queue.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
        player.playSongs(queue, idx)
        viewModelScope.launch { repo.incrementPlayCount(song.id) }
    }

    fun togglePlayPause() = player.togglePlayPause()
    fun skipNext()        = player.skipNext()
    fun skipPrevious()    = player.skipPrevious()
    fun seekTo(ms: Long)  = player.seekTo(ms)
    fun toggleRepeat()    = player.toggleRepeat()
    fun toggleShuffle()   = player.toggleShuffle()

    fun toggleFavorite(song: Song) = viewModelScope.launch { repo.toggleFavorite(song) }

    fun setSearchQuery(q: String) { _searchQuery.value = q }

    fun setEqualizerEnabled(enabled: Boolean) {
        _equalizerState.update { it.copy(enabled = enabled) }
    }

    fun setEqualizerBand(index: Int, gain: Float) {
        _equalizerState.update { state ->
            val bands = state.bands.toMutableList()
            bands[index] = bands[index].copy(gain = gain)
            state.copy(bands = bands, presetName = "Custom")
        }
    }

    fun applyEqualizerPreset(name: String) {
        val gains = equalizerPresets[name] ?: return
        _equalizerState.update { state ->
            val bands = state.bands.mapIndexed { i, band ->
                band.copy(gain = gains.getOrElse(i) { 0f })
            }
            state.copy(bands = bands, presetName = name)
        }
    }

    fun createPlaylist(name: String) = viewModelScope.launch { repo.createPlaylist(name) }

    fun addToPlaylist(playlistId: Long, songId: Long) =
        viewModelScope.launch { repo.addSongToPlaylist(playlistId, songId) }

    fun removeFromPlaylist(playlistId: Long, songId: Long) =
        viewModelScope.launch { repo.removeSongFromPlaylist(playlistId, songId) }

    fun deletePlaylist(playlist: com.andzz.music.data.model.Playlist) =
        viewModelScope.launch { repo.deletePlaylist(playlist) }

    fun getPlaylistSongs(playlistId: Long): StateFlow<List<Song>> =
        repo.getPlaylistSongs(playlistId).stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun playPlaylist(songs: List<Song>) {
        if (songs.isNotEmpty()) player.playSongs(songs)
    }
}
