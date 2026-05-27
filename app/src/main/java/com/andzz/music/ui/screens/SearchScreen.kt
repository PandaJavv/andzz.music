package com.andzz.music.ui.screens

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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.andzz.music.data.model.Song
import com.andzz.music.ui.components.SongRow
import com.andzz.music.ui.theme.*
import com.andzz.music.viewmodel.MusicViewModel

@Composable
fun SearchScreen(
    vm: MusicViewModel,
    onSongClick: (Song) -> Unit
) {
    val query       by vm.searchQuery.collectAsState()
    val results     by vm.searchResults.collectAsState()
    val currentSong by vm.currentSong.collectAsState()
    val isPlaying   by vm.isPlaying.collectAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { vm.setSearchQuery(it) },
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
            placeholder = { Text("Search songs, artists, albums…", color = TextDisabled) },
            leadingIcon = { Icon(Icons.Rounded.Search, null, tint = Accent) },
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(onClick = { vm.setSearchQuery("") }) {
                        Icon(Icons.Rounded.Close, "Clear", tint = TextSecondary)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Accent,
                unfocusedBorderColor = Divider,
                cursorColor = Accent,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = CardDark,
                unfocusedContainerColor = CardDark
            )
        )

        Spacer(Modifier.height(16.dp))

        when {
            query.isBlank() -> {
                Column(
                    Modifier.fillMaxWidth().padding(top = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Rounded.Search, null, tint = TextDisabled, modifier = Modifier.size(56.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("Start typing to search", color = TextSecondary, style = MaterialTheme.typography.titleSmall)
                }
            }
            results.isEmpty() -> {
                Column(
                    Modifier.fillMaxWidth().padding(top = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Rounded.SearchOff, null, tint = TextDisabled, modifier = Modifier.size(56.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("No results for \"$query\"", color = TextSecondary, style = MaterialTheme.typography.titleSmall)
                }
            }
            else -> {
                Text(
                    "${results.size} results",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyColumn {
                    items(results, key = { it.id }) { song ->
                        SongRow(
                            song = song,
                            isPlaying = currentSong?.id == song.id && isPlaying,
                            onClick = { onSongClick(song) },
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
