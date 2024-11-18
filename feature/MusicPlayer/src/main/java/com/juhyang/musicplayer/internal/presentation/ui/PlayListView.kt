package com.juhyang.musicplayer.internal.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.juhyang.musicplayer.internal.presentation.MusicPlayerViewModel


@Composable
internal fun PlayListView(viewModel: MusicPlayerViewModel, modifier: Modifier) {
    val playerState by viewModel.playerState.collectAsState()

    val state = remember { LazyListState() }

    LaunchedEffect(playerState.currentPlayingSongIndex) {
        state.animateScrollToItem(playerState.currentPlayingSongIndex)
    }
    LazyColumn(
        state = state,
        modifier = modifier
    ) {
        items(playerState.playList.size) { index ->
            val song = playerState.playList[index]
            Row(
                Modifier
                    .clickable { viewModel.setIntent(MusicPlayerViewModel.Intent.Play(index)) }
                    .background(if (index == playerState.currentPlayingSongIndex) Color.Gray else Color.Transparent)
            ) {
                Text(
                    text = "${index + 1}",
                    color = Color.White,
                    modifier = Modifier
                        .padding(8.dp)
                )
                Text(
                    text = song.title,
                    color = Color.White,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}
