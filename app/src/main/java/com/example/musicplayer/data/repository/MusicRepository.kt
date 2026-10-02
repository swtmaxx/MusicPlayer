package com.example.musicplayer.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.musicplayer.data.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getAllSongs(): Flow<List<Song>> = flow {
        val songs = mutableListOf<Song>()
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.SIZE
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"
        context.contentResolver.query(collection, projection, selection, null, sortOrder)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                songs.add(
                    Song(
                        id = id,
                        title = cursor.getString(titleColumn) ?: "未知标题",
                        artist = cursor.getString(artistColumn) ?: "未知艺术家",
                        album = cursor.getString(albumColumn) ?: "未知专辑",
                        albumId = cursor.getLong(albumIdColumn),
                        duration = cursor.getLong(durationColumn),
                        path = cursor.getString(dataColumn) ?: "",
                        uri = uri,
                        dateAdded = cursor.getLong(dateAddedColumn),
                        size = cursor.getLong(sizeColumn)
                    )
                )
            }
        }
        emit(songs)
    }.flowOn(Dispatchers.IO)

    fun getSongsByIds(ids: List<Long>): Flow<List<Song>> = flow {
        if (ids.isEmpty()) { emit(emptyList()); return@flow }
        val allSongs = mutableListOf<Song>()
        getAllSongs().collect { allSongs.addAll(it) }
        val idToSong = allSongs.associateBy { it.id }
        emit(ids.mapNotNull { idToSong[it] })
    }.flowOn(Dispatchers.IO)

    fun searchSongs(query: String): Flow<List<Song>> = flow {
        if (query.isBlank()) { emit(emptyList()); return@flow }
        getAllSongs().collect { songs ->
            val lowerQuery = query.lowercase()
            emit(songs.filter {
                it.title.lowercase().contains(lowerQuery) ||
                it.artist.lowercase().contains(lowerQuery) ||
                it.album.lowercase().contains(lowerQuery)
            })
        }
    }.flowOn(Dispatchers.IO)
}
