package com.github.leondevlifelog.browser.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.leondevlifelog.browser.R
import kotlinx.android.synthetic.main.toolbar.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "注册"
    }
}
