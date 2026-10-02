package com.example.musicplayer.data.lyrics

import com.example.musicplayer.data.model.Song
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LyricsLoader @Inject constructor() {
    fun loadLyrics(song: Song): Lyrics {
        if (song.path.isEmpty()) return Lyrics()
        val audioFile = File(song.path)
        if (!audioFile.exists()) return Lyrics()
        val lrcFile = File(audioFile.parentFile, replaceExtension(audioFile.name, "lrc"))
        if (lrcFile.exists()) return LrcParser.parseFile(lrcFile)
        audioFile.parentFile?.listFiles { _, name ->
            name.endsWith(".lrc", ignoreCase = true) && name.contains(audioFile.nameWithoutExtension, ignoreCase = true)
        }?.firstOrNull()?.let { return LrcParser.parseFile(it) }
        return Lyrics()
    }

    private fun replaceExtension(fileName: String, newExt: String): String {
        val dotIndex = fileName.lastIndexOf('.')
        return if (dotIndex >= 0) fileName.substring(0, dotIndex + 1) + newExt else "$fileName.$newExt"
    }
}
