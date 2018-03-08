package com.github.leondevlifelog.browser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * TODO:添加功能描述
 *
 * @author liang <TODO:邮箱或手机号>
 * @version TODO:添加版本号
 * @date 2018/3/8 14:58
 * @changelog <修订人> <修改时间(2017-11-02)> (每次修订增加一行,按最新修订时间在上排序)
 */

public class parent extends FrameLayout implements NestedScrollingParent,
        NestedScrollingChild {
    public parent(@NonNull Context context) {
        super(context);
    }

    public parent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public parent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public parent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

}
