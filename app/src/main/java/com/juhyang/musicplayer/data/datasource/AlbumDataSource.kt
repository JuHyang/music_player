package com.juhyang.musicplayer.data.datasource

import com.juhyang.musicplayer.domain.model.Album
import kotlinx.coroutines.flow.Flow


interface AlbumDataSource {
    suspend fun getAlbumList(): Flow<List<Album>>
}
