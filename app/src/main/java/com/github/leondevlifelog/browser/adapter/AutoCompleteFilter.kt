package com.github.leondevlifelog.browser.adapter

import android.widget.Filter
import com.github.leondevlifelog.browser.database.AppDatabaseImpl
import com.github.leondevlifelog.browser.database.WebData

class AutoCompleteFilter(var adapter: AutoCompleteAdapter, var datas: MutableList<WebData>?) : Filter() {
    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var filterResults = FilterResults()
        var result = AppDatabaseImpl.instance.bookMarkDao().findByUrl(constraint.toString()) as MutableList<WebData>
        result.addAll(AppDatabaseImpl.instance.historyDao().findByUrl(constraint.toString()))
        filterResults.values = result
        filterResults.count = result.size
        return filterResults
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        var resultList = results?.values as MutableList<WebData>
        adapter.constraint = constraint
        datas?.clear()
        datas?.addAll(resultList)
        adapter.notifyDataSetChanged()
    }
}