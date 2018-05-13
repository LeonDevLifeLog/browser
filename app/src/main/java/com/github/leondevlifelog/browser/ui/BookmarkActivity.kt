package com.github.leondevlifelog.browser.ui

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.MenuItem
import com.github.leondevlifelog.browser.R
import com.github.leondevlifelog.browser.adapter.CategorpExpandableGroup
import com.github.leondevlifelog.browser.adapter.MyExpandableRecyclerViewAdapter
import com.github.leondevlifelog.browser.database.AppDatabaseImpl
import kotlinx.android.synthetic.main.activity_bookmark.*
import kotlinx.android.synthetic.main.toolbar.*

class BookmarkActivity : AppCompatActivity() {

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
        rvBookmarkList.addItemDecoration(DividerItemDecoration(this@BookmarkActivity, RecyclerView.VERTICAL))
        rvBookmarkList.adapter = MyExpandableRecyclerViewAdapter(mutableListOf())
        QueryAllBookMarkDate(this, rvBookmarkList).execute()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 异步查询数据库操作
     */
    class QueryAllBookMarkDate(var activity: BookmarkActivity, var recyclerView: RecyclerView) : AsyncTask<Unit, Unit, MutableList<CategorpExpandableGroup>>() {
        override fun doInBackground(vararg params: Unit?): MutableList<CategorpExpandableGroup> {
            //查询数据库 并返回结果
            var allCategory = AppDatabaseImpl.instance.bookMarkDao().findAllCategory()
            var distinct = allCategory.distinct()
            var result: MutableList<CategorpExpandableGroup> = mutableListOf()
            distinct.forEach {
                if (!TextUtils.isEmpty(it))
                    result.add(CategorpExpandableGroup(it, AppDatabaseImpl.instance.bookMarkDao().findByCategory(it)))
            }
            return result
        }

        override fun onPostExecute(result: MutableList<CategorpExpandableGroup>) {
            super.onPostExecute(result)
            //接收到查询结果,通过适配器模式在UI线程更新视图
//            adapterBookmark.onItemClickListener = object : AdapterBookMark.OnItemClickListener {
//                override fun onClick(view: View, position: Int) {
//                    //当点击书签的某条数据时,打开相应网址
//                    var intent = Intent()
//                    intent.putExtra(BookmarkActivity.KEY_RESULT_BOOKMARK, result[position])
//                    activity.setResult(Activity.RESULT_OK, intent)
//                    activity.finish()
//                }
//            }
            var adapterBookmark = MyExpandableRecyclerViewAdapter(result)
            adapterBookmark.eventListener = object : MyExpandableRecyclerViewAdapter.EventListener {
                override fun onChildLongClick(x: Int, y: Int) {
                    adapterBookmark.isCheckedMode = true
                    adapterBookmark.notifyDataSetChanged()
                }

                override fun onChildClick(x: Int, y: Int) {
                }

                override fun onParentLongClick(x: Int) {
                }

                override fun onParentClick(x: Int) {
                }
            }
            recyclerView.adapter = adapterBookmark
            recyclerView.adapter.notifyDataSetChanged()
        }
    }

}
