package com.example.musicplayer.player

enum class PlaybackMode {
    REPEAT_ALL, REPEAT_ONE, SHUFFLE;
    fun next(): PlaybackMode = when (this) {
        REPEAT_ALL -> SHUFFLE
        SHUFFLE -> REPEAT_ONE
        REPEAT_ONE -> REPEAT_ALL
    }
}
