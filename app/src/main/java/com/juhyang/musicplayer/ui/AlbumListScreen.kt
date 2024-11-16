package com.juhyang.musicplayer.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.juhyang.musicplayer.domain.model.Album
import com.juhyang.musicplayer.presentation.AlbumListViewModel


@Composable
fun AlbumListScreen(viewModel: AlbumListViewModel) {
    viewModel.setIntent(AlbumListViewModel.Intent.LoadAlbums)
    handleViewState(viewModel = viewModel)
}

@Composable
fun handleViewState(viewModel: AlbumListViewModel) {
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
fun AlbumGridView(albumList: List<Album>, onAlbumClick: (Album) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier,
    ) {
        items(albumList.size) { count ->
            AlbumItem(albumList[count], onAlbumClick)
        }
    }
}

@Composable
fun AlbumItem(album: Album, onAlbumClick: (Album) -> Unit) {
    Column(
        modifier = Modifier.clickable { onAlbumClick(album) },
    ) {
        Text(text = album.title)
    }
}
