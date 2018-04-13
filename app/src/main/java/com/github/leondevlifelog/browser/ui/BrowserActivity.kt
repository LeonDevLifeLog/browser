package com.github.leondevlifelog.browser.ui

import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import com.github.leondevlifelog.browser.R
import com.github.leondevlifelog.browser.TabsAdapter
import com.github.leondevlifelog.browser.bean.TabInfo
import com.github.leondevlifelog.browser.view.AddressBarView
import com.just.agentweb.AgentWeb
import com.just.agentweb.NestedScrollAgentWebView
import com.jyuesong.android.kotlin.extract._toast
import kotlinx.android.synthetic.main.activity_browser.*
import kotlinx.android.synthetic.main.bottpm_navigator_bar.*
import kotlinx.android.synthetic.main.view_menu_content.*
import kotlinx.android.synthetic.main.view_tabs_content.*
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

class BrowserActivity : AppCompatActivity() {
    private val TAG: String = "BrowserActivity"
    private var bottomMenuSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var bottomTabsSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private lateinit var tabs: MutableList<TabInfo>
    private lateinit var mAgentWeb: AgentWeb

    private lateinit var webView: NestedScrollAgentWebView

    private lateinit var tabsAdapter: TabsAdapter

    private lateinit var mutableLiveData: MutableLiveData<MutableList<TabInfo>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)
        webView = NestedScrollAgentWebView(this)
        val lp = CoordinatorLayout.LayoutParams(-1, -1)
        lp.behavior = AppBarLayout.ScrollingViewBehavior()
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(textContent, 1, lp)//lp记得设置behavior属性
                .useDefaultIndicator()
                .setWebView(webView)
                .setWebViewClient(object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        addressBarView.setUrl(url)
                        super.onPageFinished(view, url)
                    }
                })
                .createAgentWeb()
                .ready()
                .go("https://m.baidu.com/")
        mAgentWeb.webCreator.webView.requestFocus()
        addressBarView.onActionButtonClickListener = object : AddressBarView.OnActionButtonClickListener {
            override fun onSecurityBtnClick(v: View) {
            }

            override fun onNoneSecurityBtnClick(v: View) {
            }

            override fun onSearchBtnClick(v: View) {
            }

            override fun onInputDone(urlOrKeyWord: String) {
                openUrlOrSearch(urlOrKeyWord)
            }

            override fun onScanBtnClick(v: View) {
                var intent = Intent(this@BrowserActivity, ScanActivity::class.java)
                startActivityForResult(intent, ScanActivity.REQUEST_SCAN_CODE)
            }
        }
        bottomMenuSheetBehavior = BottomSheetBehavior.from(bottomSheetMenu)
        bottomTabsSheetBehavior = BottomSheetBehavior.from(bottomTabsMenu)
        actionMenu.setOnClickListener({
            if (bottomMenuSheetBehavior?.state == BottomSheetBehavior.STATE_COLLAPSED)
                bottomMenuSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            else
                bottomMenuSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        })
        actionTabs.setOnClickListener({
            if (bottomTabsSheetBehavior?.state == BottomSheetBehavior.STATE_COLLAPSED)
                bottomTabsSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            else
                bottomTabsSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        })
        actionHome.setOnClickListener({
            mAgentWeb.urlLoader.loadUrl("https://m.baidu.com")
        })
        actionForward.setOnClickListener({
            if (mAgentWeb.webCreator.webView.canGoForward()) {
                mAgentWeb.webCreator.webView.goForward()
            } else {
                _toast("不能前进了")
            }
        })
        actionBack.setOnClickListener({
            if (mAgentWeb.webCreator.webView.canGoBack()) {
                mAgentWeb.webCreator.webView.goBack()
            } else {
                _toast("不能后退了")
            }
        })
        tabs = mutableListOf(TabInfo(getString(R.string.title_home), getString(R.string.url_home)))
        tabsAdapter = TabsAdapter(this, tabs)
        rvTabsList.layoutManager = LinearLayoutManager(this)
        rvTabsList.itemAnimator = DefaultItemAnimator()
        rvTabsList.adapter = tabsAdapter

        actionMenuSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        actionMenuExit.setOnClickListener {
            android.os.Process.killProcess(android.os.Process.myPid())
        }
        actionNewTab.setOnClickListener {
            tabs.add(TabInfo(getString(R.string.title_home), getString(R.string.url_home)))
            tabsAdapter.notifyItemInserted(tabs.size - 1)
            rvTabsList.scrollToPosition(tabs.size - 1)
        }
    }

    private fun openUrlOrSearch(urlOrKeyWord: String?) {
        if (isUrl(urlOrKeyWord)) {
            mAgentWeb.urlLoader.loadUrl(urlOrKeyWord)
        } else {
            mAgentWeb.urlLoader.loadUrl("https://m.baidu.com/s?&wd=$urlOrKeyWord")
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 判断字串是不是url链接
     *@param str url字符串
     */
    @Throws(PatternSyntaxException::class)
    fun isUrl(str: String?): Boolean {
        val regExp = "\\b((https?|http):\\/\\/)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[A-Za-z]{2,6}\\b(\\/[-a-zA-Z0-9@:%_\\+.~#?&//=]*)*(?:\\/|\\b)"
        val p = Pattern.compile(regExp)
        val m = p.matcher(str)
        return m.matches()
    }

    override fun onPause() {
        mAgentWeb.webLifeCycle.onPause()
        super.onPause()
    }

    override fun onResume() {
        mAgentWeb.webLifeCycle.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        mAgentWeb.webLifeCycle.onDestroy()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ScanActivity.REQUEST_SCAN_CODE && resultCode == Activity.RESULT_OK) {
            var result = data?.getStringExtra(ScanActivity.KEY_SCAN_RESULT)
            openUrlOrSearch(result)
        }
    }
}
