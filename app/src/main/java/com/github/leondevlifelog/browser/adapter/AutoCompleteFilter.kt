package com.github.leondevlifelog.browser.adapter

import android.widget.Filter
import com.github.leondevlifelog.browser.database.AppDatabaseImpl
import com.github.leondevlifelog.browser.database.WebData

class AutoCompleteFilter(var adapter: AutoCompleteAdapter, var datas: MutableList<WebData>?) : Filter() {
    override fun performFiltering(constraint: CharSequence?): FilterResults {
        //根据输入的内容进行书签表和历史记录表数据的检索
        var filterResults = FilterResults()
        var result = AppDatabaseImpl.instance.bookMarkDao().findByUrl(constraint.toString()) as MutableList<WebData>
        result.addAll(AppDatabaseImpl.instance.historyDao().findByUrl(constraint.toString()))
        filterResults.values = result
        filterResults.count = result.size
        //检索完成并返回结果
        return filterResults
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        //在UI线程更新结果到适配器中,并通知界面重新绘制
        var resultList = results?.values as MutableList<WebData>
        adapter.constraint = constraint
        datas?.clear()
        datas?.addAll(resultList)
        adapter.notifyDataSetChanged()
    }
}