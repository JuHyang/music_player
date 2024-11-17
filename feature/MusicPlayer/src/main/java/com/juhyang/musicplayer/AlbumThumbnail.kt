package com.juhyang.musicplayer

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage


@Composable
fun AlbumThumbnail(albumThumbnail: Uri?, modifier: Modifier) {
    if (albumThumbnail == null) {
    } else {
        AsyncImage(
            model = albumThumbnail,
            contentDescription = "Album Thumbnail",
            modifier = modifier
        )
    }
}
