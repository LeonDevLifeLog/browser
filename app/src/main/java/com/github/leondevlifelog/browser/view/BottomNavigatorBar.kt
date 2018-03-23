package com.github.leondevlifelog.browser.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.github.leondevlifelog.browser.R

/**
 * TODO:添加功能描述
 *
 * @author liang <TODO:邮箱或手机号>
 * @version TODO:添加版本号
 * @date 2018/3/23 17:50
 */
class BottomNavigatorBar : FrameLayout {
    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.bottpm_navigator_bar, this)
    }

}