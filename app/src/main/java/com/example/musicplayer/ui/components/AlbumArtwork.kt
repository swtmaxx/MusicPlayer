package com.example.musicplayer.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.musicplayer.data.model.Song

/**
 * 专辑封面组件
 */
@Composable
fun AlbumArtwork(
    song: Song?,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    cornerRadius: Dp = 8.dp
) {
    Surface(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius)),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        if (song != null && song.albumId > 0) {
            val artworkUri = Uri.parse(
                "content://media/external/audio/albumart/${song.albumId}"
            )
            AsyncImage(
                model = artworkUri,
                contentDescription = "专辑封面",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(size)
            )
        } else {
            Box(
                modifier = Modifier.size(size),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    modifier = Modifier.size(size * 0.5f),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }
        }
    }
}
