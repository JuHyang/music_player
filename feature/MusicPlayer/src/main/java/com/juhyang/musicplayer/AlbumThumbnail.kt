package com.juhyang.musicplayer

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage


@Composable
fun AlbumThumbnail(albumThumbnail: Uri?, modifier: Modifier) {
    Column {
        if (albumThumbnail == null) {
            Spacer(modifier = modifier)
        } else {
            AsyncImage(
                model = albumThumbnail,
                contentDescription = "Album Thumbnail",
                modifier = modifier
            )
        }
    }
}
