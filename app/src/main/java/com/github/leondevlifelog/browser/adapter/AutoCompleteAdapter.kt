package com.github.leondevlifelog.browser.adapter

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.github.leondevlifelog.browser.R
import com.github.leondevlifelog.browser.database.WebData
import com.github.leondevlifelog.browser.database.WebType

class AutoCompleteAdapter : BaseAdapter(), Filterable {
    var datas: MutableList<WebData>? = mutableListOf()

    var constraint: CharSequence? = ""

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var viewHolder: ViewHolder
        var view: View
        if (null == convertView) {
            view = LayoutInflater.from(parent?.context).inflate(R.layout.item_auto_complete_view, parent, false)
            viewHolder = ViewHolder(view, this)
            view?.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.bindData(datas!![position])
        return view
    }

    override fun getItem(position: Int): Any {
        return datas?.get(position)!!
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        if (datas == null) {
            return 0
        }
        return datas?.size!!
    }

    override fun getFilter(): Filter {
        return AutoCompleteFilter(this, datas)
    }

    class ViewHolder {

        var ivHistory: ImageView
        var ivBookMark: ImageView
        var tvTitle: TextView
        var tvUrl: TextView
        var adapter: AutoCompleteAdapter

        constructor(view: View, a: AutoCompleteAdapter) {
            adapter = a
            ivBookMark = view.findViewById(R.id.ivActBookMark)
            ivHistory = view.findViewById(R.id.ivActHistory)
            tvTitle = view.findViewById(R.id.tvActWebTitle)
            tvUrl = view.findViewById(R.id.tvActWebUrl)
        }

        fun bindData(x: WebData): Unit {
            var spannableUrl = SpannableString(x.getWebUrl())
            var indexOfUri = x.getWebUrl().indexOf(adapter.constraint.toString())
            if (indexOfUri >= 0)
                spannableUrl.setSpan(ForegroundColorSpan(Color.parseColor("#348a03")),
                        indexOfUri, indexOfUri + adapter.constraint.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            tvUrl.text = spannableUrl
            var spannableTitle = SpannableString(x.getWebTitle())
            var indexOfTitle = x.getWebTitle().indexOf(adapter.constraint.toString())
            if (indexOfTitle >= 0)
                spannableTitle.setSpan(ForegroundColorSpan(Color.parseColor("#348a03")),
                        indexOfTitle, indexOfTitle + adapter.constraint.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            tvTitle.text = spannableTitle
            if (x.getWebDataType() == WebType.BookMark) {
                ivHistory.visibility = View.GONE
                ivBookMark.visibility = View.VISIBLE
            } else {
                ivHistory.visibility = View.VISIBLE
                ivBookMark.visibility = View.GONE
            }
        }
    }
}