package com.juhyang.musicplayer.internal.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
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
import com.juhyang.musicplayer.internal.presentation.MusicPlayerViewModel


@Composable
internal fun MiniMusicPlayer(onExpand: () -> Unit, viewModel: MusicPlayerViewModel) {
    val playingState by viewModel.playingState.collectAsState()
    Column(
        modifier = Modifier.fillMaxWidth().height(150.dp)
            .background(Color.Black)
    ) {
        Slider(
            value = playingState.currentPosition.toFloat(),
            onValueChange = { viewModel.setIntent(MusicPlayerViewModel.Intent.SeekTo(it.toInt())) },
            valueRange = 0f..playingState.totalDuration.toFloat(),
            steps = 100,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.Gray
            )
        )

        Row(
            Modifier
                .background(Color.Black)
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { onExpand() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            AlbumThumbnail(playingState.currentSong?.albumCoverUri, Modifier.size(50.dp))
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = playingState.currentSong?.title ?: "No Song",
                    color = Color.White
                )
                Text(
                    text = playingState.currentSong?.artistName ?: "No Artist",
                    color = Color.White
                )
            }
            IconButton(onClick = { viewModel.setIntent(MusicPlayerViewModel.Intent.ClickPlayButton) }) {
                if (playingState.isPlaying) {
                    Icon(
                        painter = painterResource(id = R.drawable.pause),
                        contentDescription = "Pause",
                        tint = Color.White
                    )

                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.play_arrow),
                        contentDescription = "Play",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

