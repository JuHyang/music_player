package com.juhyang.musicplayer.domain.usecase

import com.juhyang.musicplayer.domain.model.Album
import com.juhyang.musicplayer.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow


class LoadAlbumUseCaseImpl(
    private val repository: AlbumRepository
): LoadAlbumUseCase {
    override suspend fun execute(): Flow<List<Album>> {
        return repository.getAlbumList()
    }
}
