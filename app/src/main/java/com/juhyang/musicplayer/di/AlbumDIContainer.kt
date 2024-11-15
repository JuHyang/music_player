package com.juhyang.musicplayer.di

import android.content.Context
import com.juhyang.musicplayer.data.datasource.AlbumDataSource
import com.juhyang.musicplayer.data.datasource.AlbumLocalDataSource
import com.juhyang.musicplayer.data.repository.AlbumRepositoryImpl
import com.juhyang.musicplayer.data.repository.PermissionRepositoryImpl
import com.juhyang.musicplayer.domain.repository.AlbumRepository
import com.juhyang.musicplayer.domain.repository.PermissionRepository
import com.juhyang.musicplayer.domain.usecase.CheckStoragePermissionUseCase
import com.juhyang.musicplayer.domain.usecase.CheckStoragePermissionUseCaseImpl
import com.juhyang.musicplayer.domain.usecase.LoadAlbumUseCase
import com.juhyang.musicplayer.domain.usecase.LoadAlbumUseCaseImpl
import com.juhyang.musicplayer.presentation.AlbumListViewModel


class AlbumDIContainer {
    fun createViewModel(context: Context): AlbumListViewModel {
        return AlbumListViewModel(createLoadAlbumUseCase(), createCheckStoragePermissionUseCase(context))
    }

    private fun createLoadAlbumUseCase(): LoadAlbumUseCase {
        return LoadAlbumUseCaseImpl(createAlbumRepository())
    }

    private fun createAlbumRepository(): AlbumRepository {
        return AlbumRepositoryImpl(createAlbumLocalDataSource())
    }

    private fun createAlbumLocalDataSource(): AlbumDataSource {
        return AlbumLocalDataSource()
    }

    private fun createCheckStoragePermissionUseCase(context: Context): CheckStoragePermissionUseCase {
        return CheckStoragePermissionUseCaseImpl(createPermissionRepository(context))
    }

    private fun createPermissionRepository(context: Context): PermissionRepository {
        return PermissionRepositoryImpl(context)
    }
}
