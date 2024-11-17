package com.juhyang.permission.internal.model

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.os.Build


internal class NotificationPermission: Permission() {
    companion object {
        const val MANIFEST_PERMISSION = Manifest.permission.POST_NOTIFICATIONS
    }

    override val manifestPermission = MANIFEST_PERMISSION

    override fun isNeedToRequestPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return false
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return !notificationManager.areNotificationsEnabled()
    }
}
