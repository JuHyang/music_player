package com.juhyang.musicplayer.internal.model


enum class ShuffleMode {
    ON,
    OFF;

    fun toggle(): ShuffleMode {
        return when (this) {
            ON -> OFF
            OFF -> ON
        }
    }
}
