package com.juhyang.musicplayer.internal.model

import com.juhyang.musicplayer.Song


data class PlayingState (
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Int = 0,
    val totalDuration: Int = 0,
)
