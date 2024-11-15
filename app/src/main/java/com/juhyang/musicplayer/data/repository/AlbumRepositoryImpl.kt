package com.juhyang.musicplayer.data.repository

import com.juhyang.musicplayer.data.datasource.AlbumDataSource
import com.juhyang.musicplayer.domain.model.Album
import com.juhyang.musicplayer.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow


class AlbumRepositoryImpl(
    private val albumLocalDataSource: AlbumDataSource
) : AlbumRepository {
    override suspend fun getAlbumList(): Flow<List<Album>> {
        return albumLocalDataSource.getAlbumList()
    }
}
