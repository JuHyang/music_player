package com.juhyang.musicplayer.domain.usecase

import com.juhyang.musicplayer.domain.model.PermissionStatus
import com.juhyang.musicplayer.domain.repository.PermissionRepository
import kotlinx.coroutines.flow.Flow


class CheckStoragePermissionUseCaseImpl(
    private val permissionRepository: PermissionRepository
): CheckStoragePermissionUseCase {
    override suspend fun execute(): Flow<PermissionStatus> {
        return permissionRepository.isGrantStoragePermission()
    }
}
