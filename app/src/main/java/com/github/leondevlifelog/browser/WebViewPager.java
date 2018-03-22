package com.github.leondevlifelog.browser;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * TODO:添加功能描述
 *
 * @author liang <TODO:邮箱或手机号>
 * @version TODO:添加版本号
 * @date 2018/3/8 15:27
 * @changelog <修订人> <修改时间(2017-11-02)> (每次修订增加一行,按最新修订时间在上排序)
 */

public class WebViewPager extends ViewPager {
    private Boolean isScrollEnable = true;

    public WebViewPager(Context context) {
        super(context);
    }

    public WebViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return isScrollEnable || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isScrollEnable || super.onInterceptTouchEvent(ev);
    }

    public Boolean getScrollEnable() {
        return isScrollEnable;
    }

    public void setScrollEnable(Boolean scrollEnable) {
        isScrollEnable = scrollEnable;
    }
}
