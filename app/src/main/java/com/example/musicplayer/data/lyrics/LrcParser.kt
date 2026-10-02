package com.example.musicplayer.data.lyrics

object LrcParser {
    private val TIME_TAG_REGEX = Regex("\\[(\\d{1,2}):(\\d{2})(?:[.:](\\d{1,3}))?\\]")
    private val META_TAG_REGEX = Regex("\\[(ti|ar|al|by|offset):(.+?)\\]", RegexOption.IGNORE_CASE)

    fun parse(content: String): Lyrics {
        var title = ""; var artist = ""; var album = ""
        val lines = mutableListOf<LrcLine>()
        content.lineSequence().forEach { rawLine ->
            val line = rawLine.trim()
            if (line.isEmpty()) return@forEach
            META_TAG_REGEX.findAll(line).forEach { match ->
                when (match.groupValues[1].lowercase()) {
                    "ti" -> title = match.groupValues[2].trim()
                    "ar" -> artist = match.groupValues[2].trim()
                    "al" -> album = match.groupValues[2].trim()
                }
            }
            val timeMatches = TIME_TAG_REGEX.findAll(line).toList()
            if (timeMatches.isNotEmpty()) {
                val text = TIME_TAG_REGEX.replace(line, "").trim()
                timeMatches.forEach { match ->
                    val minutes = match.groupValues[1].toLongOrNull() ?: 0L
                    val seconds = match.groupValues[2].toLongOrNull() ?: 0L
                    val millisStr = match.groupValues[3]
                    val millis = when {
                        millisStr.isEmpty() -> 0L
                        millisStr.length == 1 -> millisStr.toLong() * 100
                        millisStr.length == 2 -> millisStr.toLong() * 10
                        else -> millisStr.take(3).toLong()
                    }
                    lines.add(LrcLine(minutes * 60_000 + seconds * 1_000 + millis, text))
                }
            }
        }
        lines.sortBy { it.timeMs }
        return Lyrics(title, artist, album, lines)
    }

    fun parseFile(file: java.io.File): Lyrics = try { parse(file.readText(Charsets.UTF_8)) } catch (e: Exception) { Lyrics() }
}
