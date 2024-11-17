package com.juhyang.musicplayer

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomSheetState
import androidx.compose.material.Slider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.juhyang.musicplayer.internal.MusicPlayerViewModel
import com.juhyang.musicplayer.internal.model.RepeatMode
import com.juhyang.musicplayer.internal.model.ShuffleMode
import kotlinx.coroutines.launch

@Composable
fun MusicPlayerView(bottomSheetState: BottomSheetState, onCollapse: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val viewModel = MusicPlayerViewModel(MusicPlayer.instance)

    if (bottomSheetState.isExpanded) {
        ExpandMusicPlayer(onCollapse = onCollapse, viewModel = viewModel)
    } else {
        // 미니 플레이어
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    coroutineScope.launch { bottomSheetState.expand() }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Mini Player", Modifier.weight(1f))
            IconButton(onClick = { viewModel.setIntent(MusicPlayerViewModel.Intent.ClickPlayButton) }) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play")
            }
        }
    }
}


@Composable
internal fun ExpandMusicPlayer(onCollapse: () -> Unit, viewModel: MusicPlayerViewModel) {
    val playerState by viewModel.playerState.collectAsState()
    val playingState by viewModel.playingState.collectAsState()

    Column(
        modifier = Modifier
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
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
        AlbumThumbnail(playingState.currentSong?.albumCoverUri)

        Spacer(modifier = Modifier.height(16.dp))
        // 재생 시간 표시 Slider
        Slider(
            value = playingState.currentPosition.toFloat(),
            onValueChange = { viewModel.setIntent(MusicPlayerViewModel.Intent.SeekTo(it.toInt())) },
            valueRange = 0f..playingState.totalDuration.toFloat(),
            steps = 100,
            modifier = Modifier.fillMaxWidth()
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
                    painter = painterResource(id = R.drawable.baseline_skip_previous_24), // 벡터 이미지
                    contentDescription = "Previous",
                    tint = Color.White // 색상 변경
                )
            }

            IconButton(onClick = { viewModel.setIntent(MusicPlayerViewModel.Intent.ClickPlayButton) }) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White
                )
            }

            IconButton(onClick = { viewModel.setIntent(MusicPlayerViewModel.Intent.Next) }) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
//                    imageVector = Icons.Default.SkipNext,
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
                    imageVector = Icons.Default.PlayArrow,
//                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
                    tint = if (playerState.shuffleMode == ShuffleMode.ON) Color.Green else Color.White
                )
            }

            IconButton(onClick = { viewModel.setIntent(MusicPlayerViewModel.Intent.ChangeRepeatMode) }) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
//                    imageVector = Icons.Default.Repeat,
                    contentDescription = "Repeat",
                    tint = when(playerState.repeatMode) {
                        RepeatMode.OFF -> Color.White
                        RepeatMode.ONE -> Color.Green
                        RepeatMode.ALL -> Color.Blue
                    }
                )
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

@Composable
private fun AlbumThumbnail(albumThumbnail: Uri?) {
//    AsyncImage(
//        model = albumThumbnail,
//        contentDescription = "Album Thumbnail",
//        modifier = Modifier
//            .size(300.dp)
//            .clip(RoundedCornerShape(16.dp))
//            .border(2.dp, Color.White, RoundedCornerShape(16.dp))
//    )
}

fun millsToTime(mills: Int): String {
    val seconds = mills / 1000
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "$minutes:$remainingSeconds"
}
