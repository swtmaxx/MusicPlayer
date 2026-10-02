package com.example.musicplayer.ui.navigation

sealed class Screen(val route: String, val title: String) {
    data object Songs : Screen("songs", "歌曲")
    data object Playlists : Screen("playlists", "歌单")
    data object Favorites : Screen("favorites", "我喜欢")
    data object Player : Screen("player", "播放")
}
