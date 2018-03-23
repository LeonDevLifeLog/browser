package com.github.leondevlifelog.browser.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton

/**
 * 自动设置tag的imageButton
 *
 * @author Leon <leondevlifelog@gmail.com>
 * @version 0.0.1
 * @date 2018-03-23 17:43
 */
class WebStatusImageButton : ImageButton {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        tag = resId
    }
}