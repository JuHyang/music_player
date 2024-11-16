package com.juhyang.musicplayer.presentation

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
import com.juhyang.musicplayer.presentation.theme.MusicPlayerTheme
import com.juhyang.musicplayer.ui.AlbumDetailScreen
import com.juhyang.musicplayer.ui.AlbumListScreen
import com.juhyang.musicplayer.ui.MusicPlayerView
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: AlbumListViewModel
    private val musicPlayer by lazy { MusicPlayer() }
    private var navController: NavHostController? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val albumDiContainer = AlbumDIContainer()
        viewModel = albumDiContainer.createViewModel(this)
        bindViewModel()

        setContent {
            MusicPlayerTheme {
                navController = rememberNavController()
                MyApp(navController = navController!!, albumListViewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        musicPlayer.onResume(this)

        musicPlayer.start("")

        viewModel.setIntent(AlbumListViewModel.Intent.LoadAlbums)
    }

    override fun onPause() {
        super.onPause()

        musicPlayer.onPause(this)
    }

    private fun bindViewModel() {
        lifecycleScope.launch {
            viewModel.viewAction.collect {
                Log.d("##Arthur", "MainActivity bindViewModel: viewState : ${it}")
                handleViewAction(it)
            }
        }
    }

    private fun handleViewAction(viewAction: AlbumListViewModel.ViewAction) {
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


    private fun requestStoragePermission() {
        val manifestPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionRepositoryImpl.READ_MEDIA_AUDIO_PERMISSION
        } else {
            PermissionRepositoryImpl.READ_EXTERNAL_STORAGE_PERMISSION
        }
        if (shouldShowRequestPermissionRationale(manifestPermission)) {
            Log.d("##Arthur", "MainActivity bindViewModel: viewState : 사용자 거부 !")
        } else {
            Log.d("##Arthur", "MainActivity bindViewModel: viewState : 사용자 거부는 안함! !")
            requestPermissions(arrayOf(manifestPermission), 100)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(navController: NavHostController, albumListViewModel: AlbumListViewModel) {
    Scaffold(
        bottomBar = { MusicPlayerView() }
    ) {paddingValues ->

        NavHost(
            navController = navController,
            startDestination = "albumList",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("albumList") { AlbumListScreen(viewModel = albumListViewModel)}
            composable(
                "albumDetail/{albumTitle}/{artistName}",
                arguments = listOf(navArgument("albumTitle") { type = NavType.StringType}, navArgument("artistName") { type = NavType.StringType})
            ) { backStackEntry ->
                val albumTitle = backStackEntry.arguments?.getString("albumTitle") ?: ""
                val artistName = backStackEntry.arguments?.getString("artistName") ?: ""
                Log.d("##Arthur", "albumTitle MyApp: albumTitle : ${albumTitle}")
                Log.d("##Arthur", "artistName MyApp: artistName : ${artistName}")
                AlbumDetailScreen()
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
