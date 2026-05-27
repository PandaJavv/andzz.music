package com.andzz.music.service

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.andzz.music.data.model.RepeatMode
import com.andzz.music.data.model.Song
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerController @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null

    private val _isPlaying       = MutableStateFlow(false)
    private val _currentSong     = MutableStateFlow<Song?>(null)
    private val _progress        = MutableStateFlow(0L)
    private val _duration        = MutableStateFlow(0L)
    private val _repeatMode      = MutableStateFlow(RepeatMode.NONE)
    private val _shuffleEnabled  = MutableStateFlow(false)

    val isPlaying:      StateFlow<Boolean>      = _isPlaying.asStateFlow()
    val currentSong:    StateFlow<Song?>        = _currentSong.asStateFlow()
    val progress:       StateFlow<Long>         = _progress.asStateFlow()
    val duration:       StateFlow<Long>         = _duration.asStateFlow()
    val repeatMode:     StateFlow<RepeatMode>   = _repeatMode.asStateFlow()
    val shuffleEnabled: StateFlow<Boolean>      = _shuffleEnabled.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var progressJob: Job? = null

    // Queue maintained in-memory on the controller side
    private val _queue = MutableStateFlow<List<Song>>(emptyList())
    val queue: StateFlow<List<Song>> = _queue.asStateFlow()

    fun connect() {
        val token = SessionToken(
            context,
            ComponentName(context, MusicPlayerService::class.java)
        )
        controllerFuture = MediaController.Builder(context, token).buildAsync()
        controllerFuture?.addListener({
            controller = controllerFuture?.get()
            controller?.addListener(playerListener)
        }, CoroutineScope(Dispatchers.Main).asExecutor())
    }

    fun disconnect() {
        controller?.removeListener(playerListener)
        MediaController.releaseFuture(controllerFuture ?: return)
        scope.cancel()
    }

    // ── Playback controls ────────────────────────────────────────────────────

    fun playSongs(songs: List<Song>, startIndex: Int = 0) {
        _queue.value = songs
        val items = songs.map { MusicPlayerService.buildMediaItem(it) }
        controller?.run {
            setMediaItems(items, startIndex, 0L)
            prepare()
            play()
        }
        _currentSong.value = songs.getOrNull(startIndex)
        startProgressTracking()
    }

    fun togglePlayPause() {
        controller?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun skipNext() { controller?.seekToNextMediaItem() }
    fun skipPrevious() {
        controller?.let {
            if (it.currentPosition > 3000L) it.seekTo(0L) else it.seekToPreviousMediaItem()
        }
    }

    fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs)
        _progress.value = positionMs
    }

    fun toggleRepeat() {
        val next = when (_repeatMode.value) {
            RepeatMode.NONE -> RepeatMode.ALL
            RepeatMode.ALL  -> RepeatMode.ONE
            RepeatMode.ONE  -> RepeatMode.NONE
        }
        _repeatMode.value = next
        controller?.repeatMode = when (next) {
            RepeatMode.NONE -> Player.REPEAT_MODE_OFF
            RepeatMode.ALL  -> Player.REPEAT_MODE_ALL
            RepeatMode.ONE  -> Player.REPEAT_MODE_ONE
        }
    }

    fun toggleShuffle() {
        val next = !_shuffleEnabled.value
        _shuffleEnabled.value = next
        controller?.shuffleModeEnabled = next
    }

    fun setSong(song: Song) {
        val idx = _queue.value.indexOfFirst { it.id == song.id }
        if (idx >= 0) {
            controller?.seekToDefaultPosition(idx)
            controller?.play()
            _currentSong.value = song
        } else {
            playSongs(listOf(song))
        }
    }

    // ── Progress tracking ────────────────────────────────────────────────────

    private fun startProgressTracking() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                controller?.let {
                    _progress.value = it.currentPosition
                    _duration.value = it.duration.coerceAtLeast(0L)
                    _isPlaying.value = it.isPlaying
                }
                delay(500L)
            }
        }
    }

    // ── Player listener ──────────────────────────────────────────────────────

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
            if (isPlaying) startProgressTracking() else progressJob?.cancel()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val idx = controller?.currentMediaItemIndex ?: return
            _currentSong.value = _queue.value.getOrNull(idx)
        }
    }
}

// Helper extension
private fun CoroutineScope.asExecutor() = java.util.concurrent.Executor { runnable ->
    launch { runnable.run() }
}
