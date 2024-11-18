package com.juhyang.permission

import android.content.Context
import com.juhyang.permission.model.Permission
import kotlinx.coroutines.flow.Flow


public interface PermissionChecker {
    companion object {
        val instance: PermissionChecker by lazy { PermissionCheckerImpl.instance }
    }

    fun requestPermissionIfNeeded(context: Context, permission: Permission, isRequired: Boolean): Flow<PermissionResult>
    fun isGrantedPermission(context: Context, permission: Permission): Boolean
}
