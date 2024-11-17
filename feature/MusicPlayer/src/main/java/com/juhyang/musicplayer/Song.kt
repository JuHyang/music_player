package com.juhyang.musicplayer

import android.net.Uri


data class Song(
    val title: String,
    val filePath: String,
    val albumCoverUri: Uri?,
    val artistName: String,
    val duration: Long?
)
