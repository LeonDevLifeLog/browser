package com.github.leondevlifelog.browser.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.leondevlifelog.browser.R
import com.github.leondevlifelog.browser.database.entities.History
import java.text.SimpleDateFormat

class AdapterHistory(var data: List<History>?) : RecyclerView.Adapter<AdapterHistory.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, null)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (data == null) 0 else data?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(data!![position])
    }

    class ViewHolder : RecyclerView.ViewHolder {

        private var tvTitle: TextView?
        private var tvUrl: TextView?
        private var tvTime: TextView?

        constructor(itemView: View?) : super(itemView) {
            tvTitle = itemView?.findViewById<TextView>(R.id.tvTitleItem)
            tvUrl = itemView?.findViewById<TextView>(R.id.tvUrlItem)
            tvTime = itemView?.findViewById<TextView>(R.id.tvTimeItem)
        }

        fun setData(x: History): Unit {
            tvTitle?.text = x.title
            tvUrl?.text = x.url
            tvTime?.text = SimpleDateFormat("yyyy-MM-dd hh:mm").format(x.time)
        }
    }
}