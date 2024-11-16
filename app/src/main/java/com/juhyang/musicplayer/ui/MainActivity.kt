package com.juhyang.musicplayer.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.juhyang.musicplayer.MusicPlayer
import com.juhyang.musicplayer.data.repository.PermissionRepositoryImpl
import com.juhyang.musicplayer.di.AlbumDIContainer
import com.juhyang.musicplayer.di.SongListDiContainer
import com.juhyang.musicplayer.presentation.AlbumListViewModel
import com.juhyang.musicplayer.presentation.SongListViewModel
import com.juhyang.musicplayer.ui.theme.MusicPlayerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var albumListViewModel: AlbumListViewModel
    private lateinit var songListViewModel: SongListViewModel
    private val musicPlayer by lazy { MusicPlayer() }
    private var navController: NavHostController? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val albumDiContainer = AlbumDIContainer()
        albumListViewModel = albumDiContainer.createViewModel(this)

        val songListDIContainer = SongListDiContainer()
        songListViewModel = songListDIContainer.createViewModel(this)

        bindViewModel()

        setContent {
            MusicPlayerTheme {
                navController = rememberNavController()
                MyApp(navController = navController!!, albumListViewModel = albumListViewModel, songListViewModel = songListViewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        albumListViewModel
        musicPlayer.onResume(this)
    }

    override fun onPause() {
        super.onPause()

        musicPlayer.onPause(this)
    }

    private fun bindViewModel() {
        lifecycleScope.launch {
            albumListViewModel.viewAction.collect {
                Log.d("##Arthur", "MainActivity bindViewModel: AlbumListViewModelViewAction : ${it}")
                handleAlbumViewAction(it)
            }

        }
        lifecycleScope.launch {
            songListViewModel.viewAction.collect {
                Log.d("##Arthur", "MainActivity bindViewModel: SongListViewModelViewACtion : ${it}")
                handleSongListViewAction(it)
            }
        }
    }

    private fun handleAlbumViewAction(viewAction: AlbumListViewModel.ViewAction) {
        when (viewAction) {
            is AlbumListViewModel.ViewAction.Idle -> { }
            is AlbumListViewModel.ViewAction.MoveMusicList -> {
                navController?.navigate("albumDetail/${viewAction.album.title}/${viewAction.album.artist}")
            }
            is AlbumListViewModel.ViewAction.RequestStoragePermission -> {
                requestStoragePermission()
            }
        }
    }

    private fun handleSongListViewAction(viewAction: SongListViewModel.ViewAction) {
        when (viewAction) {
            is SongListViewModel.ViewAction.Idle -> {}
            is SongListViewModel.ViewAction.PlaySongs -> {
                musicPlayer.play(viewAction.songs)
            }
        }
    }


    private fun requestStoragePermission() {
        val manifestPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionRepositoryImpl.READ_MEDIA_AUDIO_PERMISSION
        } else {
            PermissionRepositoryImpl.READ_EXTERNAL_STORAGE_PERMISSION
        }
        if (shouldShowRequestPermissionRationale(manifestPermission)) {
            Log.d("##Arthur", "MainActivity bindViewModel: viewState : 사용자 거부 !")
            // TODO: 설정화면으로 보내버리기
        } else {
            Log.d("##Arthur", "MainActivity bindViewModel: viewState : 사용자 거부는 안함! !")
            requestPermissions(arrayOf(manifestPermission), 100)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(navController: NavHostController, albumListViewModel: AlbumListViewModel, songListViewModel: SongListViewModel) {
    Scaffold(
        bottomBar = { MusicPlayerView() }
    ) {paddingValues ->

        NavHost(
            navController = navController,
            startDestination = "albumList",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("albumList") {
                AlbumListScreen(viewModel = albumListViewModel)
            }
            composable(
                "albumDetail/{albumTitle}/{artistName}",
                arguments = listOf(navArgument("albumTitle") { type = NavType.StringType}, navArgument("artistName") { type = NavType.StringType})
            ) { backStackEntry ->
                val albumTitle = backStackEntry.arguments?.getString("albumTitle") ?: ""
                val artist = backStackEntry.arguments?.getString("artistName") ?: ""
                AlbumDetailScreen(viewModel = songListViewModel, albumTitle = albumTitle, artist = artist)
            }
        }
    }
    
}

@Preview(showBackground = true)
@Composable
fun MyAppPreView() {
    MusicPlayerTheme {
//        MyApp()
    }
}
