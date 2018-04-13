package com.github.leondevlifelog.browser.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.KeyEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import com.github.leondevlifelog.browser.ObversableTabsInfo
import com.github.leondevlifelog.browser.R
import com.github.leondevlifelog.browser.TabsAdapter
import com.github.leondevlifelog.browser.bean.TabInfo
import com.github.leondevlifelog.browser.view.AddressBarView
import com.just.agentweb.AgentWeb
import com.just.agentweb.NestedScrollAgentWebView
import com.jyuesong.android.kotlin.extract._toast
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import kotlinx.android.synthetic.main.activity_browser.*
import kotlinx.android.synthetic.main.bottpm_navigator_bar.*
import kotlinx.android.synthetic.main.view_menu_content.*
import kotlinx.android.synthetic.main.view_tabs_content.*
import java.util.*
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import kotlin.concurrent.schedule

class BrowserActivity : AppCompatActivity() {
    private val TAG: String = "BrowserActivity"
    private var bottomMenuSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var bottomTabsSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private lateinit var tabs: ObversableTabsInfo
    private lateinit var mAgentWeb: AgentWeb

    private lateinit var webView: NestedScrollAgentWebView

    private lateinit var tabsAdapter: TabsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)
        webView = NestedScrollAgentWebView(this)
        val lp = CoordinatorLayout.LayoutParams(-1, -1)
        var scrollingViewBehavior = AppBarLayout.ScrollingViewBehavior()
        lp.behavior = scrollingViewBehavior
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(textContent, 1, lp)//lp记得设置behavior属性
                .useDefaultIndicator()
                .setWebView(webView)
                .setWebChromeClient(object : WebChromeClient() {

                    override fun onReceivedTitle(view: WebView?, title: String?) {
                        tabs.selectedTab?.title = title.toString()
                        tabsAdapter.notifyDataSetChanged()
                        super.onReceivedTitle(view, title)
                    }
                })
                .setWebViewClient(object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        tabs.selectedTab?.url = url.toString()
                        addressBarView.setUrl(url)
                        super.onPageStarted(view, url, favicon)
                    }
                })
                .createAgentWeb()
                .ready()
                .go("file:///android_asset/index.html")
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
            mAgentWeb.urlLoader.loadUrl("file:///android_asset/index.html")
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
        tabs = ObversableTabsInfo()
        tabs.addObserver { o, arg ->
            when (arg) {
                is Int -> {
                    actionTabsNum.text = arg.toString()
                    tabsAdapter.notifyItemInserted(arg - 1)
                }
                is TabInfo -> tabsAdapter.notifyDataSetChanged()
            }
        }
        tabsAdapter = TabsAdapter(this, tabs)
        tabsAdapter.addOnSelectedTabChangedListner(object : TabsAdapter.EventLintener {
            override fun onClosed(v: View, tab: TabInfo) {
            }

            override fun onSelectedChanged(tab: TabInfo) {
                mAgentWeb.urlLoader.loadUrl(tab.url)
                delayCloseTabsContent()
            }
        })

        var linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        rvTabsList.layoutManager = linearLayoutManager
        rvTabsList.adapter = tabsAdapter
        rvTabsList.itemAnimator = SlideInLeftAnimator()
        var simpleItemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                tabs.remove(tabs.get(viewHolder?.adapterPosition!!))
                tabs.selectedTab = tabs.get(0)
                tabsAdapter.notifyDataSetChanged()
            }
        })
        simpleItemTouchHelper.attachToRecyclerView(rvTabsList)
        actionMenuSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        actionMenuExit.setOnClickListener {
            android.os.Process.killProcess(android.os.Process.myPid())
        }
        actionNewTab.setOnClickListener {
            var x = TabInfo()
            tabs.add(x)
            tabs.selectedTab = x
            mAgentWeb.urlLoader.loadUrl(x.url)
            rvTabsList.scrollToPosition(tabs.size() - 1)
            delayCloseTabsContent()
        }
        actionCloseAllTabs.setOnClickListener {
            tabs.clear()
            tabsAdapter.notifyDataSetChanged()
            delayCloseTabsContent()
        }
    }

    private fun delayCloseTabsContent() {
        Timer("closetab", false).schedule(500) {
            bottomTabsSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
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
