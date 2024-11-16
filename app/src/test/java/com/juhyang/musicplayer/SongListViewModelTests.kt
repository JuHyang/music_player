package com.juhyang.musicplayer

import com.juhyang.musicplayer.domain.model.Album
import com.juhyang.musicplayer.domain.usecase.LoadAlbumUseCase
import com.juhyang.musicplayer.presentation.SongListViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


class SongListViewModelTests: CoroutineTest() {
    private lateinit var viewModel: SongListViewModel

    private val albumA = Album("AlbumA", "artist", null, listOf(
        Song("SongA", "SongA", "AA", 100),
        Song("SongB", "SongB", "AA", 100),
        Song("SongC", "SongC", "AA", 100)
    ))
    private val albumTitle = "AlbumA"
    private val artist = "artist"

    @MockK
    private lateinit var loadAlbumUseCase: LoadAlbumUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)

        viewModel = SongListViewModel(
            loadAlbumUseCase,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            defaultDispatcher = defaultTestDispatcher
        )

        coEvery { loadAlbumUseCase.execute(any(), any())} returns flowOf(albumA)
    }

    @Test
    fun `앨범 이름과 아티스트를 가지고 음악을 로딩해온다`() = runTest {
        viewModel.setIntent(SongListViewModel.Intent.LoadAlbum(albumTitle, artist))
        coVerify { loadAlbumUseCase.execute(albumTitle, artist) }


        assert(viewModel.viewState.value is SongListViewModel.ViewState.Loaded)
        assert((viewModel.viewState.value as SongListViewModel.ViewState.Loaded).album == albumA)
    }

    @Test
    fun `재생버튼을 누르면 앨범 전체가 음악이 재생된다`() = runTest {
        viewModel.setIntent(SongListViewModel.Intent.LoadAlbum(albumTitle, artist))
        coVerify { loadAlbumUseCase.execute(albumTitle, artist) }

        viewModel.setIntent(SongListViewModel.Intent.PlayAll)

        assert(viewModel.viewAction.value is SongListViewModel.ViewAction.PlaySongs)
        assert((viewModel.viewAction.value as SongListViewModel.ViewAction.PlaySongs).songs == albumA.songs)
    }

    @Test
    fun `랜덤 재생버튼을 누르면 앨범 전체가 랜덤으로 재생된다`() = runTest  {
        viewModel.setIntent(SongListViewModel.Intent.LoadAlbum(albumTitle, artist))
        coVerify { loadAlbumUseCase.execute(albumTitle, artist) }

        viewModel.setIntent(SongListViewModel.Intent.PlayRandom)

        assert(viewModel.viewAction.value is SongListViewModel.ViewAction.PlaySongs)
        assert((viewModel.viewAction.value as SongListViewModel.ViewAction.PlaySongs).songs != albumA.songs)
        assert((viewModel.viewAction.value as SongListViewModel.ViewAction.PlaySongs).songs.size == albumA.songs.size)
        assert((viewModel.viewAction.value as SongListViewModel.ViewAction.PlaySongs).songs.toSet() == albumA.songs.toSet())
    }

    @Test
    fun `노래를 고르면 해당 노래를 포함하고 다음 노래들을 모두 재생한다`() = runTest  {
        viewModel.setIntent(SongListViewModel.Intent.LoadAlbum(albumTitle, artist))
        coVerify { loadAlbumUseCase.execute(albumTitle, artist) }

        viewModel.setIntent(SongListViewModel.Intent.PlaySong(1))

        assert(viewModel.viewAction.value is SongListViewModel.ViewAction.PlaySongs)
        assert((viewModel.viewAction.value as SongListViewModel.ViewAction.PlaySongs).songs == albumA.songs.slice(1 until albumA.songs.size))
    }
}
