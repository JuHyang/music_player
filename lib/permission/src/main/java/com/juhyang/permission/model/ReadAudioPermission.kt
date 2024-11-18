package com.juhyang.permission.model

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.juhyang.permission.GrantStatus


class ReadAudioPermission : Permission() {

    override val manifestPermission by lazy { getReadAudioManifestPermission() }

    override fun isNeedToRequestPermission(context: Context): Boolean {
        return checkPermission(context, manifestPermission) == GrantStatus.REVOKED
    }

    private fun checkPermission(context: Context, manifestPermission: String): GrantStatus {
        return if (ContextCompat.checkSelfPermission(context, manifestPermission) == PackageManager.PERMISSION_GRANTED) {
            GrantStatus.GRANTED
        } else {
            GrantStatus.REVOKED
        }
    }

    private fun getReadAudioManifestPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }
}
