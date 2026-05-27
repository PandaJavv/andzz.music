package com.andzz.music.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.andzz.music.data.model.RepeatMode
import com.andzz.music.data.model.Song
import com.andzz.music.data.model.durationFormatted
import com.andzz.music.ui.theme.*
import com.andzz.music.viewmodel.MusicViewModel

@Composable
fun NowPlayingScreen(
    vm: MusicViewModel,
    onBack: () -> Unit
) {
    val song        by vm.currentSong.collectAsState()
    val isPlaying   by vm.isPlaying.collectAsState()
    val progress    by vm.progress.collectAsState()
    val duration    by vm.duration.collectAsState()
    val repeatMode  by vm.repeatMode.collectAsState()
    val shuffle     by vm.shuffleEnabled.collectAsState()

    var showLyrics  by remember { mutableStateOf(false) }

    if (song == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Nothing playing", color = TextSecondary)
        }
        return
    }

    val s = song!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        // ── Top bar ───────────────────────────────────────────────────────────
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Rounded.KeyboardArrowDown, "Back", tint = TextSecondary, modifier = Modifier.size(28.dp))
            }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("NOW PLAYING", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
            IconButton(onClick = { showLyrics = !showLyrics }) {
                Icon(
                    Icons.Rounded.Lyrics,
                    "Lyrics",
                    tint = if (showLyrics) Accent else TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Album Art ─────────────────────────────────────────────────────────
        val artUri = "content://media/external/audio/albumart/${s.albumId}"
        val scale by animateFloatAsState(
            targetValue = if (isPlaying) 1f else 0.88f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            label = "art_scale"
        )

        Box(
            modifier = Modifier
                .size(300.dp)
                .scale(scale)
                .shadow(30.dp, RoundedCornerShape(20.dp), spotColor = AccentGlow)
                .clip(RoundedCornerShape(20.dp))
                .background(ElevatedCard),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = artUri,
                contentDescription = "Album art",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Icon(Icons.Rounded.MusicNote, null, tint = TextDisabled, modifier = Modifier.size(64.dp))
        }

        Spacer(Modifier.height(32.dp))

        // ── Song info ─────────────────────────────────────────────────────────
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    s.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    s.artist,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = { vm.toggleFavorite(s) }) {
                Icon(
                    if (s.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    "Favorite",
                    tint = if (s.isFavorite) Gold else TextSecondary
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        // ── Progress ──────────────────────────────────────────────────────────
        val fraction = if (duration > 0) progress.toFloat() / duration else 0f
        var dragging by remember { mutableStateOf(false) }
        var dragValue by remember { mutableFloatStateOf(0f) }

        Slider(
            value = if (dragging) dragValue else fraction,
            onValueChange = { dragging = true; dragValue = it },
            onValueChangeFinished = {
                vm.seekTo((dragValue * duration).toLong())
                dragging = false
            },
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Accent,
                activeTrackColor = Accent,
                inactiveTrackColor = Divider
            )
        )

        Row(Modifier.fillMaxWidth()) {
            Text(formatMs(progress), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(Modifier.weight(1f))
            Text(formatMs(duration), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }

        Spacer(Modifier.height(24.dp))

        // ── Controls ──────────────────────────────────────────────────────────
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Shuffle
            IconButton(onClick = { vm.toggleShuffle() }) {
                Icon(
                    Icons.Rounded.Shuffle,
                    "Shuffle",
                    tint = if (shuffle) Accent else TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Previous
            IconButton(onClick = { vm.skipPrevious() }, modifier = Modifier.size(56.dp)) {
                Icon(Icons.Rounded.SkipPrevious, "Previous", tint = TextPrimary, modifier = Modifier.size(36.dp))
            }

            // Play/Pause
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .background(Accent, CircleShape)
                    .clickable { vm.togglePlayPause() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = DeepBlack,
                    modifier = Modifier.size(40.dp)
                )
            }

            // Next
            IconButton(onClick = { vm.skipNext() }, modifier = Modifier.size(56.dp)) {
                Icon(Icons.Rounded.SkipNext, "Next", tint = TextPrimary, modifier = Modifier.size(36.dp))
            }

            // Repeat
            IconButton(onClick = { vm.toggleRepeat() }) {
                Icon(
                    imageVector = when (repeatMode) {
                        RepeatMode.ONE  -> Icons.Rounded.RepeatOne
                        else            -> Icons.Rounded.Repeat
                    },
                    contentDescription = "Repeat",
                    tint = if (repeatMode != RepeatMode.NONE) Accent else TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // ── Lyrics panel ──────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = showLyrics,
            enter = fadeIn() + expandVertically(),
            exit  = fadeOut() + shrinkVertically()
        ) {
            Spacer(Modifier.height(24.dp))
            LyricsPanel(song = s)
        }
    }
}

@Composable
private fun LyricsPanel(song: Song) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp, max = 200.dp)
            .background(CardDark, RoundedCornerShape(16.dp))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        if (song.lyrics.isNullOrBlank()) {
            Text(
                "No lyrics available for this song.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextDisabled,
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                song.lyrics,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                modifier = Modifier.verticalScroll(rememberScrollState())
            )
        }
    }
}

private fun formatMs(ms: Long): String {
    val total = (ms / 1000).coerceAtLeast(0)
    return "%d:%02d".format(total / 60, total % 60)
}
