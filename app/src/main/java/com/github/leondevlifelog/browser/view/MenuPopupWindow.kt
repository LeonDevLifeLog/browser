package com.github.leondevlifelog.browser.view

import android.animation.Animator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
import com.github.leondevlifelog.browser.R
import com.labo.kaji.relativepopupwindow.RelativePopupWindow


/**
 * TODO:添加功能描述
 *
 * @author liang <TODO:邮箱或手机号>
 * @version TODO:添加版本号
 * @date 2018/3/29 17:37
 */
class MenuPopupWindow(context: Context) : RelativePopupWindow(context) {
    var dismessAnim: Animator? = null

    init {
        setBackgroundDrawable(ColorDrawable(Color.WHITE))
        contentView = LayoutInflater.from(context).inflate(R.layout.view_menu_content, null)
        var btnHideMenu = contentView.findViewById<ImageButton>(R.id.btnHideMenu)
        btnHideMenu.setOnClickListener({ v ->
            if (!dismessAnim?.isRunning!!) {
                dismessAnim?.duration = 500
                dismessAnim?.interpolator = DecelerateInterpolator()
                dismessAnim?.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        this@MenuPopupWindow.dismiss()
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }
                })
                dismessAnim?.start()
            }
        })
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isFocusable = false
        isOutsideTouchable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Disable default animation for circular reveal
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animationStyle = 0
        }
    }

    override fun showOnAnchor(anchor: View, vertPos: Int, horizPos: Int, x: Int, y: Int, fitInScreen: Boolean) {
        super.showOnAnchor(anchor, vertPos, horizPos, x, y, fitInScreen)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            circularReveal(anchor)
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun circularReveal(anchor: View) {
        contentView.apply {
            post {
                val myLocation = IntArray(2).apply { getLocationOnScreen(this) }
                val anchorLocation = IntArray(2).apply { anchor.getLocationOnScreen(this) }
                val cx = anchorLocation[0] - myLocation[0] + anchor.width!! / 2
                val cy = anchorLocation[1] - myLocation[1] + anchor.height!! / 2

                measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                val dx = Math.max(cx, measuredWidth - cx)
                val dy = Math.max(cy, measuredHeight - cy)
                val finalRadius = Math.hypot(dx.toDouble(), dy.toDouble()).toFloat()
                dismessAnim = ViewAnimationUtils.createCircularReveal(this, cx, cy, finalRadius, 0f)
                ViewAnimationUtils.createCircularReveal(this, cx, cy, 0f, finalRadius).apply {
                    duration = 500
                    interpolator = AccelerateDecelerateInterpolator()
                    start()
                }
            }
        }
    }

}