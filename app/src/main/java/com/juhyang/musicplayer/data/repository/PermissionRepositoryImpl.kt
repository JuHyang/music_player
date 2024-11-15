package com.juhyang.musicplayer.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.juhyang.musicplayer.domain.model.PermissionStatus
import com.juhyang.musicplayer.domain.repository.PermissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class PermissionRepositoryImpl(
    private val context: Context
): PermissionRepository {
    companion object {
        private const val READ_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private const val READ_MEDIA_AUDIO_PERMISSION = Manifest.permission.READ_MEDIA_AUDIO
    }
    override suspend fun isGrantStoragePermission(): Flow<PermissionStatus> {
        val permissionStatus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermission(READ_MEDIA_AUDIO_PERMISSION)
        } else {
            checkPermission(READ_EXTERNAL_STORAGE_PERMISSION)
        }
        return flowOf(permissionStatus)
    }

    private fun checkPermission(manifestPermission: String): PermissionStatus {
        return if (ContextCompat.checkSelfPermission(context, manifestPermission) == PackageManager.PERMISSION_GRANTED) {
            PermissionStatus.GRANTED
        } else {
            PermissionStatus.REVOKED
        }
    }
}
