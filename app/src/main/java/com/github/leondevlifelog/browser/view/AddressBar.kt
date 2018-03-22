package com.github.leondevlifelog.browser.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View.OnFocusChangeListener
import android.widget.AutoCompleteTextView
import com.github.leondevlifelog.browser.R
import com.jyuesong.android.kotlin.extract._dip2px

/**
 * 地址栏
 *
 * @author Leon <leondevlifelog@gmail.com>
 * @version 0.0.1
 * @date 2018-03-22 13:58
 */
class AddressBar : AutoCompleteTextView, TextWatcher {
    val TAG: String = "AddressBar"

    interface OnDrawableClickListener {
        /**
         * 扫描二维码图片点击事件
         */
        fun onScanDrawableClick()

        /**
         * https通讯图片点击事件
         */
        fun onSecurityDrawableClick()

        /**
         * http通讯图片点击事件
         */
        fun onNoneSecurityDrawableClick()
    }

    var onScanBtnClickListener: OnDrawableClickListener? = null
    private var drawableScan: Drawable? = null
    private var drawableClear: Drawable? = null
    private var drawableSecurity: Drawable? = null
    private var drawableNoneSecurity: Drawable? = null
    private var drawableSearch: Drawable? = null
    val drawableWidth = _dip2px(24)
    val drawablePadding = _dip2px(10)
    var simpleOnGestureListener: GestureDetector.SimpleOnGestureListener? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }


    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context?, attrs: AttributeSet?) {
        val a = context?.obtainStyledAttributes(attrs, R.styleable.AddressBar)
        this.drawableClear = a!!.getDrawable(R.styleable.AddressBar_drawableClear)
        this.drawableScan = a.getDrawable(R.styleable.AddressBar_drawableScan)
        this.drawableSecurity = a.getDrawable(R.styleable.AddressBar_drawableSecurity)
        this.drawableNoneSecurity = a.getDrawable(R.styleable.AddressBar_drawableNoneSecurity)
        this.drawableSearch = a.getDrawable(R.styleable.AddressBar_drawableSearch)
        a.recycle()
        simpleOnGestureListener = object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                if (e!!.action != MotionEvent.ACTION_UP)
                    return false
                if ((e.x > width - paddingRight - drawableWidth - drawablePadding)
                        && compoundDrawables[2] == drawableClear) {
                    Log.d(TAG, "simpleOnGestureListener ondrawableClear")
                    setText("")
                    setLeftDrawable(drawableSearch)
                    setRightDrawable(drawableScan)
                    return true
                } else if ((e.x > width - paddingRight - drawableWidth - drawablePadding)
                        && compoundDrawables[2] == drawableScan) {
                    Log.d(TAG, "simpleOnGestureListener onScanDrawableClick")
                    onScanBtnClickListener?.onScanDrawableClick()
                    return true
                } else if (e.x < paddingLeft + drawableWidth + drawablePadding
                        && compoundDrawables[0] == drawableSecurity) {
                    Log.d(TAG, "simpleOnGestureListener onSecurityDrawableClick: ")
                    onScanBtnClickListener?.onSecurityDrawableClick()
                    return true
                } else if (e.x < paddingLeft + drawableWidth + drawablePadding
                        && compoundDrawables[0] == drawableNoneSecurity) {
                    Log.d(TAG, "simpleOnGestureListener onNoneSecurityDrawableClick: ")
                    onScanBtnClickListener?.onNoneSecurityDrawableClick()
                    return true
                }
                return super.onSingleTapConfirmed(e)
            }
        }
        setupDrawableBound(drawableScan)
        setupDrawableBound(drawableClear)
        setupDrawableBound(drawableSecurity)
        setupDrawableBound(drawableNoneSecurity)
        setupDrawableBound(drawableSearch)
        setCompoundDrawables(drawableSearch, null, drawableScan, null)
        addTextChangedListener(this)
        onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus && v is AddressBar) {
                if (v.text.length >= 8 && v.text.substring(0, 8).toLowerCase() == "https://") {
                    setLeftDrawable(drawableSecurity)
                } else if (v.text.length >= 7 && v.text.substring(0, 7).toLowerCase() == "http://") {
                    setLeftDrawable(drawableNoneSecurity)
                }
                setRightDrawable(if (v.text.isNotEmpty()) drawableClear else drawableScan)
            } else if (!hasFocus && v is AddressBar) {
                if (v.text.length >= 8 && v.text.substring(0, 8).toLowerCase() == "https://") {
                    setLeftDrawable(drawableSecurity)
                } else if (v.text.length >= 7 && v.text.substring(0, 7).toLowerCase() == "http://") {
                    setLeftDrawable(drawableNoneSecurity)
                }
                setRightDrawable(drawableScan)
                setSelection(0)
            }
        }
        setOnTouchListener { _, event ->
            var singleTapConfirmed = simpleOnGestureListener?.onSingleTapConfirmed(event)!!
            return@setOnTouchListener if (singleTapConfirmed) !singleTapConfirmed else super.onTouchEvent(event)
        }
    }

    private fun setRightDrawable(drawable: Drawable?) {
        setCompoundDrawablesRelativeWithIntrinsicBounds(compoundDrawables[0], null, drawable, null)
    }

    private fun setupDrawableBound(drawable: Drawable?) {
        drawable?.setBounds(0, 0, drawableWidth, drawableWidth)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        if (s?.length!! >= 7 && (s.startsWith("http://") || s.startsWith("http://".toUpperCase()))) {
            setLeftDrawable(drawableNoneSecurity)
        }
        if (s.length >= 8 && (s.startsWith("https://") || s.startsWith("https://".toUpperCase()))) {
            s.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorSecurityScheme)),
                    0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setLeftDrawable(drawableSecurity)
        } else {
            var toRemoveSpans = s.getSpans(0, s.length, ForegroundColorSpan::class.java)
            for (i in 0 until toRemoveSpans.size)
                s.removeSpan(toRemoveSpans[i])
        }
    }

    private fun setLeftDrawable(drawable: Drawable?) {
        setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, compoundDrawables[2], null)
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        if (text!!.isNotEmpty()) {
            setCompoundDrawablesRelativeWithIntrinsicBounds(drawableSearch, null, drawableClear, null)
        } else {
            setCompoundDrawablesRelativeWithIntrinsicBounds(drawableSearch, null, drawableScan, null)
        }
    }

}