package com.juhyang.musicplayer.internal.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juhyang.musicplayer.MusicPlayer
import com.juhyang.musicplayer.internal.model.PlayerState
import com.juhyang.musicplayer.internal.model.PlayingState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


internal class MusicPlayerViewModel(
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
        object TogglePlayList: Intent()
        class SeekTo(val position: Int): Intent()
    }

    sealed class ViewState {
        object MusicPlayer: ViewState()
        object PlayList: ViewState()
    }

    private val _intent: MutableStateFlow<Intent> = MutableStateFlow(Intent.Idle)
    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.MusicPlayer)
    val viewState: StateFlow<ViewState> = _viewState

    val playerState: StateFlow<PlayerState>
        get() { return musicPlayer.getPlayerState() }
    val playingState: StateFlow<PlayingState>
        get() { return musicPlayer.getPlayingState() }

    init {
        viewModelScope.launch(mainDispatcher) {
            _intent.collect { intent ->
                handleIntent(intent)
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
            is Intent.TogglePlayList -> {
                val newViewState  = if (_viewState.value == ViewState.MusicPlayer) {
                    ViewState.PlayList
                } else {
                    ViewState.MusicPlayer
                }
                setViewState(newViewState)
            }
        }
        _intent.value = Intent.Idle
    }
}
