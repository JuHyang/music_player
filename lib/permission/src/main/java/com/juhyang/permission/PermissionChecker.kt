package com.juhyang.permission

import android.content.Context
import kotlinx.coroutines.flow.Flow


public interface PermissionChecker {
    companion object {
        val instance: PermissionChecker by lazy { PermissionCheckerImpl.instance }
    }

    fun requestNotificationPermissionIfNeeded(context: Context): Flow<PermissionResult>
    fun isNotificationPermissionGranted(context: Context): Boolean
    fun requestReadAudioPermissionIfNeeded(context: Context): Flow<PermissionResult>
    fun isReadAudioPermissionGranted(context: Context): Boolean
    fun startSettingsForwardNotificationPermissionActivity(context: Context): Flow<PermissionResult>
    fun startSettingsForwardReadAudioPermissionActivity(context: Context): Flow<PermissionResult>
}
