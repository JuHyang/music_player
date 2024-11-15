package com.juhyang.musicplayer.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juhyang.musicplayer.domain.model.Album
import com.juhyang.musicplayer.domain.model.PermissionStatus
import com.juhyang.musicplayer.domain.usecase.CheckStoragePermissionUseCase
import com.juhyang.musicplayer.domain.usecase.LoadAlbumUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AlbumListViewModel(
    private val loadAlbumUseCase: LoadAlbumUseCase,
    private val checkStoragePermissionUseCase: CheckStoragePermissionUseCase,
    private val mainDispatcher:  CoroutineDispatcher = Dispatchers.Main,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
): ViewModel() {
    sealed class Action {
        object Idle: Action()
        object OnResume: Action()
        object GrantStoragePermission: Action()
        object RevokeStoragePermission: Action()
        class ClickAlbum(val album: Album): Action()
    }

    sealed class ViewState {
        object Idle: ViewState()
        object RequestStoragePermission: ViewState()
        class Loaded(val albumList: List<Album>): ViewState()
        class MoveMusicList(val album: Album): ViewState()
        object ErrorPermissionDenied: ViewState()
        object ErrorEmptyAlbums: ViewState()
    }

    private val _action: MutableStateFlow<Action> = MutableStateFlow(Action.Idle)
    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)
    val viewState: StateFlow<ViewState> = _viewState

    init {
        viewModelScope.launch(mainDispatcher) {
            _action.collect { action ->
                handleAction(action)
            }
        }
    }

    fun setAction(action: Action) {
        viewModelScope.launch(mainDispatcher) {
            _action.emit(action)
        }
    }

    private fun setViewState(viewState: ViewState) {
        viewModelScope.launch(mainDispatcher) {
            _viewState.emit(viewState)
        }
    }

    private fun handleAction(action: Action) {
        when (action) {
            is Action.Idle -> {}
            is Action.OnResume -> {
                handleOnResume()
            }
            is Action.GrantStoragePermission -> {
                loadAlbum()
            }
            is Action.RevokeStoragePermission -> {
                setViewState(ViewState.ErrorPermissionDenied)
            }
            is Action.ClickAlbum -> {
                handleClickAlbum(action.album)
            }
        }
    }

    private fun handleOnResume() {
        viewModelScope.launch {
            checkStoragePermissionUseCase.execute().collect {
                when (it) {
                    PermissionStatus.GRANTED -> {
                        loadAlbum()
                    }
                    PermissionStatus.REVOKED -> {
                        setViewState(ViewState.RequestStoragePermission)
                    }
                }
            }
        }
    }

    private fun loadAlbum() {
        viewModelScope.launch(ioDispatcher) {
            loadAlbumUseCase.execute().collect {
                if (it.isEmpty()) {
                    setViewState(ViewState.ErrorEmptyAlbums)
                } else {
                    setViewState(ViewState.Loaded(it))
                }
            }
        }
    }

    private fun handleClickAlbum(album: Album) {
        setViewState(ViewState.MoveMusicList(album))
    }
}
