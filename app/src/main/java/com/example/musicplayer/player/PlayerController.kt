package com.example.musicplayer.player

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.data.repository.PlaylistRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerController @Inject constructor(
    @ApplicationContext private val context: Context,
    private val queueManager: QueueManager,
    private val playlistRepository: PlaylistRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var progressJob: Job? = null

    private val _player = MutableStateFlow<ExoPlayer?>(null)
    val player: StateFlow<ExoPlayer?> = _player.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _playbackMode = MutableStateFlow(PlaybackMode.REPEAT_ALL)
    val playbackMode: StateFlow<PlaybackMode> = _playbackMode.asStateFlow()

    private val _queue = MutableStateFlow<List<Song>>(emptyList())
    val queue: StateFlow<List<Song>> = _queue.asStateFlow()

    private val _currentIndex = MutableStateFlow(-1)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private var exoPlayer: ExoPlayer? = null

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(playing: Boolean) {
            _isPlaying.value = playing
            if (playing) startProgressTracking() else stopProgressTracking()
        }
        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
                if (queueManager.shouldAutoNext(_playbackMode.value)) playNext()
                else { exoPlayer?.seekTo(0); exoPlayer?.play() }
            }
        }
    }

    fun initialize() {
        if (exoPlayer != null) return
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            setAudioAttributes(AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MUSIC).build(), true)
            addListener(playerListener)
            _player.value = this
        }
    }

    fun release() {
        stopProgressTracking()
        exoPlayer?.removeListener(playerListener)
        exoPlayer?.release()
        exoPlayer = null; _player.value = null
    }

    fun playSongs(songs: List<Song>, startIndex: Int = 0) {
        if (songs.isEmpty()) return
        queueManager.setQueue(songs, startIndex)
        _queue.value = queueManager.currentQueue
        _currentIndex.value = queueManager.currentPosition
        val song = queueManager.currentSong ?: return
        playSongInternal(song)
    }

    fun togglePlayPause() {
        val p = exoPlayer ?: return
        if (p.isPlaying) p.pause() else p.play()
    }

    fun playNext() {
        val song = queueManager.next(_playbackMode.value) ?: return
        _currentIndex.value = queueManager.currentPosition
        playSongInternal(song)
    }

    fun playPrevious() {
        val p = exoPlayer
        if (p != null && p.currentPosition > 3000) { p.seekTo(0); return }
        val song = queueManager.previous(_playbackMode.value) ?: return
        _currentIndex.value = queueManager.currentPosition
        playSongInternal(song)
    }

    fun seekTo(positionMs: Long) { exoPlayer?.seekTo(positionMs); _currentPosition.value = positionMs }

    fun togglePlaybackMode() { _playbackMode.value = _playbackMode.value.next() }

    fun removeFromQueue(index: Int) {
        if (queueManager.removeSong(index)) {
            _queue.value = queueManager.currentQueue
            _currentIndex.value = queueManager.currentPosition
            if (index == _currentIndex.value) {
                val song = queueManager.currentSong
                if (song != null) playSongInternal(song)
                else { exoPlayer?.stop(); _currentSong.value = null }
            }
        }
    }

    private fun playSongInternal(song: Song) {
        val p = exoPlayer ?: return
        _currentSong.value = song
        _duration.value = song.duration
        _currentPosition.value = 0
        p.setMediaItem(MediaItem.fromUri(song.uri))
        p.prepare(); p.play()
        scope.launch(Dispatchers.IO) { playlistRepository.addToRecentPlayed(song.id) }
    }

    private fun startProgressTracking() {
        stopProgressTracking()
        progressJob = scope.launch {
            while (true) {
                exoPlayer?.let {
                    _currentPosition.value = it.currentPosition
                    _duration.value = it.duration.coerceAtLeast(0)
                }
                delay(500)
            }
        }
    }

    private fun stopProgressTracking() { progressJob?.cancel(); progressJob = null }
}
