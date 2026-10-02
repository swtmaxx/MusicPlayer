package com.example.musicplayer.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.data.repository.MusicRepository
import com.example.musicplayer.data.repository.PlaylistRepository
import com.example.musicplayer.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(val songs: List<Song> = emptyList(), val isLoading: Boolean = true)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val musicRepository: MusicRepository,
    private val playerController: PlayerController
) : ViewModel() {
    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()
    val currentSong = playerController.currentSong
    val isPlaying = playerController.isPlaying

    init {
        viewModelScope.launch {
            playlistRepository.getFavoriteSongIds().collect { ids ->
                musicRepository.getSongsByIds(ids).collect { songs ->
                    _uiState.value = FavoritesUiState(songs = songs, isLoading = false)
                }
            }
        }
    }

    fun playSong(song: Song) {
        val songs = _uiState.value.songs
        val index = songs.indexOfFirst { it.id == song.id }
        if (index >= 0) playerController.playSongs(songs, index)
        else playerController.playSongs(listOf(song), 0)
    }

    fun togglePlayPause() { playerController.togglePlayPause() }
}
