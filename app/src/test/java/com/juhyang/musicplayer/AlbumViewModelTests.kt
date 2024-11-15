package com.juhyang.musicplayer

import com.juhyang.musicplayer.domain.model.Album
import com.juhyang.musicplayer.presentation.AlbumListViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


class AlbumViewModelTests: CoroutineTest() {
    private lateinit var viewModel: AlbumListViewModel

    @Before
    fun setUp() {
        viewModel = AlbumListViewModel(
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            defaultDispatcher = defaultTestDispatcher
        )
    }

    @Test
    fun `화면이 실행되면 곡 목록을 로딩한다`() = runTest {
        viewModel.setAction(AlbumListViewModel.Action.LoadAlbums)

        assert(viewModel.viewState.value is AlbumListViewModel.ViewState.Loaded)
    }
    @Test
    fun `앨범 클릭하면 곡 목록화면으로 넘어간다`() = runTest {
        val album = Album("TEST_ALBUM", emptyList())
        viewModel.setAction(AlbumListViewModel.Action.ClickAlbum(album))

        val viewStateValue = viewModel.viewState.value
        assert(viewStateValue is AlbumListViewModel.ViewState.MoveMusicList)
        assert((viewStateValue as AlbumListViewModel.ViewState.MoveMusicList).album == album)
    }
}
