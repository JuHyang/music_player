package com.juhyang.permission.internal.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.juhyang.permission.GrantStatus
import com.juhyang.permission.PermissionChecker
import com.juhyang.permission.PermissionResult
import com.juhyang.permission.internal.PermissionMapper
import kotlinx.coroutines.launch


internal class PermissionForwardSettingsActivity : AppCompatActivity() {
    companion object {
        private const val MANIFEST_PERMISSION_NAME = "permission_name"
        fun getIntent(context: Context, manifestPermission: String): Intent {
            return Intent(context, PermissionForwardSettingsActivity::class.java).apply {
                putExtra(MANIFEST_PERMISSION_NAME, manifestPermission)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
    }

    private lateinit var launcher: ActivityResultLauncher<Intent>
    private val permissionMapper = PermissionMapper()
    private lateinit var manifestPermission: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)

        checkManifestPermissionEmpty()
        initActivityLauncher()

        startSettings()
    }

    private fun checkManifestPermissionEmpty() {
        manifestPermission = intent.getStringExtra(MANIFEST_PERMISSION_NAME) ?: ""
        if (manifestPermission.isEmpty()) {
            lifecycleScope.launch {
                PermissionChecker.instance.permissionResultFlow.emit(listOf(PermissionResult(manifestPermission, GrantStatus.REVOKED)))
            }
            finish()
        }
    }

    private fun initActivityLauncher() {
        this.launcher = registerForActivityResult(StartActivityForResult()) {
            val permission = permissionMapper.map(manifestPermission)
            if (permission == null) {
                finish()
                return@registerForActivityResult
            }

            val grantStatus = if (permission.isNeedToRequestPermission(this)) {
                GrantStatus.REVOKED
            } else {
                GrantStatus.GRANTED
            }

            lifecycleScope.launch {
                PermissionChecker.instance.permissionResultFlow.emit(listOf(PermissionResult(manifestPermission, grantStatus)))
            }

            finish()
        }
    }

    private fun startSettings() {
        val intent: Intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:${applicationInfo.packageName}"))

        launcher.launch(intent)
    }
}
