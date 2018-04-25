package com.github.leondevlifelog.browser.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import com.github.leondevlifelog.browser.BuildConfig
import com.github.leondevlifelog.browser.R
import com.jyuesong.android.kotlin.extract._toast
import kotlinx.android.synthetic.main.activity_scan.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class ScanActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_CODE_QRCODE_PERMISSIONS = 1
        const val KEY_SCAN_RESULT = "KEY_SCAN_RESULT"
        const val REQUEST_SCAN_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        setSupportActionBar(toolbarScan)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)
        supportActionBar?.title = "扫一扫"
        scannerView.setOnScannerCompletionListener { rawResult, parsedResult, barcode ->
            var intent = Intent()
            intent.putExtra(KEY_SCAN_RESULT, parsedResult.displayResult)
            if (BuildConfig.DEBUG)
                _toast("扫描成功!\n${parsedResult.displayResult}")
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        Handler().postDelayed({
            needsPermissionWithPermissionCheck()
        }, 200)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        scannerView.onResume()
        super.onResume()
    }

    override fun onPause() {
        scannerView.onPause()
        super.onPause()
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun needsPermission() {
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    fun onCameraDenied() {
        Toast.makeText(this, R.string.permission_camera_denied, Toast.LENGTH_SHORT).show()
    }

}
