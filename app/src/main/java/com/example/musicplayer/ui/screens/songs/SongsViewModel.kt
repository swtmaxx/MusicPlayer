package com.example.musicplayer.ui.screens.songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.data.repository.MusicRepository
import com.example.musicplayer.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SongsUiState(val songs: List<Song> = emptyList(), val isLoading: Boolean = true, val searchQuery: String = "")

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val playerController: PlayerController
) : ViewModel() {
    private val _uiState = MutableStateFlow(SongsUiState())
    val uiState: StateFlow<SongsUiState> = _uiState.asStateFlow()
    val currentSong = playerController.currentSong
    val isPlaying = playerController.isPlaying

    init { loadSongs() }

    fun loadSongs() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            musicRepository.getAllSongs().collect { songs ->
                _uiState.value = _uiState.value.copy(songs = songs, isLoading = false)
            }
        }
    }

    fun searchSongs(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.isBlank()) loadSongs()
        else viewModelScope.launch {
            musicRepository.searchSongs(query).collect { songs ->
                _uiState.value = _uiState.value.copy(songs = songs)
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
