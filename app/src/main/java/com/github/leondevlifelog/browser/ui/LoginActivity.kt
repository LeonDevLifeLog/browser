package com.github.leondevlifelog.browser.ui

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import com.github.leondevlifelog.browser.R
import com.github.leondevlifelog.browser.SP_KEY_ISLOGING
import com.github.leondevlifelog.browser.SP_KEY_USERNAME_LOGINED
import com.github.leondevlifelog.browser.database.AppDatabaseImpl
import com.jyuesong.android.kotlin.extract._toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.toolbar.*

class LoginActivity : AppCompatActivity() {
    val TAG: String = "LoginActivity"
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
            var user = AppDatabaseImpl.instance.userDao().findUserByUsername(username)
            Log.d(TAG, "$user")
            if (user?.password == passwprd) {
                runOnUiThread {
                    _toast("登录成功")
                    PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(SP_KEY_ISLOGING, true).apply()
                    PreferenceManager.getDefaultSharedPreferences(this).edit().putString(SP_KEY_USERNAME_LOGINED, username).apply()
                    this.finish()
                }
            } else {
                runOnUiThread {
                    _toast("登录失败")
                }
            }
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
