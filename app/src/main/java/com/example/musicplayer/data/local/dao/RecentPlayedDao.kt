package com.example.musicplayer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.musicplayer.data.local.entity.RecentPlayedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentPlayedDao {
    @Query("SELECT songId FROM recent_played ORDER BY playedAt DESC LIMIT 50")
    fun getRecentSongIds(): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRecentPlayed(recent: RecentPlayedEntity)

    @Query("DELETE FROM recent_played WHERE songId NOT IN (SELECT songId FROM recent_played ORDER BY playedAt DESC LIMIT 100)")
    suspend fun cleanupOldRecords()
}
