package com.juhyang.musicplayer.domain.model


data class Song(
    val title: String,
    val filePath: String,
    val artistName: String,
    val duration: Long?
)
