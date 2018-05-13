package com.github.leondevlifelog.browser.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.github.leondevlifelog.browser.R
import com.github.leondevlifelog.browser.database.entities.BookMark
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.models.ExpandableList
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import java.text.SimpleDateFormat

class MyExpandableRecyclerViewAdapter(groups: MutableList<CategorpExpandableGroup>) : ExpandableRecyclerViewAdapter<MyExpandableRecyclerViewAdapter.HeadViewHolder, MyExpandableRecyclerViewAdapter.MyChildViewHolder>(groups) {
    val TAG: String = "MyExpandableRecycler"
    var isCheckedMode: Boolean = false
    var eventListener: EventListener? = null

    interface EventListener {
        fun onChildLongClick(x: Int, y: Int)
        fun onChildClick(x: Int, y: Int)
        fun onParentLongClick(x: Int): Unit
        fun onParentClick(x: Int): Unit
    }

    override fun onCreateGroupViewHolder(parent: ViewGroup?, viewType: Int): HeadViewHolder {
        var view = LayoutInflater.from(parent?.context).inflate(R.layout.view_head, parent, false)
        return HeadViewHolder(view)
    }

    fun setData(x: MutableList<CategorpExpandableGroup>): Unit {
        expandableList = ExpandableList(x)
        notifyDataSetChanged()
    }

    override fun onCreateChildViewHolder(parent: ViewGroup?, viewType: Int): MyChildViewHolder {
        var view = LayoutInflater.from(parent?.context).inflate(R.layout.item_bookmark, parent, false)
        var viewHolder = MyChildViewHolder(view)
        return viewHolder
    }

    override fun onBindChildViewHolder(holder: MyChildViewHolder?, flatPosition: Int, group: ExpandableGroup<*>?, childIndex: Int) {
        Log.d(TAG, "onBindChildViewHolder: ${(group as CategorpExpandableGroup).items[childIndex]}")
        holder?.itemHistory?.setOnLongClickListener {
            eventListener?.onChildLongClick(flatPosition, childIndex)
            return@setOnLongClickListener true
        }
        holder?.itemHistory?.setOnClickListener {
            eventListener?.onChildClick(flatPosition, childIndex)
        }
        holder?.setData(group.items[childIndex], isCheckedMode)
    }

    override fun onBindGroupViewHolder(holder: HeadViewHolder?, flatPosition: Int, group: ExpandableGroup<*>?) {
        holder?.categoryTitle?.text = (group as CategorpExpandableGroup).title
    }

    open class HeadViewHolder : GroupViewHolder {
        var categoryTitle: TextView?

        constructor(itemView: View?) : super(itemView) {
            categoryTitle = itemView?.findViewById(R.id.tvCategory)
        }
    }

    open class MyChildViewHolder : ChildViewHolder {
        private var tvTitle: TextView?
        private var tvUrl: TextView?
        private var tvTime: TextView?
        var itemHistory: LinearLayout?
        var cbBookmark: CheckBox?

        constructor(itemView: View?) : super(itemView) {
            tvTitle = itemView?.findViewById(R.id.tvTitleItem)
            tvUrl = itemView?.findViewById(R.id.tvUrlItem)
            tvTime = itemView?.findViewById(R.id.tvTimeItem)
            itemHistory = itemView?.findViewById(R.id.itemHistory)
            cbBookmark = itemView?.findViewById(R.id.checkedBookmark)
        }

        fun setData(x: BookMark, isCheckMode: Boolean): Unit {
            tvTitle?.text = x.title
            tvUrl?.text = x.url
            tvTime?.text = SimpleDateFormat("yyyy-MM-dd hh:mm").format(x.time)
            cbBookmark?.visibility = if (isCheckMode) View.VISIBLE else View.GONE
        }
    }
}