package com.juhyang.musicplayer.presentation

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.juhyang.musicplayer.MusicPlayer
import com.juhyang.musicplayer.data.repository.PermissionRepositoryImpl
import com.juhyang.musicplayer.di.AlbumDIContainer
import com.juhyang.musicplayer.domain.model.Album
import com.juhyang.musicplayer.presentation.theme.MusicPlayerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: AlbumListViewModel
    private val musicPlayer by lazy { MusicPlayer() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val albumDiContainer = AlbumDIContainer()
        viewModel = albumDiContainer.createViewModel(this)
        bindViewModel()

        setContent {
            MusicPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val viewState by viewModel.viewState.collectAsState()
                    handleViewState(viewState)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        musicPlayer.onResume(this)

        musicPlayer.start("")

        viewModel.setAction(AlbumListViewModel.Intent.OnResume)
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
            is AlbumListViewModel.ViewAction.MoveMusicList -> {}
            is AlbumListViewModel.ViewAction.RequestStoragePermission -> {
                requestStoragePermission()
            }
        }
    }


    @Composable
    private fun handleViewState(viewState: AlbumListViewModel.ViewState) {
        when(viewState) {
            is AlbumListViewModel.ViewState.Idle -> {}
            is AlbumListViewModel.ViewState.Loaded -> {
                renderAlbumList(viewState.albumList)
            }
            is AlbumListViewModel.ViewState.ErrorPermissionDenied -> {}
            is AlbumListViewModel.ViewState.ErrorEmptyAlbums -> {}
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

    @Composable
    private fun renderAlbumList(albumList: List<Album>) {
        AlbumList(albumList)
    }
}

@Composable
fun AlbumList(albumList: List<Album>) {
    Column {
        albumList.forEach {
            Text(text = it.title)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MusicPlayerTheme {

    }
}
