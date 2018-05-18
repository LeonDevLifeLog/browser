package com.github.leondevlifelog.browser.ui

import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.github.leondevlifelog.browser.R
import com.github.leondevlifelog.browser.database.AppDatabaseImpl
import com.github.leondevlifelog.browser.ktx.SP_KEY_USERNAME_LOGINED
import kotlinx.android.synthetic.main.activity_profile_edit.*
import kotlinx.android.synthetic.main.toolbar.*

class ProfileEditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "编辑信息"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        AsyncTask.execute {
            var username = PreferenceManager.getDefaultSharedPreferences(this).getString(SP_KEY_USERNAME_LOGINED, "")
            var user = AppDatabaseImpl.instance.userDao().findUserByUsername(username)
            if (user != null) {
                runOnUiThread {
                    etUsername.setText(user.username)
                    etNickName.setText(user.nickname)
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
