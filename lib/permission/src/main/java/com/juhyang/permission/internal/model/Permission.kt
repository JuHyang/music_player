package com.juhyang.permission.internal.model

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat


internal abstract class Permission {
    abstract val manifestPermission: String

    open fun isNeedToRequestPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, manifestPermission) != PackageManager.PERMISSION_GRANTED
    }
}
