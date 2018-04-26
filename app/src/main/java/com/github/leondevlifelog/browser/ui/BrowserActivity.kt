package com.github.leondevlifelog.browser.ui

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.support.design.widget.AppBarLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.TextInputEditText
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.PopupMenu
import com.github.leondevlifelog.browser.ObversableTabsInfo
import com.github.leondevlifelog.browser.R
import com.github.leondevlifelog.browser.TabsAdapter
import com.github.leondevlifelog.browser.bean.TabInfo
import com.github.leondevlifelog.browser.behavior.TopSheetBehavior
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
import kotlinx.android.synthetic.main.view_search_web.*
import kotlinx.android.synthetic.main.view_tabs_content.*
import kotlinx.android.synthetic.main.view_tools_content.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import kotlin.concurrent.schedule

@RuntimePermissions
class BrowserActivity : AppCompatActivity() {
    private val TAG: String = "BrowserActivity"
    private var bottomMenuSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var bottomTabsSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var bottomToolsSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var topSearchBehavior: TopSheetBehavior<LinearLayout>? = null
    private lateinit var tabs: ObversableTabsInfo
    private lateinit var mAgentWeb: AgentWeb

    private lateinit var webView: MyWebView

    private lateinit var tabsAdapter: TabsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw()
        }
        setContentView(R.layout.activity_browser)
        initWebView()
        initSwipeRefresh()
        initSearchPanel()
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
        topSearchBehavior = TopSheetBehavior.from(topSearchPanel)
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
        actionToolsUagent.setOnClickListener {
            var popupMenu = PopupMenu(this@BrowserActivity, it)
            popupMenu.inflate(R.menu.menu_tools_uagent)
            popupMenu.setOnMenuItemClickListener { item: MenuItem? ->
                when (item?.itemId) {
                    R.id.menu_pc -> MenuItem.OnMenuItemClickListener {
                        Log.d(TAG, "onCreate: ")
                        return@OnMenuItemClickListener true
                        //TODO 完善选择uagent
                    }

                }
                return@setOnMenuItemClickListener true
            }
            popupMenu.show()
        }
        actionToolsCopyLink.setOnClickListener {
            var systemService = this@BrowserActivity.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as ClipboardManager
            systemService.primaryClip = ClipData.newPlainText(null, tabs.selectedTab?.url)
            _toast("成功复制链接到剪贴板")
        }
        actionToolsExportLauncher.setOnClickListener {
            addShortcut()
        }
        actionToolsSaveWeb.setOnClickListener {
            saveWebToFileWithPermissionCheck()
        }
        actionToolsClipWeb.setOnClickListener {
            var cropWebView = getCropWebView(webView)
            AsyncTask.execute {
                saveBitmapToFileWithPermissionCheck(cropWebView, tabs.selectedTab?.title!!)
            }
            _toast("保存成功")
        }
        actionToolsSearchWeb.setOnClickListener {
            topSearchBehavior?.state = TopSheetBehavior.STATE_EXPANDED
            etSearchInput.requestFocus()
            var inputService = this@BrowserActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputService.showSoftInput(etSearchInput, InputMethodManager.SHOW_IMPLICIT)
            bottomToolsSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    /**
     * 初始化搜索面板
     */
    private fun initSearchPanel() {
        ibSearchClose.setOnClickListener {
            etSearchInput.setText("")
            tvSearchResultCount.text = "0/0"
            topSearchBehavior?.state = TopSheetBehavior.STATE_COLLAPSED
        }
        webView.setFindListener({ activeMatchOrdinal, numberOfMatches, isDoneCounting ->
            if (activeMatchOrdinal != numberOfMatches)
                tvSearchResultCount.text = "${activeMatchOrdinal + 1}/$numberOfMatches"
        })
        etSearchInput.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (etSearchInput.text.length < 0) {
                    _toast("请输入关键词")
                    return@setOnEditorActionListener false
                }
                var inputService = this@BrowserActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputService.hideSoftInputFromWindow(v.windowToken, 0)
                webView.findAllAsync(etSearchInput.text.toString())
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        btSearchNextResult.setOnClickListener {
            webView.findNext(true)

        }
    }


    /**
     * 保存网页到SD卡的Download目录
     */
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun saveWebToFile() {
        mAgentWeb.jsInterfaceHolder.addJavaObject("android", this)
        mAgentWeb.webCreator.webView.loadUrl("javascript:window.android.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');")

    }

    @JavascriptInterface
    fun processHTML(html: String?) {
        if (html == null)
            return
        var downloadFolder = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)
        if (!downloadFolder.exists()) {
            _toast("保存失败")
            return
        }
        var file = File(downloadFolder, "${tabs.selectedTab?.title}.html")
        file.writeText(html, Charsets.UTF_8)
        _toast("保存网页至:${file.absolutePath}")
    }

    /**
     * 添加网址快捷方式到桌面
     */
    private fun addShortcut() {
        var shortcut = Intent("com.android.launcher.action.INSTALL_SHORTCUT")
        var icon = (ContextCompat.getDrawable(this, R.mipmap.ic_launcher) as BitmapDrawable).bitmap
        var uri = Uri.parse(tabs.selectedTab?.url)
        var pendingIntent = Intent(Intent.ACTION_VIEW, uri)
        //桌面快捷方式图标
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon)
        //桌面快捷方式标题
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                tabs.selectedTab?.title)
        //桌面快捷方式动作:点击图标时的动作
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, pendingIntent)
        sendBroadcast(shortcut)
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
     * 收起工具箱面板
     */
    fun hidenToolsPanel() {
        bottomToolsSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    /**
     * 保存bitmap为图片
     */
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun saveBitmapToFile(x: Bitmap, fileName: String) {
        var downloadFolder = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)
        if (!downloadFolder.exists()) {
            _toast("保存失败")
            return
        }
        var file = File(downloadFolder, "$fileName.png")
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(file)
            x.compress(Bitmap.CompressFormat.PNG, 50, out) // bmp is your Bitmap instance
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                if (out != null) {
                    out.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 截长图
     */
    fun getCropWebView(x: WebView): Bitmap {
        // WebView 生成长图，也就是超过一屏的图片，代码中的 longImage 就是最后生成的长图
        x.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        x.layout(0, 0, x.measuredWidth, x.measuredHeight)
        x.isDrawingCacheEnabled = true
        x.buildDrawingCache()
        var longImage = Bitmap.createBitmap(x.measuredWidth,
                x.measuredHeight, Bitmap.Config.ARGB_8888)

        var canvas = Canvas(longImage)  // 画布的宽高和 WebView 的网页保持一致
        var paint = Paint()
        canvas.drawBitmap(longImage, 0f, x.measuredHeight.toFloat(), paint)
        x.draw(canvas)
        return longImage
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
}
