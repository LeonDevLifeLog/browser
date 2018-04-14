package com.github.leondevlifelog.browser

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.github.leondevlifelog.browser.bean.TabInfo

class TabsAdapter(val context: Context, var tabs: ObversableTabsInfo) : RecyclerView.Adapter<TabsAdapter.ViewHolder>() {
    interface EventLintener {
        fun onSelectedChanged(tab: TabInfo)
        fun onClosed(v: View, tab: TabInfo?)
    }

    private var eventListeners = mutableListOf<EventLintener>()
    fun addOnSelectedTabChangedListner(onSelectedTabChangedLister: EventLintener) {
        eventListeners.add(onSelectedTabChangedLister)
    }

    fun removeOnSelectedTabChangedListner(onSelectedTabChangedLister: EventLintener) {
        eventListeners.remove(onSelectedTabChangedLister)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.item_tabs_content, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tabs.size()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view?.setOnClickListener {
            tabs.selectedTab = tabs.get(position)
            eventListeners.forEach {
                it.onSelectedChanged(tabs.get(position))
            }
        }
        if (tabs.get(position) === tabs.selectedTab) {
            holder.webTitle?.setTextColor(ContextCompat.getColor(context, R.color.colorSecurityScheme))
        } else {
            holder.webTitle?.setTextColor(ContextCompat.getColor(context, R.color.colorIcon))
        }
        holder.webTitle?.text = tabs.get(position).title
        holder.actionCloseTab?.setOnClickListener({ v ->
            var tab = tabs.get(position)
            tabs.remove(tab)
            if (position >= 1) tabs.selectedTab = tabs.get(position - 1)
            else tabs.selectedTab = tabs.get(0)
            notifyItemRemoved(position)
            eventListeners.forEach {
                it.onClosed(v, tabs.selectedTab)
            }
        })
    }

    class ViewHolder : RecyclerView.ViewHolder {
        var webTitle: TextView?
        var actionCloseTab: ImageView?
        var view: View?

        constructor(itemView: View?) : super(itemView) {
            view = itemView
            webTitle = itemView?.findViewById(R.id.webTitle)
            actionCloseTab = itemView?.findViewById(R.id.actionCloseTab)
        }
    }
}