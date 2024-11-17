package com.juhyang.musicplayer.internal.model


data class PlayerState(
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val shuffleMode: ShuffleMode = ShuffleMode.OFF,
)
