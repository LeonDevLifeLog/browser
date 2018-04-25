package com.github.leondevlifelog.browser.ui

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView.VERTICAL
import android.view.MenuItem
import android.view.View
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
        const val KEY_RESULT_HISTORY = "KEY_RESULT_HISTORY"
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
            var adapterHistory = AdapterHistory(listHistory)
            adapterHistory.onItemClickListener = object : AdapterHistory.OnItemClickListener {
                override fun onClick(view: View, position: Int) {
                    var intent = Intent()
                    intent.putExtra(KEY_RESULT_HISTORY, listHistory!![position])
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
            rvHistoryList.addItemDecoration(DividerItemDecoration(this@HistoryActivity, VERTICAL))
            rvHistoryList.adapter = adapterHistory
            rvHistoryList.adapter.notifyDataSetChanged()
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
