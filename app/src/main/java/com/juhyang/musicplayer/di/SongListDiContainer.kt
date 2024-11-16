package com.juhyang.musicplayer.di

import android.content.Context
import com.juhyang.musicplayer.data.datasource.AlbumDataSource
import com.juhyang.musicplayer.data.datasource.AlbumLocalDataSource
import com.juhyang.musicplayer.data.repository.AlbumRepositoryImpl
import com.juhyang.musicplayer.domain.repository.AlbumRepository
import com.juhyang.musicplayer.domain.usecase.LoadAlbumUseCase
import com.juhyang.musicplayer.domain.usecase.LoadAlbumUseCaseImpl
import com.juhyang.musicplayer.presentation.SongListViewModel


class SongListDiContainer {
    fun createViewModel(context: Context): SongListViewModel {
        return SongListViewModel(createLoadAlbumUseCase(context))
    }

    private fun createLoadAlbumUseCase(context: Context): LoadAlbumUseCase {
        return LoadAlbumUseCaseImpl(createAlbumRepository(context))
    }

    private fun createAlbumRepository(context: Context): AlbumRepository {
        return AlbumRepositoryImpl(createAlbumLocalDataSource(context))
    }

    private fun createAlbumLocalDataSource(context: Context): AlbumDataSource {
        return AlbumLocalDataSource(context)
    }
}
