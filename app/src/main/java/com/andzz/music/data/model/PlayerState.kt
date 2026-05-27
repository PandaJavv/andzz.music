package com.andzz.music.data.model

data class PlayerState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val progress: Long = 0L,
    val duration: Long = 0L,
    val repeatMode: RepeatMode = RepeatMode.NONE,
    val shuffleEnabled: Boolean = false,
    val queue: List<Song> = emptyList(),
    val currentIndex: Int = 0
)

enum class RepeatMode {
    NONE, ONE, ALL
}

data class EqualizerState(
    val enabled: Boolean = false,
    val bands: List<EqualizerBand> = defaultBands(),
    val presetName: String = "Custom"
)

data class EqualizerBand(
    val frequency: Int,   // Hz
    val gain: Float       // dB, range -15..+15
)

fun defaultBands() = listOf(
    EqualizerBand(60, 0f),
    EqualizerBand(230, 0f),
    EqualizerBand(910, 0f),
    EqualizerBand(3600, 0f),
    EqualizerBand(14000, 0f)
)

val equalizerPresets = mapOf(
    "Flat"      to listOf(0f, 0f, 0f, 0f, 0f),
    "Pop"       to listOf(-1f, 2f, 4f, 2f, -1f),
    "Rock"      to listOf(4f, 2f, -1f, 2f, 4f),
    "Jazz"      to listOf(3f, 1f, 0f, 1f, 3f),
    "Classical" to listOf(5f, 3f, -2f, 3f, 4f),
    "Bass"      to listOf(8f, 5f, 0f, -1f, -2f),
    "Treble"    to listOf(-2f, -1f, 0f, 5f, 8f),
    "Vocal"     to listOf(-2f, 3f, 5f, 3f, -1f)
)
