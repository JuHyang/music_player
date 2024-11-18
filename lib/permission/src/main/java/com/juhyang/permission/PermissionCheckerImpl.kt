package com.juhyang.permission

import android.content.Context
import android.content.SharedPreferences
import com.juhyang.permission.internal.activity.PermissionActivity
import com.juhyang.permission.internal.activity.PermissionForwardSettingsActivity
import com.juhyang.permission.model.Permission
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map


internal class PermissionCheckerImpl private constructor(): PermissionChecker {
    companion object {
        val instance: PermissionCheckerImpl by lazy { PermissionCheckerImpl() }
    }

    val permissionResultFlow: MutableSharedFlow<List<PermissionResult>> = MutableSharedFlow()

    override fun requestPermissionIfNeeded(context: Context, permission: Permission, isRequired: Boolean): Flow<PermissionResult> {
        if (isFirstPermissionRequest(context, permission.manifestPermission)) {
            setPermissionRequested(context, permission.manifestPermission)

            return requestPermission(context, permission)
        } else {
            if (isRequired) {
                return startSettingsForwardPermissionActivity(context, permission.manifestPermission)
            } else {
                return flowOf(PermissionResult(permission.manifestPermission, GrantStatus.REVOKED))
            }
        }
    }

    override fun isGrantedPermission(context: Context, permission: Permission): Boolean {
        return !permission.isNeedToRequestPermission(context)
    }

    private fun startSettingsForwardPermissionActivity(context: Context, manifestPermission: String): Flow<PermissionResult> {
        return startSettingsForwardActivity(context, manifestPermission)
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
        val activityIntent = PermissionForwardSettingsActivity.getIntent(context, manifestPermission)

        context.startActivity(activityIntent)

        return permissionResultFlow
            .filter { it.isNotEmpty() }
            .map {
                it.first()
            }
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("PermissionPrefs", Context.MODE_PRIVATE)
    }

    private fun isFirstPermissionRequest(context: Context, permission: String): Boolean {
        // 권한 요청 여부 확인
        return getSharedPreferences(context).getBoolean(permission, true) // 기본값: 처음 요청
    }

    private fun setPermissionRequested(context: Context, permission: String) {
        // 권한 요청 기록
        getSharedPreferences(context).edit().putBoolean(permission, false).apply()
    }
}
