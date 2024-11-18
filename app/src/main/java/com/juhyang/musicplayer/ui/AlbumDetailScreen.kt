package com.juhyang.musicplayer.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.juhyang.musicplayer.AlbumThumbnail
import com.juhyang.musicplayer.R
import com.juhyang.musicplayer.Song
import com.juhyang.musicplayer.domain.model.Album
import com.juhyang.musicplayer.presentation.AlbumDetailViewModel


@Composable
fun AlbumDetailScreen(viewModel: AlbumDetailViewModel, albumTitle: String, artist: String) {
    viewModel.setIntent(AlbumDetailViewModel.Intent.LoadAlbum(albumTitle, artist))
    handleSongListViewState(viewModel = viewModel)
}

@Composable
private fun handleSongListViewState(viewModel: AlbumDetailViewModel) {
    val viewState by viewModel.viewState.collectAsState()
    when (viewState) {
        is AlbumDetailViewModel.ViewState.Idle -> {}
        is AlbumDetailViewModel.ViewState.Loaded -> {
            SongListView(
                (viewState as AlbumDetailViewModel.ViewState.Loaded).album,
                onSongClick = {
                    viewModel.setIntent(AlbumDetailViewModel.Intent.PlaySong(it))
                }, onPlayAllClick = {
                    viewModel.setIntent(AlbumDetailViewModel.Intent.PlayAll)
                }, onPlayShuffleClick = {
                    viewModel.setIntent(AlbumDetailViewModel.Intent.PlayRandom)
                }, onAddPlayList = {
                    viewModel.setIntent(AlbumDetailViewModel.Intent.AddPlayList(it))
                })
        }
    }
}

@Composable
private fun SongListView(album: Album, onSongClick: (Int) -> Unit, onPlayAllClick: () -> Unit, onPlayShuffleClick: () -> Unit, onAddPlayList: (Int) -> Unit) {
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
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = onPlayAllClick) {
                Icon(
                    painter = painterResource(id = R.drawable.play_arrow),
                    contentDescription = "PlayAll",
                    tint = Color.Black
                )
            }

            IconButton(onClick = onPlayShuffleClick) {
                Icon(
                    painter = painterResource(id = R.drawable.shuffle),
                    contentDescription = "PlayShuffle",
                    tint = Color.Black
                )
            }

        }
        LazyColumn(
            modifier = Modifier.padding(16.dp),
        ) {
            items(album.songs.size) { count ->
                SongItem(album.songs[count], count, onSongClick, onAddPlayList)
            }
        }
    }
}

@Composable
private fun SongItem(song: Song, index: Int, onSongClick: (Int) -> Unit, onAddPlayList: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onSongClick(index) },
        verticalAlignment = Alignment.CenterVertically // 수직 정렬 추가
    ) {
        Text(
            text = (index + 1).toString(),
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = song.title,
            modifier = Modifier
                .padding(8.dp)
                .weight(1f) // 제목에 남는 공간을 차지하도록 설정
        )
        IconButton(
            onClick = { onAddPlayList(index) }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.queue_music),
                contentDescription = "AddPlayList",
                tint = Color.Black
            )
        }
    }
}

