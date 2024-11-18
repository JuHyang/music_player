package com.juhyang.musicplayer.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.juhyang.musicplayer.MusicPlayer
import com.juhyang.musicplayer.MusicPlayerView
import com.juhyang.musicplayer.di.AlbumDIContainer
import com.juhyang.musicplayer.di.SongListDiContainer
import com.juhyang.musicplayer.presentation.AlbumListViewModel
import com.juhyang.musicplayer.ui.theme.MusicPlayerTheme
import com.juhyang.permission.GrantStatus
import com.juhyang.permission.PermissionChecker
import com.juhyang.permission.model.ReadAudioPermission
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var albumListViewModel: AlbumListViewModel
    private val musicPlayer by lazy { MusicPlayer.instance }
    private var navController: NavHostController? = null
    private val permissionChecker by lazy { PermissionChecker.instance }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val albumDiContainer = AlbumDIContainer()
        albumListViewModel = albumDiContainer.createViewModel(this)

        bindViewModel()

        setContent {
            MusicPlayerTheme {
                navController = rememberNavController()
                MyApp(navController = navController!!, albumListViewModel = albumListViewModel)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        musicPlayer.onStart(this)
    }

    override fun onResume() {
        super.onResume()
        musicPlayer.onResume(this)
    }

    override fun onPause() {
        super.onPause()

        musicPlayer.onPause(this)
    }

    private fun bindViewModel() {
        lifecycleScope.launch {
            albumListViewModel.viewAction.collect {
                handleAlbumViewAction(it)
            }
        }
    }

    private fun handleAlbumViewAction(viewAction: AlbumListViewModel.ViewAction) {
        when (viewAction) {
            is AlbumListViewModel.ViewAction.Idle -> { }
            is AlbumListViewModel.ViewAction.MoveAlbumDetailScreen -> {
                navController?.navigate("albumDetail/${viewAction.album.title}/${viewAction.album.artist}")
            }
            is AlbumListViewModel.ViewAction.RequestStoragePermission -> {
                requestStoragePermission()
            }
        }
    }

    private fun requestStoragePermission() {
        lifecycleScope.launch {
            permissionChecker.requestPermissionIfNeeded(this@MainActivity, ReadAudioPermission(), isRequired = true)
                .collect {
                    if (it.grantStatus == GrantStatus.GRANTED) {
                        albumListViewModel.setIntent(AlbumListViewModel.Intent.GrantStoragePermission)
                    } else {
                        albumListViewModel.setIntent(AlbumListViewModel.Intent.RevokeStoragePermission)
                    }
                }
        }
    }
}

@Composable
fun MyApp(navController: NavHostController, albumListViewModel: AlbumListViewModel) {
    val bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState)
    val coroutineScope = rememberCoroutineScope()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            MusicPlayerView(
                bottomSheetState = bottomSheetState,
                onExpand = {
                    coroutineScope.launch { bottomSheetState.expand() }
                },
                onCollapse = {
                    coroutineScope.launch { bottomSheetState.collapse() }
                }
            )
        },
        sheetPeekHeight = 150.dp
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
                val songListDIContainer = SongListDiContainer()
                val albumDetailViewModel = songListDIContainer.createViewModel(LocalContext.current)
                val albumTitle = backStackEntry.arguments?.getString("albumTitle") ?: ""
                val artist = backStackEntry.arguments?.getString("artistName") ?: ""
                AlbumDetailScreen(viewModel = albumDetailViewModel, albumTitle = albumTitle, artist = artist)
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
