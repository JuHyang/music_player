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
import com.juhyang.musicplayer.domain.usecase.LoadAlbumListUseCase
import com.juhyang.musicplayer.domain.usecase.LoadAlbumListUseCaseImpl
import com.juhyang.musicplayer.presentation.AlbumListViewModel


class AlbumDIContainer {
    fun createViewModel(context: Context): AlbumListViewModel {
        return AlbumListViewModel(createLoadAlbumUseCase(context), createCheckStoragePermissionUseCase(context))
    }

    private fun createLoadAlbumUseCase(context: Context): LoadAlbumListUseCase {
        return LoadAlbumListUseCaseImpl(createAlbumRepository(context))
    }

    private fun createAlbumRepository(context: Context): AlbumRepository {
        return AlbumRepositoryImpl(createAlbumLocalDataSource(context))
    }

    private fun createAlbumLocalDataSource(context: Context): AlbumDataSource {
        return AlbumLocalDataSource(context)
    }

    private fun createCheckStoragePermissionUseCase(context: Context): CheckStoragePermissionUseCase {
        return CheckStoragePermissionUseCaseImpl(createPermissionRepository(context))
    }

    private fun createPermissionRepository(context: Context): PermissionRepository {
        return PermissionRepositoryImpl(context)
    }
}
