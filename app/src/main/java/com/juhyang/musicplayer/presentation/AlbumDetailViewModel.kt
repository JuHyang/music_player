package com.juhyang.musicplayer.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juhyang.musicplayer.MusicPlayer
import com.juhyang.musicplayer.Song
import com.juhyang.musicplayer.domain.model.Album
import com.juhyang.musicplayer.domain.usecase.LoadAlbumUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AlbumDetailViewModel(
    private val musicPlayer: MusicPlayer,
    private val loadAlbumUseCase: LoadAlbumUseCase,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {
    sealed class Intent {
        object Idle: Intent()
        class LoadAlbum(val albumTitle: String, val artist: String): Intent()
        object PlayAll: Intent()
        class PlaySong(val index: Int): Intent()
        object PlayRandom: Intent()
        class AddPlayList(val index: Int): Intent()
    }

    sealed class ViewState {
        object Idle: ViewState()
        class Loaded(val album: Album): ViewState()
    }

    private val _intent: MutableSharedFlow<Intent> = MutableSharedFlow(replay = 1)
    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Idle)
    val viewState: StateFlow<ViewState> = _viewState

    private var album: Album? = null

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
            is Intent.LoadAlbum -> {
                handleLoadSongs(intent.albumTitle, intent.artist)
            }
            is Intent.PlayAll -> {
                handlePlayAll()
            }
            is Intent.PlayRandom -> {
                handlePlayRandom()
            }
            is Intent.PlaySong -> {
                handlePlaySong(intent.index)
            }
            is Intent.AddPlayList -> {
                handleAddPlayList(intent.index)
            }
        }
    }

    private fun handleLoadSongs(albumTitle: String, artist: String) {
        viewModelScope.launch(ioDispatcher) {
            loadAlbumUseCase.execute(albumTitle, artist).collect {
                album = it
                setViewState(ViewState.Loaded(it))
            }
        }
    }

    private fun handlePlayAll() {
        album?.let {
            musicPlayer.play(it.songs)
        }
    }

    private fun handlePlayRandom() {
        album?.let {
            val mutableSongs: MutableList<Song> = it.songs.toMutableList()
            musicPlayer.play(mutableSongs.shuffled())
        }
    }

    private fun handlePlaySong(index: Int) {
        album?.let {
            musicPlayer.play(it.songs.slice(index until it.songs.size))
        }
    }

    private fun handleAddPlayList(index: Int) {
        album?.let {
            musicPlayer.addPlayList(it.songs[index])
        }
    }
}
