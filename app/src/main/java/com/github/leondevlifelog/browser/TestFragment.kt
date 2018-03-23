package com.github.leondevlifelog.browser

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tencent.smtt.sdk.CookieSyncManager
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import com.tencent.smtt.utils.TbsLog


class TestFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater!!.inflate(R.layout.fragment_test, container, false)
        var webView = view.findViewById<WebView>(R.id.mWebView)
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
        webSetting.setAppCachePath(activity!!.getDir("appcache", 0).path)
        webSetting.databasePath = activity!!.getDir("databases", 0).path
        webSetting.setGeolocationDatabasePath(activity!!.getDir("geolocation", 0)
                .path)
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);
        val time = System.currentTimeMillis()
        TbsLog.d("time-cost", "cost time: " + (System.currentTimeMillis() - time))
        CookieSyncManager.createInstance(activity)
        CookieSyncManager.getInstance().sync()
        webView.loadUrl("http://www.baidu.com")
        return view
    }

    companion object {
        fun newInstance(): TestFragment {
            val fragment = TestFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

}
