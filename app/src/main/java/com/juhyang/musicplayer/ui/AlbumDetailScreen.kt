package com.juhyang.musicplayer.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.juhyang.musicplayer.AlbumThumbnail
import com.juhyang.musicplayer.Song
import com.juhyang.musicplayer.domain.model.Album
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
            SongListView((viewState as SongListViewModel.ViewState.Loaded).album) {
                viewModel.setIntent(SongListViewModel.Intent.PlaySong(it))
            }
        }
    }
}

@Composable
private fun SongListView(album: Album, onSongClick: (Int) -> Unit) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row {
            AlbumThumbnail(album.albumCoverUri, Modifier)

            Column {
                Text(
                    text = album.title,
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = album.artist,
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        LazyColumn(
            modifier = Modifier.padding(16.dp),
        ) {
            items(album.songs.size) { count ->
                SongItem(album.songs[count], count, onSongClick)
            }
        }
    }
}

@Composable
private fun SongItem(song: Song, index: Int, onSongClick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onSongClick(index) },
    ) {
        Text(
            text = (index + 1).toString(),
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = song.title,
            modifier = Modifier.padding(8.dp).fillMaxWidth()
        )
    }
}

