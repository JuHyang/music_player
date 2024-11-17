package com.juhyang.musicplayer.internal.model

import com.juhyang.musicplayer.Song


data class PlayerState(
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val shuffleMode: ShuffleMode = ShuffleMode.OFF,
    val playList: List<Song> = emptyList(),
    val currentPlayingSongIndex: Int = 0
)
