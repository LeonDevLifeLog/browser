package com.github.leondevlifelog.browser.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import com.jyuesong.android.kotlin.extract._dip2px

/**
 * 限制最大高度的RecyclerView
 *
 * @author Leon <leondevlifelog@gmail.com>
 * @version 0.0.1
 * @date 2018-04-10 19:36
 */
class LimitHeightRecyclerView : RecyclerView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        var heightSpec = MeasureSpec.makeMeasureSpec(_dip2px(240), MeasureSpec.AT_MOST)
        super.onMeasure(widthSpec, heightSpec)
    }
}