package com.juhyang.musicplayer.internal.model


enum class RepeatMode {
    ALL,
    ONE,
    OFF;

    fun next(): RepeatMode {
        return when (this) {
            ALL -> ONE
            ONE -> OFF
            OFF -> ALL
        }
    }
}
