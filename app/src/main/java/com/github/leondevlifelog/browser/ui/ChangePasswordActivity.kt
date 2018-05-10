package com.github.leondevlifelog.browser.ui

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.github.leondevlifelog.browser.R
import com.github.leondevlifelog.browser.database.AppDatabaseImpl
import com.jyuesong.android.kotlin.extract._toast
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.toolbar.*

class ChangePasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "修改密码"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        btnChangePasswordSubmit.setOnClickListener {
            if (tieOriginPassword.text.length < 6) {
                tilOrginPassword.error = "原始密码输入有误"
                return@setOnClickListener
            }
            if (tieFirstPassword.text.length < 6) {
                tilOrginPassword.error = "密码输入长度有误"
                return@setOnClickListener
            }
            if (tieSecondPassword.text.length < 6) {
                tilOrginPassword.error = "密码输入长度有误"
                return@setOnClickListener
            }
            if (tieFirstPassword.text != tieSecondPassword.text) {
                tilFirstPassword.error = "两次新密码输入不一致"
                return@setOnClickListener
            }
            btnChangePasswordSubmit.isEnabled = false
            AsyncTask.execute {
                var user = AppDatabaseImpl.instance.UserDao().findUserByUsername("name")
                if (user?.password == tieFirstPassword.text.toString()) {
                    user.password = tieFirstPassword.text.toString()
                    AppDatabaseImpl.instance.UserDao().update(user)
                    runOnUiThread { btnChangePasswordSubmit.isEnabled = true }
                } else {
                    runOnUiThread {
                        btnChangePasswordSubmit.isEnabled = true
                        _toast("密码修改失败")
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
