package com.andzz.music.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.andzz.music.data.model.Song
import com.andzz.music.ui.components.AlbumArtwork
import com.andzz.music.ui.components.SongRow
import com.andzz.music.ui.theme.*
import com.andzz.music.viewmodel.MusicViewModel

@Composable
fun HomeScreen(
    vm: MusicViewModel,
    onSongClick: (Song) -> Unit,
    onNavigateToPlayer: () -> Unit
) {
    val recentSongs by vm.recentlyAdded.collectAsState()
    val mostPlayed  by vm.mostPlayed.collectAsState()
    val currentSong by vm.currentSong.collectAsState()
    val isPlaying   by vm.isPlaying.collectAsState()

    LazyColumn(
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Good vibes,", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Text("AndzZ Music", style = MaterialTheme.typography.headlineLarge, color = TextPrimary)
                }
                IconButton(onClick = { vm.syncMusic() }) {
                    Icon(Icons.Rounded.Refresh, contentDescription = "Sync", tint = Accent)
                }
            }
        }

        // ── Recently Added ────────────────────────────────────────────────────
        if (recentSongs.isNotEmpty()) {
            item {
                SectionHeader("Recently Added", modifier = Modifier.padding(horizontal = 20.dp))
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recentSongs.take(10)) { song ->
                        SongCard(
                            song = song,
                            isPlaying = currentSong?.id == song.id && isPlaying,
                            onClick = { onSongClick(song) }
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }

        // ── Most Played ───────────────────────────────────────────────────────
        if (mostPlayed.isNotEmpty()) {
            item {
                SectionHeader("Most Played", modifier = Modifier.padding(horizontal = 20.dp))
                Spacer(Modifier.height(8.dp))
            }
            items(mostPlayed.take(10)) { song ->
                SongRow(
                    song = song,
                    isPlaying = currentSong?.id == song.id && isPlaying,
                    onClick = { onSongClick(song) },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )
            }
        }

        // ── Empty state ───────────────────────────────────────────────────────
        if (recentSongs.isEmpty() && mostPlayed.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Rounded.LibraryMusic,
                        contentDescription = null,
                        tint = TextDisabled,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("No music found", style = MaterialTheme.typography.titleMedium, color = TextSecondary)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Tap Refresh to scan your device",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDisabled
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { vm.syncMusic() },
                        colors = ButtonDefaults.buttonColors(containerColor = Accent)
                    ) {
                        Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Scan Music")
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = TextPrimary,
        modifier = modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun SongCard(song: Song, isPlaying: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(130.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        AlbumArtwork(
            albumId = song.albumId,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = song.title,
            style = MaterialTheme.typography.labelLarge,
            color = if (isPlaying) Accent else TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = song.artist,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
