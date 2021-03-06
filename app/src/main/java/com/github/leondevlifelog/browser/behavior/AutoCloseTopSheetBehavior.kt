package com.github.leondevlifelog.browser.behavior

import android.content.Context
import android.graphics.Rect
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.github.leondevlifelog.browser.R


class AutoCloseTopSheetBehavior<V : View>(context: Context, attrs: AttributeSet) : TopSheetBehavior<V>(context, attrs) {

    override fun onInterceptTouchEvent(parent: CoordinatorLayout?, child: V, event: MotionEvent?): Boolean {
        if (event!!.action == MotionEvent.ACTION_DOWN && state == BottomSheetBehavior.STATE_EXPANDED) {
            val outRect = Rect()
            child.getGlobalVisibleRect(outRect)
            outRect.bottom = outRect.bottom - parent?.resources!!.getDimensionPixelSize(R.dimen.bottom_navigator_height)
            if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                state = TopSheetBehavior.STATE_COLLAPSED
            }
        }
        return super.onInterceptTouchEvent(parent, child, event)
    }
}