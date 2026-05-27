package com.andzz.music.data.local

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.andzz.music.data.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaStoreScanner @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun scanLocalMusic(): List<Song> {
        val songs = mutableListOf<Song>()

        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.TRACK
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} >= 30000"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        val cursor: Cursor? = context.contentResolver.query(uri, projection, selection, null, sortOrder)

        cursor?.use { c ->
            val idCol       = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol    = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol   = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol    = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val albumIdCol  = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val durationCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val pathCol     = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val sizeCol     = c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val dateCol     = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val trackCol    = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)

            while (c.moveToNext()) {
                songs.add(
                    Song(
                        id          = c.getLong(idCol),
                        title       = c.getString(titleCol) ?: "Unknown",
                        artist      = c.getString(artistCol) ?: "Unknown Artist",
                        album       = c.getString(albumCol) ?: "Unknown Album",
                        albumId     = c.getLong(albumIdCol),
                        duration    = c.getLong(durationCol),
                        path        = c.getString(pathCol) ?: "",
                        size        = c.getLong(sizeCol),
                        dateAdded   = c.getLong(dateCol),
                        trackNumber = c.getInt(trackCol)
                    )
                )
            }
        }

        return songs
    }

    fun getAlbumArtUri(albumId: Long): Uri =
        Uri.parse("content://media/external/audio/albumart/$albumId")
}
