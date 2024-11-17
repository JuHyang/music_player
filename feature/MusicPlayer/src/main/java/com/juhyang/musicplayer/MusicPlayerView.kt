package com.juhyang.musicplayer

import androidx.compose.material.BottomSheetState
import androidx.compose.runtime.Composable
import com.juhyang.musicplayer.internal.presentation.MusicPlayerViewModel
import com.juhyang.musicplayer.internal.presentation.ui.ExpandedMusicPlayer
import com.juhyang.musicplayer.internal.presentation.ui.MiniMusicPlayer

@Composable
fun MusicPlayerView(bottomSheetState: BottomSheetState, onExpand: () -> Unit, onCollapse: () -> Unit) {
    val viewModel = MusicPlayerViewModel(MusicPlayer.instance)

    if (bottomSheetState.isExpanded) {
        ExpandedMusicPlayer(onCollapse = onCollapse, viewModel = viewModel)
    } else {
        MiniMusicPlayer(onExpand = onExpand, viewModel = viewModel)
    }
}
