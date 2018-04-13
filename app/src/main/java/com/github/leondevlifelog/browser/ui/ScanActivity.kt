package com.github.leondevlifelog.browser.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.github.leondevlifelog.browser.R
import com.jyuesong.android.kotlin.extract._toast
import kotlinx.android.synthetic.main.activity_scan.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class ScanActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_CODE_QRCODE_PERMISSIONS = 1
        const val KEY_SCAN_RESULT = "KEY_SCAN_RESULT"
        const val REQUEST_SCAN_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        zxingView.setDelegate(object : QRCodeView.Delegate {
            override fun onScanQRCodeSuccess(result: String?) {
                var intent = Intent()
                intent.putExtra(KEY_SCAN_RESULT, result)
                _toast("扫描成功!")
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

            override fun onScanQRCodeOpenCameraError() {
                _toast("相机打开出错")
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    fun onPermissionsGranted(requestCode: Int, perms: List<String>) {}

    fun onPermissionsDenied(requestCode: Int, perms: List<String>) {}

    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private fun requestCodeQRCodePermissions() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和散光灯的权限",
                    REQUEST_CODE_QRCODE_PERMISSIONS, Manifest.permission.CAMERA)
        }
    }
}
