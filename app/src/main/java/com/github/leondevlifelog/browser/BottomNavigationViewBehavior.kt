package com.github.leondevlifelog.browser

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View

/**
 * 与toolbar联动隐藏底部菜单
 *
 * @author LeonDevLifeLog <leondevlifelog@gmail.com>
 * @date 2018-02-24 08:59
 * @since V4.0.0
 */
class BottomNavigationViewBehavior : CoordinatorLayout.Behavior<View> {
    constructor() {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onLayoutChild(parent: CoordinatorLayout?, child: View?, layoutDirection: Int): Boolean {
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun layoutDependsOn(parent: CoordinatorLayout?, child: View?, dependency: View?): Boolean {
        return dependency is AppBarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout?, child: View?, dependency: View?): Boolean {
        //得到依赖View的滑动距离
        val top = ((dependency!!
                .layoutParams as CoordinatorLayout.LayoutParams).behavior as AppBarLayout.Behavior).topAndBottomOffset
        //因为BottomNavigation的滑动与ToolBar是反向的，所以取负值
        ViewCompat.setTranslationY(child!!, (-(top * child.measuredHeight / dependency
                .measuredHeight)).toFloat())
        return false
    }
}