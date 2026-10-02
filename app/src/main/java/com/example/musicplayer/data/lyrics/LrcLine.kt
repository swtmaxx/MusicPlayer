package com.example.musicplayer.data.lyrics

data class LrcLine(
    val timeMs: Long,
    val text: String
)

data class Lyrics(
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val lines: List<LrcLine> = emptyList()
) {
    val isEmpty: Boolean get() = lines.isEmpty

    fun getLineIndexAtTime(timeMs: Long): Int {
        if (lines.isEmpty()) return -1
        var low = 0; var high = lines.size - 1; var result = -1
        while (low <= high) {
            val mid = (low + high) / 2
            when {
                lines[mid].timeMs <= timeMs -> { result = mid; low = mid + 1 }
                else -> high = mid - 1
            }
        }
        return result
    }

    fun getLineAtTime(timeMs: Long): LrcLine? {
        val index = getLineIndexAtTime(timeMs)
        return if (index >= 0) lines[index] else null
    }
}
