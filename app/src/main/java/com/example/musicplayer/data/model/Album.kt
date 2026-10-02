package com.example.musicplayer.data.model

import android.net.Uri

data class Album(
    val id: Long,
    val title: String,
    val artist: String,
    val songCount: Int,
    val year: Int,
    val artworkUri: Uri
)
