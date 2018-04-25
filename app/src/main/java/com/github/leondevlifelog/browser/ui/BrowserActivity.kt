package com.github.leondevlifelog.browser.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
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
import com.github.leondevlifelog.browser.database.AppDatabaseImpl
import com.github.leondevlifelog.browser.database.entities.BookMark
import com.github.leondevlifelog.browser.database.entities.History
import com.github.leondevlifelog.browser.view.AddressBarView
import com.github.leondevlifelog.browser.view.MyWebView
import com.just.agentweb.AgentWeb
import com.jyuesong.android.kotlin.extract._dip2px
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
    private var bottomToolsSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private lateinit var tabs: ObversableTabsInfo
    private lateinit var mAgentWeb: AgentWeb

    private lateinit var webView: MyWebView

    private lateinit var tabsAdapter: TabsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)
        initWebView()
        initSwipeRefresh()
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
        bottomToolsSheetBehavior = BottomSheetBehavior.from(bottomToolsMenu)
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
            override fun onClosed(v: View, tab: TabInfo?) {
                mAgentWeb.urlLoader.loadUrl(tab?.url)
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
            if (tabs.selectedTab != x)
                mAgentWeb.urlLoader.loadUrl(x.url)
            tabs.selectedTab = x
            rvTabsList.scrollToPosition(tabs.size() - 1)
            delayCloseTabsContent()
        }
        actionCloseAllTabs.setOnClickListener {
            tabs.clear()
            tabsAdapter.notifyDataSetChanged()
            delayCloseTabsContent()
        }
        actionMenuHistory.setOnClickListener {
            startActivityForResult(Intent(this@BrowserActivity, HistoryActivity::class.java),
                    HistoryActivity.REQUEST_HISTORY)
        }
        actionMenuBookmark.setOnClickListener {
            startActivityForResult(Intent(this@BrowserActivity, BookmarkActivity::class.java),
                    BookmarkActivity.REQUEST_BOOKMARK)
        }
        actionMenuShare.setOnClickListener {
            var shareIntent = Intent()
            //设置分享行为
            shareIntent.action = Intent.ACTION_SEND
            //设置分享内容的类型
            shareIntent.type = "text/plain"
            //添加分享内容标题
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "我正在浏览一个干货满满的网站,你也看一看吧~")
            //添加分享内容
            shareIntent.putExtra(Intent.EXTRA_TEXT, "${tabs.selectedTab?.title}\n${tabs.selectedTab?.url}")
            //创建分享的Dialog
            shareIntent = Intent.createChooser(shareIntent, "分享至")
            this@BrowserActivity.startActivity(shareIntent)
        }
        actionMenuDownload.setOnClickListener {
            startActivity(Intent(this@BrowserActivity, DownLoadActivity::class.java))
        }
        actionMenuAddBookmark.setOnClickListener {

            var dialogBuilder = AlertDialog.Builder(this@BrowserActivity)
            var dialogView = layoutInflater.inflate(R.layout.dialog_add_bookmark, null)
            var title = dialogView.findViewById<TextInputEditText>(R.id.dialogWebTitle)
            title.setText(this@BrowserActivity.tabs.selectedTab?.title)
            var url = dialogView.findViewById<TextInputEditText>(R.id.dialogWebUrl)
            url.setText(this@BrowserActivity.tabs.selectedTab?.url)
            dialogBuilder.setView(dialogView)
            dialogBuilder.setTitle("保存书签")
            dialogBuilder.setPositiveButton("保存", { dialog, which ->
                AsyncTask.execute {
                    AppDatabaseImpl.instance.bookMarkDao().insert(BookMark(title = title.text.toString(),
                            url = url.text.toString(), time = Date()))
                }
            })
            dialogBuilder.setNegativeButton("取消", { dialog, which ->
                dialog.dismiss()
            })
            dialogBuilder.show()
        }
        actionMenuToolbox.setOnClickListener {
            bottomMenuSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomToolsSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    /**
     * 初始化下拉刷新
     */
    private fun initSwipeRefresh() {
        srlMain.setOnRefreshListener {
            mAgentWeb.urlLoader.reload()
        }
        srlMain.setProgressViewOffset(false, _dip2px(52), _dip2px(120))
        srlMain.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light)
        srlMain.setDistanceToTriggerSync(_dip2px(120))
    }

    /**
     * 初始化webview
     */
    private fun initWebView() {
        webView = MyWebView(this)
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
                        var url = mAgentWeb.webCreator.webView.url.toString()
                        tabsAdapter.notifyDataSetChanged()
                        super.onReceivedTitle(view, title)
                        srlMain.isRefreshing = false
                        if ("file:///android_asset/index.html" != url)
                            AsyncTask.execute({
                                AppDatabaseImpl.instance.historyDao()
                                        .insert(History(title = title.toString(), url = url, time = Date()))
                            })
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
        webView.changeListener = object : MyWebView.OnScrollChangeListener {
            //滑动webView时确定下拉刷新是否可以使用
            override fun onPageEnd(l: Int, t: Int, oldl: Int, oldt: Int) {
                srlMain.isEnabled = false
            }

            override fun onPageTop(l: Int, t: Int, oldl: Int, oldt: Int) {
                srlMain.isEnabled = true
            }

            override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
                srlMain.isEnabled = false
            }

        }
    }

    /**
     * 延时关闭tabs面板
     */
    private fun delayCloseTabsContent() {
        Timer("closetab", false).schedule(500) {
            bottomTabsSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun openUrlOrSearch(urlOrKeyWord: String?) {
        if (isUrl(urlOrKeyWord)) {
            if (!urlOrKeyWord?.startsWith("http")!! and !urlOrKeyWord.startsWith("https")) {
                mAgentWeb.urlLoader.loadUrl("http://$urlOrKeyWord")
            } else {
                mAgentWeb.urlLoader.loadUrl(urlOrKeyWord)
            }
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
        val regExp = "\\b((https?|http?):\\/\\/)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[A-Za-z]{2,6}\\b(\\/[-a-zA-Z0-9@:%_\\+.~#?&//=]*)*(?:\\/|\\b)"
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
        bottomTabsSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomMenuSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomToolsSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        super.onResume()
    }

    override fun onDestroy() {
        mAgentWeb.webLifeCycle.onDestroy()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == ScanActivity.REQUEST_SCAN_CODE) {
            val result = data?.getStringExtra(ScanActivity.KEY_SCAN_RESULT)
            openUrlOrSearch(result)
        }
        if (requestCode == HistoryActivity.REQUEST_HISTORY) {
            val result = data?.getParcelableExtra<History>(HistoryActivity.KEY_RESULT_HISTORY)
            openUrlOrSearch(result?.url)
        }
        if (requestCode == BookmarkActivity.REQUEST_BOOKMARK) {
            val result = data?.getParcelableExtra<BookMark>(BookmarkActivity.KEY_RESULT_BOOKMARK)
            openUrlOrSearch(result?.url)
        }
    }
}
