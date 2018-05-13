package com.github.leondevlifelog.browser.ui

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import com.github.leondevlifelog.browser.R
import com.github.leondevlifelog.browser.SP_KEY_ISLOGING
import com.github.leondevlifelog.browser.SP_KEY_USERNAME_LOGINED
import com.github.leondevlifelog.browser.database.AppDatabaseImpl
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.toolbar.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        var preferences = PreferenceManager.getDefaultSharedPreferences(this)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "个人中心"
        ivEditProfile.setOnClickListener {
            startActivity(Intent(this, ProfileEditActivity::class.java))
        }
        ivChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }
        var isLogin = preferences.getBoolean(SP_KEY_ISLOGING, false)
        if (!isLogin) {
            startActivity(Intent(this, LoginActivity::class.java))
            return
        }
        initUi()
    }

    override fun onResume() {
        super.onResume()
        initUi()
    }

    private fun initUi() {
        AsyncTask.execute {
            var usernameLogined = AppDatabaseImpl.instance.userDao().findUserByUsername(PreferenceManager.getDefaultSharedPreferences(this).getString(SP_KEY_USERNAME_LOGINED, ""))
            runOnUiThread {
                if (!TextUtils.isEmpty(usernameLogined.toString())) {
                    tvUserName.text = usernameLogined?.username
                } else {
                    tvUserName.text = "未登录"
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initUi()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_profile, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.action_logout -> {
                AlertDialog.Builder(this)
                        .setTitle("退出登录确认")
                        .setMessage("确定要退出登录吗?")
                        .setPositiveButton("确定", { dialog, which ->
                            PreferenceManager.getDefaultSharedPreferences(this).edit().putString(SP_KEY_USERNAME_LOGINED, "").apply()
                            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(SP_KEY_ISLOGING, false).apply()
                            this.finish()
                        })
                        .setNegativeButton("取消", null)
                        .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
