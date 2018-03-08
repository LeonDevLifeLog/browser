package com.github.leondevlifelog.browser;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild2;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.tencent.smtt.sdk.WebView;

/**
 * TODO:添加功能描述
 *
 * @author liang <TODO:邮箱或手机号>
 * @version TODO:添加版本号
 * @date 2018/3/6 14:16
 * @changelog <修订人> <修改时间(2017-11-02)> (每次修订增加一行,按最新修订时间在上排序)
 */

public class NestedWebView extends WebView implements NestedScrollingChild2 {
    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];
    private int mLastY;
    private int mNestedOffsetY;
    private NestedScrollingChildHelper mChildHelper;

    public NestedWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final MotionEvent event = MotionEvent.obtain(ev);
        final int action = ev.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN) {
            mNestedOffsetY = 0;
        }

        final int eventY = (int) event.getY();
        event.offsetLocation(0, mNestedOffsetY);

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                int deltaY = mLastY - eventY;

                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
                    deltaY -= mScrollConsumed[1];
                    event.offsetLocation(0, -mScrollOffset[1]);
                    mNestedOffsetY += mScrollOffset[1];
                }

                mLastY = eventY - mScrollOffset[1];

                if (dispatchNestedScroll(0, mScrollOffset[1], 0, deltaY, mScrollOffset)) {
                    mLastY -= mScrollOffset[1];
                    event.offsetLocation(0, mScrollOffset[1]);
                    mNestedOffsetY += mScrollOffset[1];
                }
                break;

            case MotionEvent.ACTION_DOWN:
                mLastY = eventY;
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                stopNestedScroll();
                break;

            default:
                // We don't care about other touch events
        }

        // Execute event handler from parent class in all cases
        boolean eventHandled = super.onTouchEvent(event);

        // Recycle previously obtained event
        event.recycle();

        return eventHandled;
    }

    // NestedScrollingChild
    @Override
    public boolean startNestedScroll(int axes, int type) {
        return mChildHelper.startNestedScroll(axes, type);
    }

    @Override
    public void stopNestedScroll(int type) {
        mChildHelper.stopNestedScroll(type);
    }

    @Override
    public boolean hasNestedScrollingParent(int type) {
        return mChildHelper.hasNestedScrollingParent(type);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, int type) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow, int type) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }
}