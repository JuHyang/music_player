package com.juhyang.musicplayer.domain.usecase

import com.juhyang.musicplayer.domain.model.Album
import com.juhyang.musicplayer.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow


class LoadAlbumUseCaseImpl(
    private val albumRepository: AlbumRepository
): LoadAlbumUseCase {
    override suspend fun execute(albumTitle: String, artist: String): Flow<Album> {
        return albumRepository.getAlbum(albumTitle, artist)
    }
}
