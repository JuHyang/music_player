package com.juhyang.musicplayer.internal.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.juhyang.musicplayer.AlbumThumbnail
import com.juhyang.musicplayer.R
import com.juhyang.musicplayer.internal.model.RepeatMode
import com.juhyang.musicplayer.internal.model.ShuffleMode
import com.juhyang.musicplayer.internal.presentation.MusicPlayerViewModel


@Composable
internal fun ExpandedMusicPlayer(onCollapse: () -> Unit, viewModel: MusicPlayerViewModel) {
    val playerState by viewModel.playerState.collectAsState()
    val playingState by viewModel.playingState.collectAsState()

    Column(
        modifier = Modifier
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { onCollapse() },
            modifier = Modifier.align(Alignment.Start),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_down),
                contentDescription = "Shuffle",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        // SongTitle
        Text(
            text = playingState.currentSong?.title ?: "No Song",
            color = Color.White
        )
        // Artist
        Text(
            text = playingState.currentSong?.artistName ?: "No Artist",
            color = Color.White
        )
//         앨범 섬네일
        AlbumThumbnail(
            playingState.currentSong?.albumCoverUri,
            Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Color.White, RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))
        // 재생 시간 표시 Slider
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

        // 재생 시간 표시 (분:초)
        Text(
            modifier = Modifier.align(Alignment.End),
            text = "${millsToTime(playingState.currentPosition)} / ${millsToTime(playingState.totalDuration)}",
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 재생 및 컨트롤 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { viewModel.setIntent(MusicPlayerViewModel.Intent.Previous) }) {
                Icon(
                    painter = painterResource(id = R.drawable.skip_previous),
                    contentDescription = "Previous",
                    tint = Color.White
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

            IconButton(onClick = { viewModel.setIntent(MusicPlayerViewModel.Intent.Next) }) {
                Icon(
                    painter = painterResource(id = R.drawable.skip_next),
                    contentDescription = "Next",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 모드 변경 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { viewModel.setIntent(MusicPlayerViewModel.Intent.ChangeShuffleMode) }) {
                Icon(
                    painter = painterResource(id = R.drawable.shuffle),
                    contentDescription = "Shuffle",
                    tint = if (playerState.shuffleMode == ShuffleMode.ON) Color.White else Color.Gray
                )
            }

            IconButton(onClick = { viewModel.setIntent(MusicPlayerViewModel.Intent.ChangeRepeatMode) }) {
                when (playerState.repeatMode) {
                    RepeatMode.OFF -> {
                        Icon(
                            painter = painterResource(id = R.drawable.repeat_on),
                            contentDescription = "Repeat",
                            tint = Color.Gray
                        )
                    }

                    RepeatMode.ONE -> {
                        Icon(
                            painter = painterResource(id = R.drawable.repeat_one),
                            contentDescription = "Repeat",
                            tint = Color.White
                        )
                    }

                    RepeatMode.ALL -> {
                        Icon(
                            painter = painterResource(id = R.drawable.repeat_on),
                            contentDescription = "Repeat",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 재생목록 보기 버튼
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "View Playlist", color = Color.White)
        }
    }
}



private fun millsToTime(mills: Int): String {
    val seconds = mills / 1000
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "$minutes:$remainingSeconds"
}
