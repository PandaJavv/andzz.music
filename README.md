# 🎵 AndzZ Music

A feature-rich Android music player built with **Jetpack Compose** and **Material 3**.

![Android](https://img.shields.io/badge/Android-API%2026+-green)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.08-purple)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## ✨ Features

| Feature | Description |
|---|---|
| 🎵 **Music Playback** | ExoPlayer-powered with background playback & notification controls |
| 📋 **Playlist Management** | Create, edit, delete playlists. Add/remove songs freely |
| 🎚️ **Equalizer** | 5-band EQ with 8 presets: Pop, Rock, Jazz, Classical, Bass, Treble, Vocal |
| 📝 **Lyrics Display** | Tap the lyrics icon in Now Playing to view stored lyrics |
| 🔍 **Search** | Real-time search across titles, artists, and albums |
| ❤️ **Favorites** | Mark songs as favorites for quick access |
| 📊 **Smart Lists** | Most Played and Recently Added auto-lists |
| 🔀 **Shuffle & Repeat** | Full shuffle, repeat-all, and repeat-one modes |
| 🎨 **Dark Theme** | Luxury dark aesthetic with electric violet accents |

---

## 🏗️ Architecture

```
com.andzz.music
├── data
│   ├── local        # Room database, DAOs, MediaStore scanner
│   ├── model        # Song, Playlist, PlayerState data classes
│   └── repository   # MusicRepository (single source of truth)
├── di               # Hilt dependency injection modules
├── service          # ExoPlayer MediaSessionService + PlayerController
├── ui
│   ├── components   # MiniPlayer, SongRow, AlbumArtwork, PlayingIndicator
│   ├── screens      # Home, Library, NowPlaying, Playlist, Search, Equalizer
│   └── theme        # Material3 dark color scheme & typography
└── viewmodel        # MusicViewModel (single ViewModel for entire app)
```

**Stack:**
- **UI** — Jetpack Compose + Material3
- **DI** — Hilt
- **DB** — Room
- **Player** — Media3 / ExoPlayer
- **Images** — Coil
- **Architecture** — MVVM + Repository + StateFlow

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 17
- Android device / emulator (API 26+)

### Build
```bash
git clone https://github.com/andzz/andzz.music.git
cd andzz.music
./gradlew assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

### Permissions
The app requests `READ_MEDIA_AUDIO` (Android 13+) or `READ_EXTERNAL_STORAGE` to scan your device for music files.

---

## 📱 Screenshots

> _Coming soon_

---

## 🤝 Contributing

Pull requests are welcome! For major changes, please open an issue first.

---

## 📄 License

MIT License — see [LICENSE](LICENSE) for details.
