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
    sealed class Intent {
        object Idle: Intent()
        object OnResume: Intent()
        object GrantStoragePermission: Intent()
        object RevokeStoragePermission: Intent()
        class ClickAlbum(val album: Album): Intent()
    }

    sealed class ViewState {
        object Idle: ViewState()
        class Loaded(val albumList: List<Album>): ViewState()
        object ErrorPermissionDenied: ViewState()
        object ErrorEmptyAlbums: ViewState()
    }

    sealed class ViewAction {
        object Idle: ViewAction()
        class MoveMusicList(val album: Album): ViewAction()
        object RequestStoragePermission: ViewAction()
    }

    private val _intent: MutableStateFlow<Intent> = MutableStateFlow(Intent.Idle)
    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)
    val viewState: StateFlow<ViewState> = _viewState

    private val _viewAction: MutableStateFlow<ViewAction> = MutableStateFlow(ViewAction.Idle)
    val viewAction: StateFlow<ViewAction> = _viewAction

    init {
        viewModelScope.launch(mainDispatcher) {
            _intent.collect { action ->
                handleAction(action)
            }
        }
    }

    fun setAction(intent: Intent) {
        viewModelScope.launch(mainDispatcher) {
            _intent.emit(intent)
        }
    }

    private fun setViewState(viewState: ViewState) {
        viewModelScope.launch(mainDispatcher) {
            _viewState.emit(viewState)
        }
    }

    private fun setViewAction(viewAction: ViewAction) {
        viewModelScope.launch(mainDispatcher) {
            _viewAction.emit(viewAction)
        }
    }

    private fun handleAction(intent: Intent) {
        when (intent) {
            is Intent.Idle -> {}
            is Intent.OnResume -> {
                handleOnResume()
            }
            is Intent.GrantStoragePermission -> {
                loadAlbum()
            }
            is Intent.RevokeStoragePermission -> {
                setViewState(ViewState.ErrorPermissionDenied)
            }
            is Intent.ClickAlbum -> {
                handleClickAlbum(intent.album)
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
                        setViewAction(ViewAction.RequestStoragePermission)
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
        setViewAction(ViewAction.MoveMusicList(album))
    }
}
