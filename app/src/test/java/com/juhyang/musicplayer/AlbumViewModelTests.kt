package com.juhyang.musicplayer

import com.juhyang.musicplayer.domain.model.Album
import com.juhyang.musicplayer.domain.model.PermissionStatus
import com.juhyang.musicplayer.domain.usecase.CheckStoragePermissionUseCase
import com.juhyang.musicplayer.domain.usecase.LoadAlbumUseCase
import com.juhyang.musicplayer.presentation.AlbumListViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


class AlbumViewModelTests : CoroutineTest() {
    private lateinit var viewModel: AlbumListViewModel

    @MockK
    private lateinit var checkStoragePermissionUseCase: CheckStoragePermissionUseCase

    @MockK
    private lateinit var loadAlbumUseCase: LoadAlbumUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)

        viewModel = AlbumListViewModel(
            loadAlbumUseCase,
            checkStoragePermissionUseCase,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            defaultDispatcher = defaultTestDispatcher
        )
    }

    private val albumA = Album("AlbumA", "artist", null, listOf())
    private val albumB = Album("AlbumB", "artist2", null, listOf())

    @Test
    fun `화면 로딩 시저장소 접근권한이 없다면 요청한다`() = runTest {
        coEvery { checkStoragePermissionUseCase.execute() } returns flowOf(PermissionStatus.REVOKED)

        viewModel.setIntent(AlbumListViewModel.Intent.LoadAlbums)
        coVerify { checkStoragePermissionUseCase.execute() }

        assert(viewModel.viewAction.value is AlbumListViewModel.ViewAction.RequestStoragePermission)
    }

    @Test
    fun `화면 로딩 시저장소 접근권한이 없다면 요청하고, 거절하면 에러화면을 보여준다`() = runTest {
        coEvery { checkStoragePermissionUseCase.execute() } returns flowOf(PermissionStatus.REVOKED)

        viewModel.setIntent(AlbumListViewModel.Intent.LoadAlbums)
        coVerify { checkStoragePermissionUseCase.execute() }

        assert(viewModel.viewAction.value is AlbumListViewModel.ViewAction.RequestStoragePermission)
        viewModel.setIntent(AlbumListViewModel.Intent.RevokeStoragePermission)

        assert(viewModel.viewState.value is AlbumListViewModel.ViewState.ErrorPermissionDenied)
    }

    @Test
    fun `화면 로딩 시저장소 접근권한이 없다면 요청하고, 승인하면 목록을 로딩한다`() = runTest {
        coEvery { checkStoragePermissionUseCase.execute() } returns flowOf(PermissionStatus.REVOKED)
        coEvery { loadAlbumUseCase.execute() } returns flowOf(listOf(albumA))

        viewModel.setIntent(AlbumListViewModel.Intent.LoadAlbums)
        coVerify { checkStoragePermissionUseCase.execute() }

        assert(viewModel.viewAction.value is AlbumListViewModel.ViewAction.RequestStoragePermission)
        viewModel.setIntent(AlbumListViewModel.Intent.GrantStoragePermission)

        coVerify { loadAlbumUseCase.execute() }
        assert(viewModel.viewState.value is AlbumListViewModel.ViewState.Loaded)
        assert((viewModel.viewState.value as AlbumListViewModel.ViewState.Loaded).albumList == listOf(albumA))
    }

    @Test
    fun `화면 로딩 시 저장소 접근권한이 있으면 목록을 로딩한다`() = runTest {
        coEvery { checkStoragePermissionUseCase.execute() } returns flowOf(PermissionStatus.GRANTED)
        coEvery { loadAlbumUseCase.execute() } returns flowOf(listOf(albumA))

        viewModel.setIntent(AlbumListViewModel.Intent.LoadAlbums)
        coVerify { checkStoragePermissionUseCase.execute() }
        coVerify { loadAlbumUseCase.execute() }

        assert(viewModel.viewState.value is AlbumListViewModel.ViewState.Loaded)
        assert((viewModel.viewState.value as AlbumListViewModel.ViewState.Loaded).albumList == listOf(albumA))
    }

    @Test
    fun `로딩한 앨범이 한개도 없다면 관련 에러화면을 보여준다`() = runTest {
        coEvery { checkStoragePermissionUseCase.execute() } returns flowOf(PermissionStatus.GRANTED)
        coEvery { loadAlbumUseCase.execute() } returns flowOf(emptyList())

        viewModel.setIntent(AlbumListViewModel.Intent.LoadAlbums)
        coVerify { checkStoragePermissionUseCase.execute() }
        coVerify { loadAlbumUseCase.execute() }

        assert(viewModel.viewState.value is AlbumListViewModel.ViewState.ErrorEmptyAlbums)
    }

    @Test
    fun `앨범 클릭하면 곡 목록화면으로 넘어간다`() = runTest {
        viewModel.setIntent(AlbumListViewModel.Intent.ClickAlbum(albumB))

        val viewActionValue = viewModel.viewAction.value
        assert(viewActionValue is AlbumListViewModel.ViewAction.MoveMusicList)
        assert((viewActionValue as AlbumListViewModel.ViewAction.MoveMusicList).album == albumB)
    }
}
