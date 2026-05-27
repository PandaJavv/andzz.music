package com.andzz.music.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.andzz.music.data.model.Song
import com.andzz.music.data.model.durationFormatted
import com.andzz.music.ui.theme.*

// ── Album Art ─────────────────────────────────────────────────────────────────

@Composable
fun AlbumArtwork(
    albumId: Long,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp)
) {
    val artUri = "content://media/external/audio/albumart/$albumId"
    Box(
        modifier = modifier
            .clip(shape)
            .background(ElevatedCard),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = artUri,
            contentDescription = "Album art",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Fallback icon rendered below AsyncImage; Coil will overlay the image when loaded
        Icon(
            Icons.Rounded.MusicNote,
            contentDescription = null,
            tint = TextDisabled,
            modifier = Modifier.size(40.dp)
        )
    }
}

// ── Song Row ──────────────────────────────────────────────────────────────────

@Composable
fun SongRow(
    song: Song,
    isPlaying: Boolean = false,
    onClick: () -> Unit,
    onMoreClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val bgAlpha by animateFloatAsState(
        targetValue = if (isPlaying) 1f else 0f,
        animationSpec = tween(300),
        label = "bg"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = AccentGlow.copy(alpha = bgAlpha * 0.15f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AlbumArtwork(
            albumId = song.albumId,
            modifier = Modifier.size(52.dp)
        )

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.titleSmall,
                color = if (isPlaying) Accent else TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${song.artist} · ${song.album}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(8.dp))

        if (isPlaying) {
            PlayingIndicator(modifier = Modifier.size(20.dp))
        } else {
            Text(
                text = song.durationFormatted(),
                style = MaterialTheme.typography.labelSmall,
                color = TextDisabled
            )
        }

        onMoreClick?.let {
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = it, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ── Animated Playing Indicator ────────────────────────────────────────────────

@Composable
fun PlayingIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "playing")
    val bars = 3
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.Bottom) {
        repeat(bars) { i ->
            val phase = i * 150
            val height by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, easing = FastOutSlowInEasing, delayMillis = phase),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar$i"
            )
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight(height)
                    .background(Accent, RoundedCornerShape(2.dp))
            )
        }
    }
}

// ── Mini Player ───────────────────────────────────────────────────────────────

@Composable
fun MiniPlayer(
    song: Song,
    isPlaying: Boolean,
    progress: Long,
    duration: Long,
    onTogglePlay: () -> Unit,
    onSkipNext: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressFraction = if (duration > 0) progress.toFloat() / duration else 0f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CardDark)
            .clickable(onClick = onClick)
    ) {
        // Progress bar
        LinearProgressIndicator(
            progress = { progressFraction },
            modifier = Modifier.fillMaxWidth().height(2.dp),
            color = Accent,
            trackColor = Divider
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AlbumArtwork(
                albumId = song.albumId,
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = onTogglePlay) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Accent,
                    modifier = Modifier.size(30.dp)
                )
            }

            IconButton(onClick = onSkipNext) {
                Icon(
                    Icons.Rounded.SkipNext,
                    contentDescription = "Skip next",
                    tint = TextSecondary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}
