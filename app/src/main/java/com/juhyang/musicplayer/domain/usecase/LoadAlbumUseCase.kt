package com.juhyang.musicplayer.domain.usecase

import com.juhyang.musicplayer.domain.model.Album
import kotlinx.coroutines.flow.Flow


interface LoadAlbumUseCase {
    suspend fun execute(): Flow<Album>
}
