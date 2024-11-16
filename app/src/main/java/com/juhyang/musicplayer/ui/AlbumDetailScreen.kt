package com.juhyang.musicplayer.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.juhyang.musicplayer.domain.model.Song
import com.juhyang.musicplayer.presentation.SongListViewModel


@Composable
fun AlbumDetailScreen(viewModel: SongListViewModel, albumTitle: String, artist: String) {
    viewModel.setIntent(SongListViewModel.Intent.LoadAlbum(albumTitle, artist))
    handleSongListViewState(viewModel = viewModel)
}

@Composable
private fun handleSongListViewState(viewModel: SongListViewModel) {
    val viewState by viewModel.viewState.collectAsState()
    when (viewState) {
        is SongListViewModel.ViewState.Error -> {}
        is SongListViewModel.ViewState.Idle -> {}
        is SongListViewModel.ViewState.Loaded -> {
            SongListView(songs = (viewState as SongListViewModel.ViewState.Loaded).album.songs) {
                viewModel.setIntent(SongListViewModel.Intent.PlaySong(it))
            }
        }
    }
}

@Composable
private fun SongListView(songs: List<Song>, onSongClick: (Int) -> Unit) {
    LazyColumn(
        modifier = Modifier,
    ) {
        items(songs.size) { count ->
            SongItem(songs[count], count, onSongClick)
        }
    }
}

@Composable
private fun SongItem(song: Song, index: Int, onSongClick: (Int) -> Unit) {
    Column(
        modifier = Modifier.clickable { onSongClick(index) },
    ) {
        Text(text = song.title)
    }
}

