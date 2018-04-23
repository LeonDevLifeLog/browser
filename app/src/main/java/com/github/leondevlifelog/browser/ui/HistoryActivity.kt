package com.github.leondevlifelog.browser.ui

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.github.leondevlifelog.browser.R
import com.github.leondevlifelog.browser.adapter.AdapterHistory
import com.github.leondevlifelog.browser.database.AppDatabaseImpl
import com.github.leondevlifelog.browser.database.entities.History
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.tolbar.*

class HistoryActivity : AppCompatActivity() {
    companion object {
        /**
         * 历史数据 请求码
         */
        const val REQUEST_HISTORY = 12
    }

    private var listHistory: List<History>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "历史记录"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        AsyncTask.execute {
            listHistory = AppDatabaseImpl.instance.historyDao().listHistory()
            rvHistoryList.adapter.notifyDataSetChanged()
        }
        rvHistoryList.adapter = AdapterHistory(listHistory)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
