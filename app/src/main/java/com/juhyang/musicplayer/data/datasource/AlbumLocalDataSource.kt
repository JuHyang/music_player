package com.juhyang.musicplayer.data.datasource

import com.juhyang.musicplayer.domain.model.Album
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class AlbumLocalDataSource: AlbumDataSource {
    override suspend fun getAlbumList(): Flow<List<Album>> {
        return flowOf(emptyList())
    }
}
