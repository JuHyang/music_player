package com.juhyang.musicplayer.domain.usecase

import com.juhyang.musicplayer.domain.model.Album
import com.juhyang.musicplayer.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow


class LoadAlbumListUseCaseImpl(
    private val repository: AlbumRepository
): LoadAlbumListUseCase {
    override suspend fun execute(): Flow<List<Album>> {
        return repository.getAlbumList()
    }
}
