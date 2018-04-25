package com.github.leondevlifelog.browser

import android.app.Application
import android.util.Log
import com.androidev.download.DefaultNotifier
import com.androidev.download.DownloadManager
import com.squareup.leakcanary.LeakCanary

import com.tencent.smtt.sdk.QbSdk

/**
 * Created by leon on 2018/3/5.
 */

class App : Application() {
    val TAG: String = "App"

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
        DownloadManager.getInstance().initialize(this, 3)
        DownloadManager.getInstance().setDownloadNotifier(DefaultNotifier(this))
        QbSdk.initBuglyAsync(true)
        QbSdk.initX5Environment(this, object : QbSdk.PreInitCallback {
            override fun onCoreInitFinished() {
                Log.d(TAG, "onCoreInitFinished: 内核初始化结束")
            }

            override fun onViewInitFinished(p0: Boolean) {
                Log.d(TAG, "onViewInitFinished: 视图初始化结束")
            }

        })
    }
}
