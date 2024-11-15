package com.juhyang.musicplayer.data.model

import com.juhyang.musicplayer.domain.model.Song


class SongMapper {
    fun map(mp3Data: Mp3Data): Song {
        return Song(
            mp3Data.title,
            mp3Data.filePath,
            mp3Data.artistName,
            mp3Data.duration
        )
    }
}
