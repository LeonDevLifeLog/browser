package com.github.leondevlifelog.browser.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.github.leondevlifelog.browser.R
import com.github.leondevlifelog.browser.database.entities.History
import java.text.SimpleDateFormat

class AdapterHistory(var data: List<History>?) : RecyclerView.Adapter<AdapterHistory.ViewHolder>() {
    interface OnItemClickListener {
        fun onClick(view: View, position: Int)
    }

    var onItemClickListener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (data == null) 0 else data?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onItemClickListener?.onClick(it, position)
        }
        holder.setData(data!![position])
    }

    class ViewHolder : RecyclerView.ViewHolder {

        private var tvTitle: TextView?
        private var tvUrl: TextView?
        private var tvTime: TextView?
        private var itemHistory: LinearLayout?

        constructor(itemView: View?) : super(itemView) {
            tvTitle = itemView?.findViewById(R.id.tvTitleItem)
            tvUrl = itemView?.findViewById(R.id.tvUrlItem)
            tvTime = itemView?.findViewById(R.id.tvTimeItem)
            itemHistory = itemView?.findViewById(R.id.itemHistory)
        }

        fun setData(x: History): Unit {
            tvTitle?.text = x.title
            tvUrl?.text = x.url
            tvTime?.text = SimpleDateFormat("yyyy-MM-dd hh:mm").format(x.time)
        }
    }
}