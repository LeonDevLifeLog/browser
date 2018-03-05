package com.github.leondevlifelog.browser

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tencent.smtt.sdk.CookieSyncManager
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import com.tencent.smtt.utils.TbsLog
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 首页
 *
 * @author Leon <leondevlifelog@gmail.com>
 * @version v0.0.1
 * @date 2018-03-05 22:28
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
        setContentView(R.layout.activity_main)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(web: WebView?, url: String?): Boolean {
                return false
            }
        }
        val webSetting = webView.settings
        webSetting.allowFileAccess = true
        webSetting.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        webSetting.setSupportZoom(true)
        webSetting.builtInZoomControls = true
        webSetting.useWideViewPort = true
        webSetting.setSupportMultipleWindows(false)
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true)
        // webSetting.setDatabaseEnabled(true);
        webSetting.domStorageEnabled = true
        webSetting.javaScriptEnabled = true
        webSetting.setGeolocationEnabled(true)
        webSetting.setAppCacheMaxSize(java.lang.Long.MAX_VALUE)
        webSetting.setAppCachePath(this.getDir("appcache", 0).path)
        webSetting.databasePath = this.getDir("databases", 0).path
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .path)
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);
        val time = System.currentTimeMillis()
        TbsLog.d("time-cost", "cost time: " + (System.currentTimeMillis() - time))
        CookieSyncManager.createInstance(this)
        CookieSyncManager.getInstance().sync()
        webView.loadUrl("http://www.baidu.com")
    }
}
