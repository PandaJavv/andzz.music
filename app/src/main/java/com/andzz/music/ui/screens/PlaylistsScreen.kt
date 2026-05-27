package com.andzz.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.andzz.music.data.model.Playlist
import com.andzz.music.data.model.Song
import com.andzz.music.ui.components.SongRow
import com.andzz.music.ui.theme.*
import com.andzz.music.viewmodel.MusicViewModel

@Composable
fun PlaylistsScreen(
    vm: MusicViewModel,
    onSongClick: (Song) -> Unit
) {
    val playlists by vm.allPlaylists.collectAsState()
    var selectedPlaylist by remember { mutableStateOf<Playlist?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    if (selectedPlaylist != null) {
        PlaylistDetailScreen(
            playlist = selectedPlaylist!!,
            vm = vm,
            onBack = { selectedPlaylist = null },
            onSongClick = onSongClick
        )
        return
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Playlists", style = MaterialTheme.typography.headlineLarge, color = TextPrimary, modifier = Modifier.weight(1f))
            FilledIconButton(
                onClick = { showCreateDialog = true },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Accent)
            ) {
                Icon(Icons.Rounded.Add, "New playlist", tint = DeepBlack)
            }
        }

        LazyColumn(contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
            items(playlists, key = { it.id }) { playlist ->
                PlaylistRow(
                    playlist = playlist,
                    onClick = { selectedPlaylist = playlist },
                    onDelete = { vm.deletePlaylist(playlist) }
                )
            }
            if (playlists.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 80.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Rounded.QueueMusic, null, tint = TextDisabled, modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("No playlists yet", color = TextSecondary, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text("Tap + to create one", color = TextDisabled, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreatePlaylistDialog(
            onConfirm = { name ->
                vm.createPlaylist(name)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }
}

@Composable
fun PlaylistRow(playlist: Playlist, onClick: () -> Unit, onDelete: () -> Unit) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(ElevatedCard, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.QueueMusic, null, tint = Accent, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(playlist.name, style = MaterialTheme.typography.titleSmall, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (playlist.description.isNotBlank()) {
                Text(playlist.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        IconButton(onClick = { showDeleteConfirm = true }, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Rounded.DeleteOutline, "Delete", tint = TextDisabled, modifier = Modifier.size(18.dp))
        }
    }
    Spacer(Modifier.height(6.dp))

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete playlist?") },
            text  = { Text("\"${playlist.name}\" will be permanently deleted.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteConfirm = false }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            },
            containerColor = CardDark
        )
    }
}

@Composable
fun PlaylistDetailScreen(
    playlist: Playlist,
    vm: MusicViewModel,
    onBack: () -> Unit,
    onSongClick: (Song) -> Unit
) {
    val songs       by vm.getPlaylistSongs(playlist.id).collectAsState()
    val currentSong by vm.currentSong.collectAsState()
    val isPlaying   by vm.isPlaying.collectAsState()

    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBack, "Back", tint = TextPrimary)
            }
            Column(Modifier.weight(1f)) {
                Text(playlist.name, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                Text("${songs.size} songs", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
            if (songs.isNotEmpty()) {
                Button(
                    onClick = { vm.playPlaylist(songs) },
                    colors = ButtonDefaults.buttonColors(containerColor = Accent)
                ) {
                    Icon(Icons.Rounded.PlayArrow, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Play All")
                }
            }
        }

        LazyColumn(contentPadding = PaddingValues(horizontal = 8.dp)) {
            items(songs, key = { it.id }) { song ->
                SongRow(
                    song = song,
                    isPlaying = currentSong?.id == song.id && isPlaying,
                    onClick = { onSongClick(song) },
                    onMoreClick = { vm.removeFromPlaylist(playlist.id, song.id) },
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun CreatePlaylistDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        title = { Text("New Playlist", color = TextPrimary) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Playlist name") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent,
                    focusedLabelColor = Accent,
                    cursorColor = Accent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim()) },
                colors = ButtonDefaults.buttonColors(containerColor = Accent)
            ) { Text("Create", color = DeepBlack) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}
