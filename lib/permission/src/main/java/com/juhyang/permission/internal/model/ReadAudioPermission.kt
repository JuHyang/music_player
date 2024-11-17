package com.juhyang.permission.internal.model

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.juhyang.permission.GrantStatus


internal class ReadAudioPermission: Permission() {
    companion object {
        fun getManifestPermission(): String {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        }
    }

    override val manifestPermission = getManifestPermission()

    override fun isNeedToRequestPermission(context: Context): Boolean {
        return checkPermission(context, getManifestPermission()) == GrantStatus.REVOKED
    }

    private fun checkPermission(context: Context, manifestPermission: String): GrantStatus {
        return if (ContextCompat.checkSelfPermission(context, manifestPermission) == PackageManager.PERMISSION_GRANTED) {
            GrantStatus.GRANTED
        } else {
            GrantStatus.REVOKED
        }
    }
}
