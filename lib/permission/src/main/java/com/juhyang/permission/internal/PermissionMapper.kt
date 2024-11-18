package com.juhyang.permission.internal

import android.Manifest
import com.juhyang.permission.model.NotificationPermission
import com.juhyang.permission.model.Permission
import com.juhyang.permission.model.ReadAudioPermission

internal class PermissionMapper {
    fun map(permission: String): Permission? {
        return when (permission) {
            Manifest.permission.POST_NOTIFICATIONS -> {
                NotificationPermission()
            }
            Manifest.permission.READ_MEDIA_AUDIO -> {
                ReadAudioPermission()
            }
            Manifest.permission.READ_EXTERNAL_STORAGE -> {
                ReadAudioPermission()
            }
            else -> {
                null
            }
        }
    }
}
