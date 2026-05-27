package com.andzz.music.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andzz.music.data.model.Song
import com.andzz.music.ui.components.SongRow
import com.andzz.music.ui.theme.*
import com.andzz.music.viewmodel.MusicViewModel

@Composable
fun LibraryScreen(
    vm: MusicViewModel,
    onSongClick: (Song) -> Unit
) {
    val tabs = listOf("Songs", "Albums", "Artists", "Favorites")
    var selectedTab by remember { mutableIntStateOf(0) }

    val allSongs    by vm.allSongs.collectAsState()
    val favorites   by vm.favorites.collectAsState()
    val allAlbums   by vm.allAlbums.collectAsState()
    val allArtists  by vm.allArtists.collectAsState()
    val currentSong by vm.currentSong.collectAsState()
    val isPlaying   by vm.isPlaying.collectAsState()

    Column {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor   = DeepBlack,
            contentColor     = Accent,
            edgePadding      = 16.dp
        ) {
            tabs.forEachIndexed { index, label ->
                Tab(
                    selected = selectedTab == index,
                    onClick  = { selectedTab = index },
                    text = {
                        Text(
                            label,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selectedTab == index) Accent else TextSecondary
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> SongsTab(allSongs, currentSong, isPlaying, onSongClick)
            1 -> AlbumsTab(allAlbums, vm, onSongClick, currentSong, isPlaying)
            2 -> ArtistsTab(allArtists, vm, onSongClick, currentSong, isPlaying)
            3 -> SongsTab(favorites, currentSong, isPlaying, onSongClick)
        }
    }
}

@Composable
private fun SongsTab(
    songs: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    onSongClick: (Song) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)) {
        items(songs, key = { it.id }) { song ->
            SongRow(
                song = song,
                isPlaying = currentSong?.id == song.id && isPlaying,
                onClick = { onSongClick(song) },
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun AlbumsTab(
    albums: List<String>,
    vm: MusicViewModel,
    onSongClick: (Song) -> Unit,
    currentSong: Song?,
    isPlaying: Boolean
) {
    var expandedAlbum by remember { mutableStateOf<String?>(null) }

    LazyColumn(contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)) {
        items(albums) { album ->
            val expanded = expandedAlbum == album
            val songs by vm.allSongs.collectAsState()
            val albumSongs = songs.filter { it.album == album }

            Card(
                onClick = { expandedAlbum = if (expanded) null else album },
                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        albumSongs.firstOrNull()?.let {
                            com.andzz.music.ui.components.AlbumArtwork(
                                albumId = it.albumId,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                        }
                        Column(Modifier.weight(1f)) {
                            Text(album, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                            Text(
                                "${albumSongs.firstOrNull()?.artist ?: ""} · ${albumSongs.size} tracks",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                    if (expanded) {
                        Spacer(Modifier.height(8.dp))
                        albumSongs.forEach { song ->
                            SongRow(
                                song = song,
                                isPlaying = currentSong?.id == song.id && isPlaying,
                                onClick = { onSongClick(song) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArtistsTab(
    artists: List<String>,
    vm: MusicViewModel,
    onSongClick: (Song) -> Unit,
    currentSong: Song?,
    isPlaying: Boolean
) {
    var expandedArtist by remember { mutableStateOf<String?>(null) }
    val allSongs by vm.allSongs.collectAsState()

    LazyColumn(contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)) {
        items(artists) { artist ->
            val expanded    = expandedArtist == artist
            val artistSongs = allSongs.filter { it.artist == artist }

            Card(
                onClick = { expandedArtist = if (expanded) null else artist },
                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier
                                .size(48.dp)
                                .androidx.compose.foundation.background(
                                    ElevatedCard,
                                    androidx.compose.foundation.shape.CircleShape
                                ),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            Text(
                                text = artist.first().uppercase(),
                                style = MaterialTheme.typography.titleLarge,
                                color = Accent
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(artist, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                            Text("${artistSongs.size} songs", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                    }
                    if (expanded) {
                        Spacer(Modifier.height(8.dp))
                        artistSongs.forEach { song ->
                            SongRow(
                                song = song,
                                isPlaying = currentSong?.id == song.id && isPlaying,
                                onClick = { onSongClick(song) }
                            )
                        }
                    }
                }
            }
        }
    }
}
