package com.example.musicplayer.di

import android.content.Context
import androidx.room.Room
import com.example.musicplayer.data.local.MusicDatabase
import com.example.musicplayer.data.local.dao.FavoriteDao
import com.example.musicplayer.data.local.dao.PlaylistDao
import com.example.musicplayer.data.local.dao.PlaylistSongDao
import com.example.musicplayer.data.local.dao.RecentPlayedDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MusicDatabase {
        return Room.databaseBuilder(context, MusicDatabase::class.java, MusicDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }
    @Provides fun providePlaylistDao(db: MusicDatabase) = db.playlistDao()
    @Provides fun providePlaylistSongDao(db: MusicDatabase) = db.playlistSongDao()
    @Provides fun provideFavoriteDao(db: MusicDatabase) = db.favoriteDao()
    @Provides fun provideRecentPlayedDao(db: MusicDatabase) = db.recentPlayedDao()
}
