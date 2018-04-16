package com.github.leondevlifelog.browser.view

import android.content.Context
import android.util.AttributeSet
import com.just.agentweb.NestedScrollAgentWebView

class MyWebView : NestedScrollAgentWebView {
    interface OnScrollChangeListener {
        fun onPageEnd(l: Int, t: Int, oldl: Int, oldt: Int)
        fun onPageTop(l: Int, t: Int, oldl: Int, oldt: Int)
        fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int)

    }

    var changeListener: OnScrollChangeListener? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        val webContent = contentHeight * scale// webview的高度
        val webNow = (height + scrollY).toFloat()// 当前webview的高度
        if (Math.abs(webContent - webNow) < 1) {
            // 已经处于底端
            // Log.i("TAG1", "已经处于底端");
            changeListener?.onPageEnd(l, t, oldl, oldt)
        } else if (scrollY == 0) {
            // Log.i("TAG1", "已经处于顶端");
            changeListener?.onPageTop(l, t, oldl, oldt)
        } else {
            changeListener?.onScrollChanged(l, t, oldl, oldt)
        }
    }
}