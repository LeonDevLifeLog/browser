package com.github.leondevlifelog.browser.ui

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.github.leondevlifelog.browser.R
import com.github.leondevlifelog.browser.database.AppDatabaseImpl
import com.github.leondevlifelog.browser.database.entities.User
import com.jyuesong.android.kotlin.extract._long_toast
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.toolbar.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "注册"
        btnRegister.setOnClickListener {
            if (etUserName.length() < 4 || etUserName.length() > 16) {
                tilNickName.error = "用户名长度有误"
                return@setOnClickListener
            }
            if (etPassword.length() < 6 || etPassword.length() > 16) {
                tilPassword.error = "密码长度有误"
                return@setOnClickListener
            }
            if (etSecondPassword.length() < 6 || etSecondPassword.length() > 16) {
                tilSecondPassword.error = "密码长度有误"
                return@setOnClickListener
            }
            if (etPassword.text.toString() != etSecondPassword.text.toString()) {
                tilSecondPassword.error = "两次密码输入不一致"
                return@setOnClickListener
            }
            AsyncTask.execute {
                var user = AppDatabaseImpl.instance.userDao().findUserByUsername(etUserName.text.toString())
                if (user != null) {
                    runOnUiThread {
                        tilNickName.error = "用户已经存在,请更换用户名"
                        _long_toast("用户已经存在,请更换用户名")
                    }
                } else {
                    AppDatabaseImpl.instance.userDao().insert(User(username = etUserName.text.toString(), password = etPassword.text.toString(),
                            nickname = etUserName.text.toString()))
                    runOnUiThread {
                        _long_toast("用户注册成功")
                        this.finish()
                    }
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
