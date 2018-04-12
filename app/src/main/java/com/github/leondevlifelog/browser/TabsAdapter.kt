package com.github.leondevlifelog.browser

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class TabsAdapter(val context: Context) : RecyclerView.Adapter<TabsAdapter.ViewHolder>() {

    var str = mutableListOf("百度网", "腾讯网", "淘宝网")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.item_tabs_content, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return str.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.webTitle?.text = str[position]
        holder.actionCloseTab?.setOnClickListener({
            str.removeAt(position)
            notifyDataSetChanged()
        })
    }

    class ViewHolder : RecyclerView.ViewHolder {
        var webTitle: TextView?
        var actionCloseTab: ImageView?

        constructor(itemView: View?) : super(itemView) {
            webTitle = itemView?.findViewById(R.id.webTitle)
            actionCloseTab = itemView?.findViewById(R.id.actionCloseTab)
        }
    }
}