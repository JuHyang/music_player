package com.juhyang.musicplayer

import com.juhyang.musicplayer.domain.model.Album
import com.juhyang.musicplayer.domain.model.PermissionStatus
import com.juhyang.musicplayer.domain.usecase.CheckStoragePermissionUseCase
import com.juhyang.musicplayer.domain.usecase.LoadAlbumListUseCase
import com.juhyang.musicplayer.presentation.AlbumListViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


class AlbumViewModelTests : CoroutineTest() {
    private lateinit var viewModel: AlbumListViewModel

    @MockK
    private lateinit var checkStoragePermissionUseCase: CheckStoragePermissionUseCase

    @MockK
    private lateinit var loadAlbumListUseCase: LoadAlbumListUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)

        viewModel = AlbumListViewModel(
            loadAlbumListUseCase,
            checkStoragePermissionUseCase,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
        )
    }

    private val albumA = Album("AlbumA", "artist", null, listOf())
    private val albumB = Album("AlbumB", "artist2", null, listOf())

    @Test
    fun `화면 로딩 시저장소 접근권한이 없다면 요청한다`() = runTest {
        coEvery { checkStoragePermissionUseCase.execute() } returns flowOf(PermissionStatus.REVOKED)

        viewModel.setIntent(AlbumListViewModel.Intent.LoadAlbums)
        coVerify { checkStoragePermissionUseCase.execute() }

        assert(viewModel.viewState.value is AlbumListViewModel.ViewState.ErrorPermissionDenied)
    }

    @Test
    fun `화면 로딩 시저장소 접근권한이 없다면 요청하고, 거절하면 에러화면을 보여준다`() = runTest {
        coEvery { checkStoragePermissionUseCase.execute() } returns flowOf(PermissionStatus.REVOKED)

        viewModel.setIntent(AlbumListViewModel.Intent.LoadAlbums)
        coVerify { checkStoragePermissionUseCase.execute() }
        assert(viewModel.viewState.value is AlbumListViewModel.ViewState.ErrorPermissionDenied)

        viewModel.setIntent(AlbumListViewModel.Intent.RequestStoragePermission)
        assert(viewModel.viewAction.first() is AlbumListViewModel.ViewAction.RequestStoragePermission)
        viewModel.setIntent(AlbumListViewModel.Intent.RevokeStoragePermission)

        assert(viewModel.viewState.value is AlbumListViewModel.ViewState.ErrorPermissionDenied)
    }

    @Test
    fun `화면 로딩 시저장소 접근권한이 없다면 요청하고, 승인하면 목록을 로딩한다`() = runTest {
        coEvery { checkStoragePermissionUseCase.execute() } returns flowOf(PermissionStatus.REVOKED)
        coEvery { loadAlbumListUseCase.execute() } returns flowOf(listOf(albumA))

        viewModel.setIntent(AlbumListViewModel.Intent.LoadAlbums)
        coVerify { checkStoragePermissionUseCase.execute() }
        assert(viewModel.viewState.value is AlbumListViewModel.ViewState.ErrorPermissionDenied)

        viewModel.setIntent(AlbumListViewModel.Intent.RequestStoragePermission)
        assert(viewModel.viewAction.first() is AlbumListViewModel.ViewAction.RequestStoragePermission)
        viewModel.setIntent(AlbumListViewModel.Intent.GrantStoragePermission)

        coVerify { loadAlbumListUseCase.execute() }
        assert(viewModel.viewState.value is AlbumListViewModel.ViewState.Loaded)
        assert((viewModel.viewState.value as AlbumListViewModel.ViewState.Loaded).albumList == listOf(albumA))
    }

    @Test
    fun `화면 로딩 시 저장소 접근권한이 있으면 목록을 로딩한다`() = runTest {
        coEvery { checkStoragePermissionUseCase.execute() } returns flowOf(PermissionStatus.GRANTED)
        coEvery { loadAlbumListUseCase.execute() } returns flowOf(listOf(albumA))

        viewModel.setIntent(AlbumListViewModel.Intent.LoadAlbums)
        coVerify { checkStoragePermissionUseCase.execute() }
        coVerify { loadAlbumListUseCase.execute() }

        assert(viewModel.viewState.value is AlbumListViewModel.ViewState.Loaded)
        assert((viewModel.viewState.value as AlbumListViewModel.ViewState.Loaded).albumList == listOf(albumA))
    }

    @Test
    fun `로딩한 앨범이 한개도 없다면 관련 에러화면을 보여준다`() = runTest {
        coEvery { checkStoragePermissionUseCase.execute() } returns flowOf(PermissionStatus.GRANTED)
        coEvery { loadAlbumListUseCase.execute() } returns flowOf(emptyList())

        viewModel.setIntent(AlbumListViewModel.Intent.LoadAlbums)
        coVerify { checkStoragePermissionUseCase.execute() }
        coVerify { loadAlbumListUseCase.execute() }

        assert(viewModel.viewState.value is AlbumListViewModel.ViewState.ErrorEmptyAlbums)
    }

    @Test
    fun `앨범 클릭하면 곡 목록화면으로 넘어간다`() = runTest {
        viewModel.setIntent(AlbumListViewModel.Intent.ClickAlbum(albumB))

        val viewActionValue = viewModel.viewAction.first()
        assert(viewActionValue is AlbumListViewModel.ViewAction.MoveAlbumDetailScreen)
        assert((viewActionValue as AlbumListViewModel.ViewAction.MoveAlbumDetailScreen).album == albumB)
    }
}
