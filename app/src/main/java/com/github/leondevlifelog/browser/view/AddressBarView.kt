package com.github.leondevlifelog.browser.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import com.github.leondevlifelog.browser.R
import com.github.leondevlifelog.browser.adapter.AutoCompleteAdapter
import kotlinx.android.synthetic.main.view_address_bar.view.*


/**
 *
 */
class AddressBarView : FrameLayout {
    val TAG = "AddressBarView"
    private val COUNTRIES = arrayOf("abcdAfghanistan", "asdasdf", "asdasdf", "asdasdf", "asdasdf", "asdasdf", "asdasdf", "asdasdf", "asdasdf", "asdasdf", "asdasdf", "Albania", "Algeria", "American Samoa", "Andorra", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Cote d'Ivoire", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Democratic Republic of the Congo", "Denmark", "Djibouti", "Dominica", "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea")

    interface OnActionButtonClickListener {
        fun onScanBtnClick(v: View): Unit
        fun onSecurityBtnClick(v: View): Unit
        fun onNoneSecurityBtnClick(v: View): Unit
        fun onSearchBtnClick(v: View): Unit
        fun onInputDone(urlOrKeyWord: String): Unit
    }

    private var drawableClear: Drawable? = null
    private var drawableSearch: Drawable? = null
    private var drawableSecurity: Drawable? = null
    private var drawableNoneSecurity: Drawable? = null
    private var drawableScan: Drawable? = null
    var onActionButtonClickListener: OnActionButtonClickListener? = null

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        LayoutInflater.from(context).inflate(R.layout.view_address_bar, this)
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
        initDrawables(attrs, defStyle)
        initView()
    }

    /**
     * 初始化视图相关
     */
    private fun initView() {
        webStatusIcon.setImageResource(R.drawable.ic_search)
        webStatusIcon.setOnClickListener { v ->
            when (v.tag) {
                R.drawable.ic_search -> onActionButtonClickListener?.onSearchBtnClick(v)
                R.drawable.ic_security -> onActionButtonClickListener?.onSecurityBtnClick(v)
                R.drawable.ic_warning -> onActionButtonClickListener?.onNoneSecurityBtnClick(v)
            }
        }
        actionScan.setImageDrawable(drawableScan)
        actionScan.setOnClickListener { v -> onActionButtonClickListener?.onScanBtnClick(v) }
        //清空输入框按钮事件
        actionClearInput.setOnClickListener { urlInputBox.setText("") }
        urlInputBox.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus && urlInputBox.text.isNotEmpty()) {
                actionClearInput.visibility = View.VISIBLE
            } else {
                actionClearInput.visibility = View.GONE
                urlInputBox.setSelection(0)
            }
        }
        urlInputBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //清空按钮逻辑
                if (s.isNullOrEmpty()) {
                    actionClearInput.visibility = View.GONE
                } else {
                    actionClearInput.visibility = View.VISIBLE
                }
                //https处理逻辑
                if (s!!.length >= 8 && s.substring(0, 8).toLowerCase().equals("https://")) {
                    Log.d(TAG, "https处理逻辑: ${s.substring(0, 8)}")
                    s.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorSecurityScheme)),
                            0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    webStatusIcon.setImageResource(R.drawable.ic_security)
                    return
                } else if (s.length >= 7 && s.substring(0, 7).toLowerCase().equals("http://")) {
                    //http处理逻辑
                    Log.d(TAG, "http处理逻辑: ${s.substring(0, 7)}")
                    var removeTextSpan = s.getSpans(0, s.length, ForegroundColorSpan::class.java)
                    for (i in 0 until removeTextSpan.size)
                        s.removeSpan(removeTextSpan[i])
                    webStatusIcon.setImageResource(R.drawable.ic_warning)
                    return
                } else {
                    webStatusIcon.setImageResource(R.drawable.ic_search)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        urlInputBox.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                var inputService = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (v.hasFocus()) {
                    inputService.hideSoftInputFromWindow(v.windowToken, 0)
                    v.isFocusableInTouchMode = false
                    v.isFocusable = false
                    v.isFocusableInTouchMode = true
                    v.isFocusable = true
                }
                onActionButtonClickListener?.onInputDone(urlInputBox.text.toString())
            }
            false
        }
        urlInputBox.setAdapter(AutoCompleteAdapter())
    }
    /**
     * 初始化drawable
     * @param attrs
     * @property defStyle
     */
    private fun initDrawables(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.AddressBarView, defStyle, 0)
        if (a.hasValue(R.styleable.AddressBarView_drawableClear)) {
            drawableClear = a.getDrawable(
                    R.styleable.AddressBarView_drawableClear)
            drawableClear!!.callback = this
        }
        if (a.hasValue(R.styleable.AddressBarView_drawableSearch)) {
            drawableSearch = a.getDrawable(
                    R.styleable.AddressBarView_drawableSearch)
            drawableSearch!!.callback = this
        }
        if (a.hasValue(R.styleable.AddressBarView_drawableScan)) {
            drawableScan = a.getDrawable(
                    R.styleable.AddressBarView_drawableScan)
            drawableScan!!.callback = this
        }
        if (a.hasValue(R.styleable.AddressBarView_drawableSecurity)) {
            drawableSecurity = a.getDrawable(
                    R.styleable.AddressBarView_drawableSecurity)
            drawableSecurity!!.callback = this
        }
        if (a.hasValue(R.styleable.AddressBarView_drawableNoneSecurity)) {
            drawableNoneSecurity = a.getDrawable(
                    R.styleable.AddressBarView_drawableNoneSecurity)
            drawableNoneSecurity!!.callback = this
        }
        a.recycle()
    }

    fun setUrl(url: String?): Unit {
        urlInputBox.setText(url)
    }
}
