package com.andzz.music.data.repository

import com.andzz.music.data.local.MediaStoreScanner
import com.andzz.music.data.local.MusicDatabase
import com.andzz.music.data.model.Playlist
import com.andzz.music.data.model.PlaylistSong
import com.andzz.music.data.model.Song
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(
    private val db: MusicDatabase,
    private val scanner: MediaStoreScanner
) {
    private val songDao     = db.songDao()
    private val playlistDao = db.playlistDao()

    // ── Songs ────────────────────────────────────────────────────────────────

    fun getAllSongs(): Flow<List<Song>> = songDao.getAllSongs()
    fun getFavorites(): Flow<List<Song>> = songDao.getFavorites()
    fun searchSongs(query: String): Flow<List<Song>> = songDao.searchSongs(query)
    fun getMostPlayed(limit: Int = 20): Flow<List<Song>> = songDao.getMostPlayed(limit)
    fun getRecentlyAdded(limit: Int = 20): Flow<List<Song>> = songDao.getRecentlyAdded(limit)
    fun getAllAlbums(): Flow<List<String>> = songDao.getAllAlbums()
    fun getSongsByAlbum(album: String): Flow<List<Song>> = songDao.getSongsByAlbum(album)
    fun getAllArtists(): Flow<List<String>> = songDao.getAllArtists()
    fun getSongsByArtist(artist: String): Flow<List<Song>> = songDao.getSongsByArtist(artist)

    suspend fun syncLocalMusic() {
        val songs = scanner.scanLocalMusic()
        songDao.upsertSongs(songs)
    }

    suspend fun toggleFavorite(song: Song) {
        songDao.setFavorite(song.id, !song.isFavorite)
    }

    suspend fun incrementPlayCount(songId: Long) {
        songDao.incrementPlayCount(songId)
    }

    suspend fun updateLyrics(songId: Long, lyrics: String) {
        songDao.updateLyrics(songId, lyrics)
    }

    // ── Playlists ────────────────────────────────────────────────────────────

    fun getAllPlaylists(): Flow<List<Playlist>> = playlistDao.getAllPlaylists()
    fun getPlaylistSongs(playlistId: Long): Flow<List<Song>> = playlistDao.getPlaylistSongs(playlistId)

    suspend fun createPlaylist(name: String, description: String = ""): Long {
        return playlistDao.insertPlaylist(
            Playlist(name = name, description = description)
        )
    }

    suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlist.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deletePlaylist(playlist: Playlist) {
        playlistDao.deletePlaylist(playlist)
    }

    suspend fun addSongToPlaylist(playlistId: Long, songId: Long) {
        val count = playlistDao.getPlaylistSongCount(playlistId)
        playlistDao.addSongToPlaylist(
            PlaylistSong(playlistId = playlistId, songId = songId, position = count)
        )
    }

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        playlistDao.removeSongFromPlaylist(playlistId, songId)
    }
}
