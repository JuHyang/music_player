package com.juhyang.musicplayer.data.repository

import android.content.Context
import com.juhyang.musicplayer.domain.model.PermissionStatus
import com.juhyang.musicplayer.domain.repository.PermissionRepository
import com.juhyang.permission.PermissionChecker
import com.juhyang.permission.model.ReadAudioPermission
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class PermissionRepositoryImpl(
    private val context: Context,
    private val permissionChecker: PermissionChecker,
): PermissionRepository {

    override suspend fun isGrantStoragePermission(): Flow<PermissionStatus> {
        val grantStatus = permissionChecker.isGrantedPermission(context, ReadAudioPermission())

        val result = if (grantStatus) {
            PermissionStatus.GRANTED
        } else {
            PermissionStatus.REVOKED
        }

        return flowOf(result)
    }
}
