package com.juhyang.permission.internal.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.juhyang.permission.GrantStatus
import com.juhyang.permission.PermissionChecker
import com.juhyang.permission.PermissionResult
import kotlinx.coroutines.launch


internal class PermissionActivity: AppCompatActivity() {

    private var requestPermissionList: MutableList<String> = mutableListOf()

    companion object {
        private const val PERMISSION_LIST_KEY = "permission_list"
        fun getIntent(context: Context, permissionList: Array<String>): Intent {
            return Intent(context, PermissionActivity::class.java).apply {
                putExtra(PERMISSION_LIST_KEY, permissionList)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val manifestPermissionArray = intent.getStringArrayExtra(PERMISSION_LIST_KEY)
        if (!manifestPermissionArray.isNullOrEmpty()) {
            requestPermissionList.addAll(manifestPermissionArray.toList())
            ActivityCompat.requestPermissions(this, manifestPermissionArray, 0)
        } else {
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val permissionResultList: List<PermissionResult> = grantResults.zip(permissions) { grantResult, permission ->
            val permissionGrantStatus = if (grantResult == PackageManager.PERMISSION_GRANTED) {
                GrantStatus.GRANTED
            } else {
                GrantStatus.REVOKED
            }
            PermissionResult(permission, permissionGrantStatus)
        }

        lifecycleScope.launch {
            PermissionChecker.instance.permissionResultFlow.emit(permissionResultList)
        }
        finish()
    }
}
