package com.example.musicplayer.data.repository

import com.example.musicplayer.data.local.dao.FavoriteDao
import com.example.musicplayer.data.local.dao.PlaylistDao
import com.example.musicplayer.data.local.dao.PlaylistSongDao
import com.example.musicplayer.data.local.dao.RecentPlayedDao
import com.example.musicplayer.data.local.entity.FavoriteEntity
import com.example.musicplayer.data.local.entity.PlaylistEntity
import com.example.musicplayer.data.local.entity.PlaylistSongCrossRef
import com.example.musicplayer.data.local.entity.RecentPlayedEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepository @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val playlistSongDao: PlaylistSongDao,
    private val favoriteDao: FavoriteDao,
    private val recentPlayedDao: RecentPlayedDao
) {
    fun getAllPlaylists(): Flow<List<PlaylistEntity>> = playlistDao.getAllPlaylists()

    suspend fun createPlaylist(name: String): Long = playlistDao.insertPlaylist(PlaylistEntity(name = name))

    suspend fun deletePlaylist(playlist: PlaylistEntity) = playlistDao.deletePlaylist(playlist)

    fun getSongIdsByPlaylist(playlistId: Long): Flow<List<Long>> = playlistSongDao.getSongIdsByPlaylist(playlistId)

    suspend fun addSongToPlaylist(playlistId: Long, songId: Long) {
        val maxPos = playlistSongDao.getMaxPosition(playlistId) ?: 0
        playlistSongDao.insertSongToPlaylist(PlaylistSongCrossRef(playlistId = playlistId, songId = songId, position = maxPos + 1))
        playlistDao.incrementSongCount(playlistId)
    }

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        playlistSongDao.removeSongFromPlaylist(playlistId, songId)
        playlistDao.decrementSongCount(playlistId)
    }

    fun getFavoriteSongIds(): Flow<List<Long>> = favoriteDao.getFavoriteSongIds()

    suspend fun isFavorite(songId: Long): Boolean = favoriteDao.isFavorite(songId) > 0

    suspend fun toggleFavorite(songId: Long): Boolean {
        return if (isFavorite(songId)) {
            favoriteDao.removeFavorite(songId); false
        } else {
            favoriteDao.addFavorite(FavoriteEntity(songId = songId)); true
        }
    }

    fun getRecentSongIds(): Flow<List<Long>> = recentPlayedDao.getRecentSongIds()

    suspend fun addToRecentPlayed(songId: Long) {
        recentPlayedDao.addRecentPlayed(RecentPlayedEntity(songId = songId))
        recentPlayedDao.cleanupOldRecords()
    }
}
