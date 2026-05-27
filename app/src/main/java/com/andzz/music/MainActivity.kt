package com.andzz.music

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.andzz.music.data.model.Song
import com.andzz.music.ui.screens.*
import com.andzz.music.ui.components.MiniPlayer
import com.andzz.music.ui.theme.*
import com.andzz.music.viewmodel.MusicViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val vm: MusicViewModel by viewModels()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) vm.syncMusic()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.player.connect()
        requestPermissionsIfNeeded()

        setContent {
            AndzZMusicTheme {
                AndzZMusicApp(vm)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.player.disconnect()
    }

    private fun requestPermissionsIfNeeded() {
        val needed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        val toRequest = needed.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (toRequest.isEmpty()) {
            vm.syncMusic()
        } else {
            permissionLauncher.launch(toRequest.toTypedArray())
        }
    }
}

// ── Navigation ────────────────────────────────────────────────────────────────

enum class Screen(val label: String, val icon: ImageVector) {
    Home    ("Home",      Icons.Rounded.Home),
    Library ("Library",   Icons.Rounded.LibraryMusic),
    Search  ("Search",    Icons.Rounded.Search),
    Playlist("Playlists", Icons.Rounded.QueueMusic),
    EQ      ("EQ",        Icons.Rounded.Equalizer),
}

@Composable
fun AndzZMusicApp(vm: MusicViewModel) {
    var currentScreen  by remember { mutableStateOf(Screen.Home) }
    var showNowPlaying by remember { mutableStateOf(false) }

    val currentSong by vm.currentSong.collectAsState()
    val isPlaying   by vm.isPlaying.collectAsState()
    val progress    by vm.progress.collectAsState()
    val duration    by vm.duration.collectAsState()

    val onSongClick: (Song) -> Unit = { song ->
        vm.playSong(song)
        showNowPlaying = true
    }

    if (showNowPlaying) {
        NowPlayingScreen(vm = vm, onBack = { showNowPlaying = false })
        return
    }

    Scaffold(
        containerColor = DeepBlack,
        bottomBar = {
            Column {
                // Mini player
                AnimatedVisibility(
                    visible = currentSong != null,
                    enter = slideInVertically { it },
                    exit  = slideOutVertically { it }
                ) {
                    currentSong?.let { song ->
                        MiniPlayer(
                            song = song,
                            isPlaying = isPlaying,
                            progress = progress,
                            duration = duration,
                            onTogglePlay = { vm.togglePlayPause() },
                            onSkipNext = { vm.skipNext() },
                            onClick = { showNowPlaying = true }
                        )
                    }
                }

                // Bottom nav
                NavigationBar(containerColor = SurfaceDark, tonalElevation = 0.dp) {
                    Screen.entries.forEach { screen ->
                        NavigationBarItem(
                            selected = currentScreen == screen,
                            onClick  = { currentScreen = screen },
                            icon = {
                                Icon(screen.icon, screen.label)
                            },
                            label = { Text(screen.label, style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor   = Accent,
                                selectedTextColor   = Accent,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor      = AccentGlow
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (currentScreen) {
                Screen.Home     -> HomeScreen(vm, onSongClick, { showNowPlaying = true })
                Screen.Library  -> LibraryScreen(vm, onSongClick)
                Screen.Search   -> SearchScreen(vm, onSongClick)
                Screen.Playlist -> PlaylistsScreen(vm, onSongClick)
                Screen.EQ       -> EqualizerScreen(vm)
            }
        }
    }
}
