package com.juhyang.musicplayer.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juhyang.musicplayer.MusicPlayer
import com.juhyang.musicplayer.internal.model.PlayerState
import com.juhyang.musicplayer.internal.model.PlayingState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class MusicPlayerViewModel(
    private val musicPlayer: MusicPlayer,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {
    sealed class Intent {
        object Idle : Intent()
        class Play(val index: Int): Intent()
        object ClickPlayButton: Intent()
        object Previous: Intent()
        object Next: Intent()
        object ChangeShuffleMode: Intent()
        object ChangeRepeatMode: Intent()
        class SeekTo(val position: Int): Intent()
    }

    sealed class ViewState {
        object Idle : ViewState()
        class LoadPlayerState(val playerState: PlayerState): ViewState()
        class Playing(val currentPosition: Int): ViewState()
        class Paused(val position: Int): ViewState()
        class Stopped(val position: Int): ViewState()
    }

    sealed class ViewAction {
        object Idle : ViewAction()
    }

    private val _intent: MutableStateFlow<Intent> = MutableStateFlow(Intent.Idle)
    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)
    val viewState: StateFlow<ViewState> = _viewState

    private val _viewAction: MutableStateFlow<ViewAction> = MutableStateFlow(ViewAction.Idle)
    val viewAction: StateFlow<ViewAction> = _viewAction

    private val playerState: Flow<PlayerState>
        get() = musicPlayer.getPlayerState()
    private val playingState: Flow<PlayingState>
        get() = musicPlayer.getCurrentPlayingState()

    init {
        viewModelScope.launch(mainDispatcher) {
            _intent.collect { intent ->
                handleIntent(intent)
            }
        }

        viewModelScope.launch {
            playerState.collect { playerState ->
                setViewState(ViewState.LoadPlayerState(playerState))
            }
        }

        viewModelScope.launch {
            playingState.collect { playingState ->
                setViewState(ViewState.Playing(playingState.currentPosition))
            }
        }
    }

    fun setIntent(intent: Intent) {
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

    private fun handleIntent(intent: Intent) {
        when (intent) {
            is Intent.Idle -> {}
            is Intent.Play -> {
                musicPlayer.play(intent.index)
            }
            is Intent.Next -> {
                musicPlayer.skipToNext()
            }
            is Intent.ClickPlayButton -> {
                if (musicPlayer.isPlaying()) {
                    musicPlayer.pause()
                } else {
                    musicPlayer.resume()
                }
            }
            is Intent.Previous -> {
                musicPlayer.skipToPrevious()
            }
            is Intent.ChangeRepeatMode -> {
                musicPlayer.changeRepeatMode()
            }
            is Intent.SeekTo -> {
                musicPlayer.seekTo(intent.position)
            }
            is Intent.ChangeShuffleMode -> {
                musicPlayer.changeShuffleMode()
            }
        }
        _intent.value = Intent.Idle
    }
}
