package com.juhyang.musicplayer.domain.repository

import com.juhyang.musicplayer.domain.model.PermissionStatus
import kotlinx.coroutines.flow.Flow


interface PermissionRepository {
    suspend fun isGrantStoragePermission(): Flow<PermissionStatus>
}
