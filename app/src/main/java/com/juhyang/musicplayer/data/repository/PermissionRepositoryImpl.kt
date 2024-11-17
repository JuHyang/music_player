package com.juhyang.musicplayer.data.repository

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.juhyang.musicplayer.domain.model.PermissionStatus
import com.juhyang.musicplayer.domain.repository.PermissionRepository
import com.juhyang.permission.PermissionChecker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class PermissionRepositoryImpl(
    private val context: Context,
    private val permissionChecker: PermissionChecker,
): PermissionRepository {
    companion object {
        const val READ_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        const val READ_MEDIA_AUDIO_PERMISSION = Manifest.permission.READ_MEDIA_AUDIO
    }
    override suspend fun isGrantStoragePermission(): Flow<PermissionStatus> {
        val grantStatus = permissionChecker.isReadAudioPermissionGranted(context)

        val result = if (grantStatus) {
            PermissionStatus.GRANTED
        } else {
            PermissionStatus.REVOKED
        }

        return flowOf(result)
    }
}
