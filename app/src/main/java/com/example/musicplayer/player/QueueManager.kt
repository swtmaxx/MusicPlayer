package com.example.musicplayer.player

import com.example.musicplayer.data.model.Song
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QueueManager @Inject constructor() {
    private val queue = mutableListOf<Song>()
    private var currentIndex = -1
    private var shuffleOrder = listOf<Int>()

    val currentSong: Song? get() = if (currentIndex in queue.indices) queue[currentIndex] else null
    val currentQueue: List<Song> get() = queue.toList()
    val currentPosition: Int get() = currentIndex
    val queueSize: Int get() = queue.size

    fun setQueue(songs: List<Song>, startIndex: Int = 0) {
        queue.clear(); queue.addAll(songs)
        currentIndex = if (startIndex in songs.indices) startIndex else 0
        rebuildShuffleOrder()
    }

    fun addSong(song: Song) { queue.add(song); rebuildShuffleOrder() }

    fun removeSong(index: Int): Boolean {
        if (index !in queue.indices) return false
        queue.removeAt(index)
        if (index < currentIndex) currentIndex--
        else if (index == currentIndex) currentIndex = if (currentIndex >= queue.size) queue.size - 1 else currentIndex
        rebuildShuffleOrder()
        return true
    }

    fun next(mode: PlaybackMode): Song? {
        if (queue.isEmpty()) return null
        currentIndex = when (mode) {
            PlaybackMode.SHUFFLE -> {
                val cur = shuffleOrder.indexOf(currentIndex)
                shuffleOrder[(cur + 1) % shuffleOrder.size]
            }
            else -> (currentIndex + 1) % queue.size
        }
        return currentSong
    }

    fun previous(mode: PlaybackMode): Song? {
        if (queue.isEmpty()) return null
        currentIndex = when (mode) {
            PlaybackMode.SHUFFLE -> {
                val cur = shuffleOrder.indexOf(currentIndex)
                shuffleOrder[if (cur - 1 < 0) shuffleOrder.size - 1 else cur - 1]
            }
            else -> if (currentIndex - 1 < 0) queue.size - 1 else currentIndex - 1
        }
        return currentSong
    }

    fun jumpTo(index: Int): Song? {
        if (index in queue.indices) currentIndex = index
        return currentSong
    }

    fun shouldAutoNext(mode: PlaybackMode): Boolean = mode != PlaybackMode.REPEAT_ONE

    fun clear() { queue.clear(); currentIndex = -1; shuffleOrder = emptyList() }

    private fun rebuildShuffleOrder() { shuffleOrder = queue.indices.shuffled() }
}
