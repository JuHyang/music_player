package com.juhyang.musicplayer.domain.model

import android.net.Uri
import com.juhyang.musicplayer.Song


data class Album (
    val title: String,
    val artist: String,
    val albumCoverUri: Uri?,
    val songs: List<Song>
)
