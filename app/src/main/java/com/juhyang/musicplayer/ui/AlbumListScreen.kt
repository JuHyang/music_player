package com.juhyang.musicplayer.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.juhyang.musicplayer.AlbumThumbnail
import com.juhyang.musicplayer.domain.model.Album
import com.juhyang.musicplayer.presentation.AlbumListViewModel


@Composable
fun AlbumListScreen(viewModel: AlbumListViewModel) {
    viewModel.setIntent(AlbumListViewModel.Intent.LoadAlbums)
    handleAlbumListViewState(viewModel = viewModel)
}

@Composable
private fun handleAlbumListViewState(viewModel: AlbumListViewModel) {
    val viewState by viewModel.viewState.collectAsState()
    when(viewState) {
        is AlbumListViewModel.ViewState.Idle -> {}
        is AlbumListViewModel.ViewState.Loaded -> {
            AlbumGridView(albumList = (viewState as AlbumListViewModel.ViewState.Loaded).albumList) {
                viewModel.setIntent(AlbumListViewModel.Intent.ClickAlbum(it))
            }
        }
        is AlbumListViewModel.ViewState.ErrorPermissionDenied -> {}
        is AlbumListViewModel.ViewState.ErrorEmptyAlbums -> {}
    }
}

@Composable
private fun AlbumGridView(albumList: List<Album>, onAlbumClick: (Album) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(albumList.size) { count ->
            AlbumItem(albumList[count], onAlbumClick)
        }
    }
}

@Composable
private fun AlbumItem(album: Album, onAlbumClick: (Album) -> Unit) {
    Column(
        modifier = Modifier.clickable { onAlbumClick(album) }.padding(8.dp).fillMaxSize(),
    ) {
        AlbumThumbnail(
            albumThumbnail = album.albumCoverUri,
            modifier = Modifier
                .fillMaxSize()
        )
        Text(
            text = album.title,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
        )
        Text(
            text = album.artist,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}
