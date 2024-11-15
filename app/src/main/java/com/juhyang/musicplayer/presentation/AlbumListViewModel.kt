package com.juhyang.musicplayer.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juhyang.musicplayer.domain.model.Album
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AlbumListViewModel(
    private val mainDispatcher:  CoroutineDispatcher = Dispatchers.Main,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
): ViewModel() {
    sealed class Action {
        object Idle: Action()
        object LoadAlbums: Action()
        class ClickAlbum(album: Album): Action()
    }

    sealed class ViewState {
        object Idle: ViewState()
        class Loaded(val albumList: List<Album>): ViewState()
        class MoveMusicList(val album: Album): ViewState()
    }

    private val _action: MutableStateFlow<Action> = MutableStateFlow(Action.Idle)
    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)
    val viewState: StateFlow<ViewState> = _viewState

    init {
        viewModelScope.launch {
            _action.collect { action ->
                handleAction(action)
            }
        }
    }

    fun setAction(action: Action) {
        viewModelScope.launch {
            _action.emit(action)
        }
    }

    private fun setViewState(viewState: ViewState) {
        viewModelScope.launch {
            _viewState.emit(viewState)
        }
    }

    private fun handleAction(action: Action) {
        when (action) {
            is Action.Idle -> {

            }
            is Action.LoadAlbums -> {

            }

            is Action.ClickAlbum -> {

            }
        }
    }
}
