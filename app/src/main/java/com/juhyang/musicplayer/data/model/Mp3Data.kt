package com.juhyang.musicplayer.data.model

import android.net.Uri


data class Mp3Data(
    val title: String,
    val filePath: String,
    val albumTitle: String,
    val artistName: String,
    val albumCoverUri: Uri?,
    val duration: Long?
)
