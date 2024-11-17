package com.juhyang.permission

import android.content.Context
import android.util.Log
import com.juhyang.permission.internal.activity.PermissionActivity
import com.juhyang.permission.internal.activity.PermissionForwardSettingsActivity
import com.juhyang.permission.internal.model.NotificationPermission
import com.juhyang.permission.internal.model.Permission
import com.juhyang.permission.internal.model.ReadAudioPermission
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map


public class PermissionChecker private constructor() {
    companion object {
        val instance: PermissionChecker by lazy { PermissionChecker() }
    }

    internal val permissionResultFlow: MutableSharedFlow<List<PermissionResult>> = MutableSharedFlow()

    fun requestNotificationPermissionIfNeeded(context: Context): Flow<PermissionResult> {
        return requestPermission(context, NotificationPermission())
    }

    fun isNotificationPermissionGranted(context: Context): Boolean {
        return isGrantedPermission(context, NotificationPermission())
    }

    fun requestReadAudioPermissionIfNeeded(context: Context): Flow<PermissionResult> {
        return requestPermission(context, ReadAudioPermission())
    }

    fun isReadAudioPermissionGranted(context: Context): Boolean {
        return isGrantedPermission(context, ReadAudioPermission())
    }

    fun startSettingsForwardNotificationPermissionActivity(context: Context): Flow<PermissionResult> {
        return startSettingsForwardPermissionActivity(context, NotificationPermission().manifestPermission)
    }

    fun startSettingsForwardReadAudioPermissionActivity(context: Context): Flow<PermissionResult> {
        return startSettingsForwardPermissionActivity(context, ReadAudioPermission().manifestPermission)
    }

    private fun startSettingsForwardPermissionActivity(context: Context, manifestPermission: String): Flow<PermissionResult> {
        Log.d("##Arthur", "PermissionChecker startSettingsForwardPermissionActivity: manifestPErmission : ${manifestPermission}")
        return startSettingsForwardActivity(context, manifestPermission)
    }

    private fun isGrantedPermission(context: Context, permission: Permission): Boolean {
        return !permission.isNeedToRequestPermission(context)
    }

    private fun requestPermission(context: Context, permission: Permission): Flow<PermissionResult> {
        return requestPermissions(context, listOf(permission))
            .filter { it.isNotEmpty() }
            .map { it.first() }
    }

    private fun requestPermissions(context: Context, permissionList: List<Permission>): Flow<List<PermissionResult>> {
        val permissionRequestList: MutableList<Permission> = mutableListOf()
        val permissionResultList: MutableList<PermissionResult> = mutableListOf()
        for (permission in permissionList) {
            if (permission.isNeedToRequestPermission(context)) {
                permissionRequestList.add(permission)
            } else {
                permissionResultList.add(PermissionResult(permission.manifestPermission, GrantStatus.GRANTED))
            }
        }

        return startPermissionActivity(context, permissionList, permissionResultList)
    }

    private fun startPermissionActivity(context: Context, permissionList: List<Permission>, permissionResultList: List<PermissionResult>): Flow<List<PermissionResult>> {
        if (permissionList.isEmpty()) {
            return flowOf(permissionResultList)
        }
        val permissionStringArray = permissionList.map { it.manifestPermission }.toTypedArray()
        val activityIntent = PermissionActivity.getIntent(context, permissionStringArray)

        context.startActivity(activityIntent)

        return permissionResultFlow
    }

    private fun startSettingsForwardActivity(context: Context, manifestPermission: String): Flow<PermissionResult> {
        Log.d("##Arthur", "PermissionChecker startSettingsForwardActivity: manifestPermission : ${manifestPermission}")
        val activityIntent = PermissionForwardSettingsActivity.getIntent(context, manifestPermission)

        context.startActivity(activityIntent)

        return permissionResultFlow
            .filter { it.isNotEmpty() }
            .map {
                it.first()
            }
    }
}
