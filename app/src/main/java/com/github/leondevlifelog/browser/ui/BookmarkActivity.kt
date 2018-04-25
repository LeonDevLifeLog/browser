package com.github.leondevlifelog.browser.ui

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import com.github.leondevlifelog.browser.R
import com.github.leondevlifelog.browser.adapter.AdapterBookMark
import com.github.leondevlifelog.browser.database.AppDatabaseImpl
import com.github.leondevlifelog.browser.database.entities.BookMark
import kotlinx.android.synthetic.main.activity_bookmark.*
import kotlinx.android.synthetic.main.tolbar.*

class BookmarkActivity : AppCompatActivity() {

    private var listBookmark: List<BookMark>? = null

    companion object {
        /**
         * 书签请求码
         */
        const val REQUEST_BOOKMARK = 13
        const val KEY_RESULT_BOOKMARK = "KEY_RESULT_BOOKMARK"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "书签"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        AsyncTask.execute {
            listBookmark = AppDatabaseImpl.instance.bookMarkDao().listAll()
            var adapterBookmark = AdapterBookMark(listBookmark)
            adapterBookmark.onItemClickListener = object : AdapterBookMark.OnItemClickListener {
                override fun onClick(view: View, position: Int) {
                    var intent = Intent()
                    intent.putExtra(BookmarkActivity.KEY_RESULT_BOOKMARK, listBookmark!![position])
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
            rvBookmarkList.addItemDecoration(DividerItemDecoration(this@BookmarkActivity, RecyclerView.VERTICAL))
            rvBookmarkList.adapter = adapterBookmark
            rvBookmarkList.adapter.notifyDataSetChanged()
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
