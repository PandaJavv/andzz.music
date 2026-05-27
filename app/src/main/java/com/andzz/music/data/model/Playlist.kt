package com.andzz.music.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val coverSongId: Long? = null
)

@Entity(tableName = "playlist_songs", primaryKeys = ["playlistId", "songId"])
data class PlaylistSong(
    val playlistId: Long,
    val songId: Long,
    val position: Int,
    val addedAt: Long = System.currentTimeMillis()
)

data class PlaylistWithSongs(
    val playlist: Playlist,
    val songs: List<Song>
)
