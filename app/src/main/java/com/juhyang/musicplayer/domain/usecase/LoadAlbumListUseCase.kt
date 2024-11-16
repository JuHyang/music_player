package com.juhyang.musicplayer.domain.usecase

import com.juhyang.musicplayer.domain.model.Album
import kotlinx.coroutines.flow.Flow


interface LoadAlbumListUseCase {
    suspend fun execute(): Flow<List<Album>>
}
