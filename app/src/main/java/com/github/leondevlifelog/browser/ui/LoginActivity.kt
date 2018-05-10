package com.github.leondevlifelog.browser.ui

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.github.leondevlifelog.browser.R
import com.github.leondevlifelog.browser.database.AppDatabaseImpl
import com.github.leondevlifelog.browser.database.entities.User
import com.jyuesong.android.kotlin.extract._toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.toolbar.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "登录"
        btnRegister.setOnClickListener {
            //跳转至注册界面
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
        btnLogin.setOnClickListener {
            var username = etUserName.text.toString()
            var passwprd = etPassword.text.toString()
            //输入内容校验
            if (etPassword.text.length < 6) {
                tilPassword.error = "密码长度有误"
            } else {
                //处理登陆逻辑
                doLoginAction(username, passwprd)
            }
        }
    }

    private fun doLoginAction(username: String, passwprd: String) {
        AsyncTask.execute {
            AppDatabaseImpl.instance.UserDao().insert(User(username = username, password = passwprd, nickname = username))
            runOnUiThread { _toast("注册成功") }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
