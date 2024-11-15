package com.juhyang.musicplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun MusicPlayerView() {
    // 하단 플레이어 UI
    Box(
        modifier = Modifier.fillMaxWidth().height(56.dp).background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        Text("Music Player")
    }
}
