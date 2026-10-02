package com.example.musicplayer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_played")
data class RecentPlayedEntity(
    @PrimaryKey
    val songId: Long,
    val playedAt: Long = System.currentTimeMillis()
)
