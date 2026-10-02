package com.example.musicplayer.data.model

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val duration: Long,
    val path: String,
    val uri: Uri,
    val dateAdded: Long,
    val size: Long
) {
    companion object {
        val EMPTY = Song(
            id = -1, title = "", artist = "", album = "", albumId = -1,
            duration = 0, path = "", uri = Uri.EMPTY, dateAdded = 0, size = 0
        )
    }
    val isEmpty: Boolean get() = id == -1L
}
