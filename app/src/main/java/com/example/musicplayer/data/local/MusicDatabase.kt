package com.example.musicplayer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.musicplayer.data.local.dao.FavoriteDao
import com.example.musicplayer.data.local.dao.PlaylistDao
import com.example.musicplayer.data.local.dao.PlaylistSongDao
import com.example.musicplayer.data.local.dao.RecentPlayedDao
import com.example.musicplayer.data.local.entity.FavoriteEntity
import com.example.musicplayer.data.local.entity.PlaylistEntity
import com.example.musicplayer.data.local.entity.PlaylistSongCrossRef
import com.example.musicplayer.data.local.entity.RecentPlayedEntity

@Database(
    entities = [
        PlaylistEntity::class,
        PlaylistSongCrossRef::class,
        FavoriteEntity::class,
        RecentPlayedEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistSongDao(): PlaylistSongDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recentPlayedDao(): RecentPlayedDao

    companion object {
        const val DATABASE_NAME = "music_player.db"
    }
}
