package com.example.musicplayer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.musicplayer.ui.screens.favorites.FavoritesScreen
import com.example.musicplayer.ui.screens.playlists.PlaylistsScreen
import com.example.musicplayer.ui.screens.songs.SongsScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Songs.route) {
        composable(Screen.Songs.route) { SongsScreen() }
        composable(Screen.Playlists.route) { PlaylistsScreen() }
        composable(Screen.Favorites.route) { FavoritesScreen() }
    }
}
