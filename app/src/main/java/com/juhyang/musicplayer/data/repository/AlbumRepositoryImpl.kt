package com.juhyang.musicplayer.data.repository

import com.juhyang.musicplayer.data.datasource.AlbumDataSource
import com.juhyang.musicplayer.domain.model.Album
import com.juhyang.musicplayer.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow


class AlbumRepositoryImpl(
    private val albumLocalDataSource: AlbumDataSource
) : AlbumRepository {
    private var albumMemoryCache: List<Album>? = null
    override suspend fun getAlbumList(refresh: Boolean): Flow<List<Album>> {
        if(refresh) {
            clearCache()
        }

        return flow {
            // 캐시에 데이터가 있으면 바로 방출
            albumMemoryCache?.let { cachedAlbums ->
                emit(cachedAlbums)
                return@flow
            }

            // 캐시에 데이터가 없으면 로컬 데이터 소스에서 가져오기
            val albums = albumLocalDataSource.getAlbumList().first() // 첫 번째 데이터 가져오기
            albumMemoryCache = albums // 캐시에 저장
            emit(albums) // 결과 방출
        }
    }

    override suspend fun getAlbum(albumTitle: String, artist: String, refresh: Boolean): Flow<Album> {
        if(refresh) {
            clearCache()
        }
        return flow {
            // 캐시 확인 및 로드
            if (albumMemoryCache == null) {
                // 캐시가 비어 있으면 로컬 데이터 소스에서 로드
                albumMemoryCache = albumLocalDataSource.getAlbumList().first()
            }

            // 앨범 검색
            val album = albumMemoryCache!!.find { it.title == albumTitle && it.artist == artist }
                ?: throw NoSuchElementException("Album not found: $albumTitle by $artist")

            emit(album)
        }
    }

    private fun clearCache() {
        albumMemoryCache = null
    }
}
