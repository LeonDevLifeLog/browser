package com.github.leondevlifelog.browser.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.github.leondevlifelog.browser.R

/**
 * 搜索栏
 *
 * @author Leon <leondevlifelog@gmail.com>
 * @version 0.0.1
 * @date 2018-03-22 10:04
 */
class HeaderView : LinearLayout {
    constructor(context: Context) : super(context) {
        init()
    }


    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    /**
     * 初始化
     */
    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.view_header, this)
    }
}
