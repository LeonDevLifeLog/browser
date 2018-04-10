package com.github.leondevlifelog.browser.ui

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import com.github.leondevlifelog.browser.R
import com.github.leondevlifelog.browser.TabsAdapter
import com.github.leondevlifelog.browser.view.AddressBarView
import com.just.agentweb.AgentWeb
import com.just.agentweb.NestedScrollAgentWebView
import com.jyuesong.android.kotlin.extract._toast
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.android.synthetic.main.bottpm_navigator_bar.*
import kotlinx.android.synthetic.main.view_tabs_content.*
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

class TestActivity : AppCompatActivity() {
    private val TAG: String = "TestActivity"
    private var bottomMenuSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var bottomTabsSheetBehavior: BottomSheetBehavior<LinearLayout>? = null

    private lateinit var mAgentWeb: AgentWeb

    private lateinit var webView: NestedScrollAgentWebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        webView = NestedScrollAgentWebView(this)

        val lp = CoordinatorLayout.LayoutParams(-1, -1)
        lp.behavior = AppBarLayout.ScrollingViewBehavior()

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(textContent, 1, lp)//lp记得设置behavior属性
                .useDefaultIndicator()
                .setWebView(webView)
                .createAgentWeb()
                .ready()
                .go("https://m.baidu.com/")
        addressBarView.onActionButtonClickListener = object : AddressBarView.OnActionButtonClickListener {
            override fun onSecurityBtnClick(v: View) {
            }

            override fun onNoneSecurityBtnClick(v: View) {
            }

            override fun onSearchBtnClick(v: View) {
            }

            override fun onInputDone(urlOrKeyWord: String) {
                if (isUrl(urlOrKeyWord)) {
                    mAgentWeb.urlLoader.loadUrl(urlOrKeyWord)
                } else {
                    mAgentWeb.urlLoader.loadUrl("https://m.baidu.com/s?&wd=$urlOrKeyWord")
                }
            }

            override fun onScanBtnClick(v: View) {
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
        rvTabsList.layoutManager = LinearLayoutManager(this)
        rvTabsList.adapter = TabsAdapter(this)
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
    fun isUrl(str: String): Boolean {
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
}
