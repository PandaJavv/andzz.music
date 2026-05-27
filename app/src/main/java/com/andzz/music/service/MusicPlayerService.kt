package com.andzz.music.service

import android.media.audiofx.Equalizer
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.andzz.music.data.model.Song
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicPlayerService : MediaSessionService() {

    @Inject lateinit var player: ExoPlayer

    private var mediaSession: MediaSession? = null
    private var equalizer: Equalizer? = null

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSession.Builder(this, player).build()

        // Equalizer setup
        val audioSessionId = player.audioSessionId
        if (audioSessionId != C.AUDIO_SESSION_ID_UNSET) {
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = false
            }
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        equalizer?.release()
        super.onDestroy()
    }

    // ── Equalizer control ────────────────────────────────────────────────────

    fun setEqualizerEnabled(enabled: Boolean) {
        equalizer?.enabled = enabled
    }

    fun setEqualizerBandLevel(band: Short, gainDb: Float) {
        val eq = equalizer ?: return
        val minLevel = eq.bandLevelRange[0]
        val maxLevel = eq.bandLevelRange[1]
        val level = ((gainDb / 15f) * ((maxLevel - minLevel) / 2) + ((maxLevel + minLevel) / 2))
            .toInt().toShort()
            .coerceIn(minLevel, maxLevel)
        eq.setBandLevel(band, level)
    }

    fun getNumberOfBands(): Short = equalizer?.numberOfBands ?: 5

    companion object {
        fun buildMediaItem(song: Song): MediaItem =
            MediaItem.Builder()
                .setMediaId(song.id.toString())
                .setUri(song.path)
                .build()
    }
}
