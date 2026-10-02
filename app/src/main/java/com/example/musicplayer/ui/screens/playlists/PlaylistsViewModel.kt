package com.example.musicplayer.ui.screens.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.local.entity.PlaylistEntity
import com.example.musicplayer.data.repository.MusicRepository
import com.example.musicplayer.data.repository.PlaylistRepository
import com.example.musicplayer.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlaylistsUiState(val playlists: List<PlaylistEntity> = emptyList(), val isLoading: Boolean = true)

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val musicRepository: MusicRepository,
    private val playerController: PlayerController
) : ViewModel() {
    private val _uiState = MutableStateFlow(PlaylistsUiState())
    val uiState: StateFlow<PlaylistsUiState> = _uiState.asStateFlow()
    val currentSong = playerController.currentSong
    val isPlaying = playerController.isPlaying

    init {
        viewModelScope.launch {
            playlistRepository.getAllPlaylists().collect { playlists ->
                _uiState.value = PlaylistsUiState(playlists = playlists, isLoading = false)
            }
        }
    }

    fun createPlaylist(name: String) = viewModelScope.launch { playlistRepository.createPlaylist(name) }
    fun deletePlaylist(playlist: PlaylistEntity) = viewModelScope.launch { playlistRepository.deletePlaylist(playlist) }

    fun playPlaylist(playlistId: Long) {
        viewModelScope.launch {
            playlistRepository.getSongIdsByPlaylist(playlistId).collect { ids ->
                musicRepository.getSongsByIds(ids).collect { songs ->
                    if (songs.isNotEmpty()) playerController.playSongs(songs, 0)
                }
            }
        }
    }

    fun togglePlayPause() { playerController.togglePlayPause() }
}
