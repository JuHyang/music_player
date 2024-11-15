package com.juhyang.musicplayer.domain.repository

import com.juhyang.musicplayer.domain.model.Album
import kotlinx.coroutines.flow.Flow


interface AlbumRepository {
    suspend fun getAlbumList(): Flow<List<Album>>
}
