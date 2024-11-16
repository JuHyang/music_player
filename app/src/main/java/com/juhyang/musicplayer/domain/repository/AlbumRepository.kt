package com.juhyang.musicplayer.domain.repository

import com.juhyang.musicplayer.domain.model.Album
import kotlinx.coroutines.flow.Flow


interface AlbumRepository {
    suspend fun getAlbumList(refresh: Boolean = false): Flow<List<Album>>
    suspend fun getAlbum(albumTitle: String, artist: String, refresh: Boolean = false): Flow<Album>
}
