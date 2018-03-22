package com.github.leondevlifelog.browser.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.leondevlifelog.browser.R

class TestActivity : AppCompatActivity() {
    var isSecurity: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
//        var drawable = ContextCompat.getDrawable(this, R.drawable.ic_warning)
//        drawable.setBounds(0, 0, _dip2px(24), _dip2px(24))
//        var dbSec = ContextCompat.getDrawable(this, R.drawable.ic_security)
//        dbSec.setBounds(0, 0, _dip2px(24), _dip2px(24))
//        var dbEnd = ContextCompat.getDrawable(this, R.drawable.ic_scanning)
//        dbEnd.setBounds(0, 0, _dip2px(24), _dip2px(24))
//        var dbEndClear = ContextCompat.getDrawable(this, R.drawable.ic_clear)
//        dbEndClear.setBounds(0, 0, _dip2px(24), _dip2px(24))
//        btn.setOnClickListener({
//            autoCompleteTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(if (isSecurity) drawable else dbSec, null,
//                    if (isSecurity) dbEnd else dbEndClear, null)
//            isSecurity = !isSecurity
//        })
    }
}
