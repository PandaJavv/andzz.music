package com.andzz.music.data.local

import androidx.room.*
import com.andzz.music.data.model.Playlist
import com.andzz.music.data.model.PlaylistSong
import com.andzz.music.data.model.Song
import kotlinx.coroutines.flow.Flow

// ─── DAOs ────────────────────────────────────────────────────────────────────

@Dao
interface SongDao {
    @Query("SELECT * FROM songs ORDER BY title ASC")
    fun getAllSongs(): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavorites(): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%' OR album LIKE '%' || :query || '%'")
    fun searchSongs(query: String): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE id = :id")
    suspend fun getSongById(id: Long): Song?

    @Upsert
    suspend fun upsertSongs(songs: List<Song>)

    @Query("UPDATE songs SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    @Query("UPDATE songs SET playCount = playCount + 1 WHERE id = :id")
    suspend fun incrementPlayCount(id: Long)

    @Query("UPDATE songs SET lyrics = :lyrics WHERE id = :id")
    suspend fun updateLyrics(id: Long, lyrics: String)

    @Query("SELECT * FROM songs ORDER BY playCount DESC LIMIT :limit")
    fun getMostPlayed(limit: Int = 20): Flow<List<Song>>

    @Query("SELECT * FROM songs ORDER BY dateAdded DESC LIMIT :limit")
    fun getRecentlyAdded(limit: Int = 20): Flow<List<Song>>

    @Query("SELECT DISTINCT album FROM songs ORDER BY album ASC")
    fun getAllAlbums(): Flow<List<String>>

    @Query("SELECT * FROM songs WHERE album = :album ORDER BY trackNumber ASC")
    fun getSongsByAlbum(album: String): Flow<List<Song>>

    @Query("SELECT DISTINCT artist FROM songs ORDER BY artist ASC")
    fun getAllArtists(): Flow<List<String>>

    @Query("SELECT * FROM songs WHERE artist = :artist ORDER BY title ASC")
    fun getSongsByArtist(artist: String): Flow<List<Song>>
}

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY updatedAt DESC")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun getPlaylistById(id: Long): Playlist?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist): Long

    @Update
    suspend fun updatePlaylist(playlist: Playlist)

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    @Query("""
        SELECT s.* FROM songs s
        INNER JOIN playlist_songs ps ON s.id = ps.songId
        WHERE ps.playlistId = :playlistId
        ORDER BY ps.position ASC
    """)
    fun getPlaylistSongs(playlistId: Long): Flow<List<Song>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSongToPlaylist(playlistSong: PlaylistSong)

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long)

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun clearPlaylist(playlistId: Long)

    @Query("SELECT COUNT(*) FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun getPlaylistSongCount(playlistId: Long): Int
}

// ─── Database ────────────────────────────────────────────────────────────────

@Database(
    entities = [Song::class, Playlist::class, PlaylistSong::class],
    version = 1,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        const val DATABASE_NAME = "andzz_music.db"
    }
}
