package com.andzz.music.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val duration: Long,          // milliseconds
    val path: String,
    val size: Long,
    val dateAdded: Long,
    val trackNumber: Int = 0,
    val isFavorite: Boolean = false,
    val playCount: Int = 0,
    val lyrics: String? = null
)

fun Song.durationFormatted(): String {
    val totalSeconds = duration / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
