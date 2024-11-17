package com.juhyang.musicplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.juhyang.musicplayer.internal.MusicPlayerViewModel
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
fun ExpandMusicPlayer(onCollapse: () -> Unit, viewModel: MusicPlayerViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
//         앨범 섬네일
//        Image(
//            painter = rememberImagePainter(data = albumThumbnail),
//            contentDescription = "Album Thumbnail",
//            modifier = Modifier
//                .size(300.dp)
//                .clip(RoundedCornerShape(16.dp))
//                .border(2.dp, Color.White, RoundedCornerShape(16.dp))
//        )

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
//                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
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
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
//                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
//                    tint = if (isShuffleMode) Color.Green else Color.White
                )
            }

            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
//                    imageVector = Icons.Default.Repeat,
                    contentDescription = "Repeat",
//                    tint = if (isRepeatMode) Color.Green else Color.White
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
