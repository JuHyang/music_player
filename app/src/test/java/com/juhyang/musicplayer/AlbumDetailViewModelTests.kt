package com.juhyang.musicplayer

import com.juhyang.musicplayer.domain.model.Album
import com.juhyang.musicplayer.domain.usecase.LoadAlbumUseCase
import com.juhyang.musicplayer.presentation.AlbumDetailViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


class AlbumDetailViewModelTests: CoroutineTest() {
    private lateinit var viewModel: AlbumDetailViewModel

    private val albumA = Album("AlbumA", "artist", null, listOf(
        Song("SongA", "SongA", null, "SongA", 100),
        Song("SongB", "SongA", null, "SongA", 100),
        Song("SongC", "SongA", null, "SongA", 100),
        Song("SongD", "SongA", null, "SongA", 100),
        Song("SongE", "SongA", null, "SongA", 100),

    ))
    private val albumTitle = "AlbumA"
    private val artist = "artist"

    @MockK
    private lateinit var loadAlbumUseCase: LoadAlbumUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)

        viewModel = AlbumDetailViewModel(
            loadAlbumUseCase,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
        )

        coEvery { loadAlbumUseCase.execute(any(), any())} returns flowOf(albumA)
    }

    @Test
    fun `앨범 이름과 아티스트를 가지고 음악을 로딩해온다`() = runTest {
        viewModel.setIntent(AlbumDetailViewModel.Intent.LoadAlbum(albumTitle, artist))
        coVerify { loadAlbumUseCase.execute(albumTitle, artist) }


        assert(viewModel.viewState.value is AlbumDetailViewModel.ViewState.Loaded)
        assert((viewModel.viewState.value as AlbumDetailViewModel.ViewState.Loaded).album == albumA)
    }

    @Test
    fun `재생버튼을 누르면 앨범 전체가 음악이 재생된다`() = runTest {
        viewModel.setIntent(AlbumDetailViewModel.Intent.LoadAlbum(albumTitle, artist))
        coVerify { loadAlbumUseCase.execute(albumTitle, artist) }

        viewModel.setIntent(AlbumDetailViewModel.Intent.PlayAll)

        assert(viewModel.viewAction.value is AlbumDetailViewModel.ViewAction.PlaySongs)
        assert((viewModel.viewAction.value as AlbumDetailViewModel.ViewAction.PlaySongs).songs == albumA.songs)
    }

    @Test
    fun `랜덤 재생버튼을 누르면 앨범 전체가 랜덤으로 재생된다`() = runTest  {
        viewModel.setIntent(AlbumDetailViewModel.Intent.LoadAlbum(albumTitle, artist))
        coVerify { loadAlbumUseCase.execute(albumTitle, artist) }

        viewModel.setIntent(AlbumDetailViewModel.Intent.PlayRandom)

        assert(viewModel.viewAction.value is AlbumDetailViewModel.ViewAction.PlaySongs)
        assert((viewModel.viewAction.value as AlbumDetailViewModel.ViewAction.PlaySongs).songs != albumA.songs)
        assert((viewModel.viewAction.value as AlbumDetailViewModel.ViewAction.PlaySongs).songs.size == albumA.songs.size)
        assert((viewModel.viewAction.value as AlbumDetailViewModel.ViewAction.PlaySongs).songs.toSet() == albumA.songs.toSet())
    }

    @Test
    fun `노래를 고르면 해당 노래를 포함하고 다음 노래들을 모두 재생한다`() = runTest  {
        viewModel.setIntent(AlbumDetailViewModel.Intent.LoadAlbum(albumTitle, artist))
        coVerify { loadAlbumUseCase.execute(albumTitle, artist) }

        viewModel.setIntent(AlbumDetailViewModel.Intent.PlaySong(1))

        assert(viewModel.viewAction.value is AlbumDetailViewModel.ViewAction.PlaySongs)
        assert((viewModel.viewAction.value as AlbumDetailViewModel.ViewAction.PlaySongs).songs == albumA.songs.slice(1 until albumA.songs.size))
    }
}
