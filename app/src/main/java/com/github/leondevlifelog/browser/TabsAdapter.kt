package com.github.leondevlifelog.browser

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.github.leondevlifelog.browser.bean.TabInfo

class TabsAdapter(val context: Context, var tabs: MutableList<TabInfo>) : RecyclerView.Adapter<TabsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.item_tabs_content, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tabs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.webTitle?.text = tabs[position].title
        holder.actionCloseTab?.setOnClickListener({
            tabs.removeAt(position)
            notifyItemRemoved(position)
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