package com.example.musicplayer.ui.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.lyrics.Lyrics
import com.example.musicplayer.data.lyrics.LyricsLoader
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.data.repository.PlaylistRepository
import com.example.musicplayer.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val lyrics: Lyrics = Lyrics(),
    val isFavorite: Boolean = false,
    val currentLyricIndex: Int = -1
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerController: PlayerController,
    private val lyricsLoader: LyricsLoader,
    private val playlistRepository: PlaylistRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()
    val currentSong = playerController.currentSong
    val isPlaying = playerController.isPlaying
    val currentPosition = playerController.currentPosition
    val duration = playerController.duration
    val playbackMode = playerController.playbackMode

    init {
        viewModelScope.launch {
            currentSong.collect { song ->
                if (song != null && !song.isEmpty) {
                    loadLyrics(song)
                    checkFavorite(song.id)
                } else _uiState.value = _uiState.value.copy(lyrics = Lyrics())
            }
        }
        viewModelScope.launch {
            currentPosition.collect { position ->
                val lyrics = _uiState.value.lyrics
                if (!lyrics.isEmpty) {
                    val index = lyrics.getLineIndexAtTime(position)
                    if (index != _uiState.value.currentLyricIndex) {
                        _uiState.value = _uiState.value.copy(currentLyricIndex = index)
                    }
                }
            }
        }
    }

    private fun loadLyrics(song: Song) = viewModelScope.launch {
        val lyrics = lyricsLoader.loadLyrics(song)
        _uiState.value = _uiState.value.copy(lyrics = lyrics, currentLyricIndex = -1)
    }

    private fun checkFavorite(songId: Long) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isFavorite = playlistRepository.isFavorite(songId))
    }

    fun togglePlayPause() = playerController.togglePlayPause()
    fun playNext() = playerController.playNext()
    fun playPrevious() = playerController.playPrevious()
    fun seekTo(positionMs: Long) = playerController.seekTo(positionMs)
    fun togglePlaybackMode() = playerController.togglePlaybackMode()

    fun toggleFavorite() {
        val song = currentSong.value ?: return
        viewModelScope.launch {
            val isFav = playlistRepository.toggleFavorite(song.id)
            _uiState.value = _uiState.value.copy(isFavorite = isFav)
        }
    }

    fun jumpToLyricLine(index: Int) {
        val lyrics = _uiState.value.lyrics
        if (index in lyrics.lines.indices) playerController.seekTo(lyrics.lines[index].timeMs)
    }
}
