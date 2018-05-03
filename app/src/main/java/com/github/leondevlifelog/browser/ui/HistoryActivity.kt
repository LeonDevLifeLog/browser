package com.github.leondevlifelog.browser.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.VERTICAL
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import com.github.leondevlifelog.browser.R
import com.github.leondevlifelog.browser.adapter.AdapterHistory
import com.github.leondevlifelog.browser.database.AppDatabaseImpl
import com.github.leondevlifelog.browser.database.entities.BookMark
import com.github.leondevlifelog.browser.database.entities.History
import com.goyourfly.multiple.adapter.MultipleSelect
import com.goyourfly.multiple.adapter.StateChangeListener
import com.goyourfly.multiple.adapter.menu.CustomMenuBar
import com.goyourfly.multiple.adapter.menu.MenuController
import com.goyourfly.multiple.adapter.viewholder.view.RadioBtnFactory
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*

class HistoryActivity : AppCompatActivity() {
    companion object {
        /**
         * 历史数据 请求码
         */
        const val REQUEST_HISTORY = 12
        const val KEY_RESULT_HISTORY = "KEY_RESULT_HISTORY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "历史记录"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        rvHistoryList.addItemDecoration(DividerItemDecoration(this@HistoryActivity, VERTICAL))
        QueryAllHistoryDate(this, rvHistoryList).execute()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 异步查询数据库历史记录操作
     */
    class QueryAllHistoryDate(var activity: HistoryActivity, var recyclerView: RecyclerView) : AsyncTask<Unit, Unit, MutableList<History>>() {
        var willBeRemove: MutableList<History> = mutableListOf()
        override fun doInBackground(vararg params: Unit?): MutableList<History> {
            //异步查询历史数据并返回结果
            return AppDatabaseImpl.instance.historyDao().listHistory()
        }

        override fun onPostExecute(result: MutableList<History>) {
            //在UI线程更新视图
            super.onPostExecute(result)
            var adapterHistory = AdapterHistory(result)
            adapterHistory.onItemClickListener = object : AdapterHistory.OnItemClickListener {
                override fun onClick(view: View, position: Int) {
                    var intent = Intent()
                    intent.putExtra(KEY_RESULT_HISTORY, result[position])
                    activity.setResult(Activity.RESULT_OK, intent)
                    activity.finish()
                }
            }
            recyclerView.adapter = MultipleSelect.with(activity)
                    .adapter(adapterHistory)
                    .decorateFactory(RadioBtnFactory(Gravity.RIGHT))
                    .linkList(result)
                    .stateChangeListener(object : StateChangeListener {
                        override fun onCancel() {
                        }

                        override fun onDelete(array: ArrayList<Int>) {
                            //选择完成后,点击转存为书签按钮事件
                            AsyncTask.execute {
                                willBeRemove.forEach {
                                    //从数据库中删除浏览历史数据
                                    AppDatabaseImpl.instance.historyDao().delete(it)
                                }
                            }
                        }

                        override fun onDone(array: ArrayList<Int>) {
                            //选择完成后,点击转存为书签按钮事件
                            AsyncTask.execute {
                                willBeRemove.forEach {
                                    //存储历史记录到书签数据库
                                    AppDatabaseImpl.instance.bookMarkDao().insert(BookMark(title = it.title,
                                            url = it.url, time = Date()))
                                }
                            }
                        }

                        override fun onSelect(position: Int, selectNum: Int) {
                            willBeRemove.add(result[position])
                        }

                        override fun onSelectMode() {
                        }

                        override fun onUnSelect(position: Int, selectNum: Int) {
                            if (willBeRemove.contains(result[position])) {
                                willBeRemove.remove(result[position])
                            }
                        }
                    })
                    .customMenu(object : CustomMenuBar(activity, R.menu.menu_history_activity,
                            ContextCompat.getColor(activity, R.color.white), Gravity.TOP) {
                        override fun getContentView(): View {
                            var menubar = Toolbar(activity)
                            menubar.setBackgroundColor(Color.parseColor("#00000000"))
                            menubar.setTitleTextColor(Color.BLACK)
                            menubar.inflateMenu(menuId)
                            menubar.navigationIcon = ContextCompat.getDrawable(activity, R.drawable.ic_arrow_back_aplah)
                            menubar.setNavigationOnClickListener {
                                dismiss()
                                controler?.cancel()
                            }
                            menubar.setOnMenuItemClickListener {
                                onMenuItemClick(it, controler!!)
                                true
                            }
                            return menubar
                        }

                        override fun onMenuItemClick(menuItem: MenuItem, controller: MenuController) {
                            when (menuItem.itemId) {
                                R.id.action_delete_history -> controller.delete()
                                R.id.action_save_as_bookmark -> controller.done()
                                R.id.action_select_all -> controller.selectAll()
                            }
                        }
                    })
                    .build()
            recyclerView.adapter.notifyDataSetChanged()
        }
    }
}
