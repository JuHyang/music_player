package com.juhyang.musicplayer.internal.model

import com.juhyang.musicplayer.Song


data class PlayerState(
    val currentSong: Song? = null,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val shuffleMode: ShuffleMode = ShuffleMode.OFF,
)
