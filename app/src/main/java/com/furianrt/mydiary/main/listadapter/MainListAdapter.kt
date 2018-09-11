package com.furianrt.mydiary.main.listadapter

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.general.HeaderItemDecoration
import com.furianrt.mydiary.utils.*
import kotlinx.android.synthetic.main.activity_main_list_content.view.*
import kotlinx.android.synthetic.main.activity_main_list_date.view.*
import kotlinx.android.synthetic.main.activity_main_list_header.view.*

class MainListAdapter(private val mListener: OnMainListItemInteractionListener)
    : ListAdapter<MainListItem, MainListAdapter.MyViewHolder>(MainDiffCallback()),
        HeaderItemDecoration.StickyHeaderInterface {

    override fun bindHeaderData(header: View, headerPosition: Int) {
        if (headerPosition == RecyclerView.NO_POSITION) {
            header.layoutParams.height = 0
        }

        val time = (getItem(headerPosition) as MainHeaderItem).time
        val date = getMonth(time) + ", " + getYear(time)

        header.text_date.text = date
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.activity_main_list_header -> HeaderViewHolder(view)
            R.layout.activity_main_list_content -> ContentViewHolder(view)
            else -> throw IllegalStateException("unsupported item type")
        }
    }

    override fun isHeader(itemPosition: Int): Boolean {
        return getItem(itemPosition) is MainHeaderItem
    }

    override fun getHeaderPositionForItem(itemPosition: Int): Int =
            (itemPosition downTo 0)
                    .map { Pair(isHeader(it), it) }
                    .firstOrNull { it.first }?.second ?: RecyclerView.NO_POSITION

    override fun getHeaderLayout(headerPosition: Int): Int {
        return R.layout.activity_main_list_header
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).getType()
    }

    interface OnMainListItemInteractionListener {

        fun onMainListItemClick(note: MyNoteWithProp)

        fun onMainListItemLongClick(note: MyNoteWithProp)
    }

    abstract class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: MainListItem)
    }

    inner class HeaderViewHolder(private val view: View) : MyViewHolder(view) {

        private lateinit var mHeaderItem: MainHeaderItem

        override fun bind(item: MainListItem) {
            mHeaderItem = item as MainHeaderItem
            val date = getMonth(mHeaderItem.time) + ", " + getYear(mHeaderItem.time)
            view.apply {
                text_date.text = date
            }
        }
    }

    inner class ContentViewHolder(private val view: View) : MyViewHolder(view), View.OnClickListener,
            View.OnLongClickListener {

        private lateinit var mContentItem: MainContentItem

        override fun bind(item: MainListItem) {
            mContentItem = item as MainContentItem
            val time = mContentItem.note.note.time
            view.apply {
                setOnClickListener(this@ContentViewHolder)
                setOnLongClickListener(this@ContentViewHolder)
                text_day_of_week.text = getDayOfWeek(time)
                text_day.text = getDay(time)
                text_time.text = getTime(time)
                val title = mContentItem.note.note.title
                if (title.isEmpty()) {
                    text_note_title.visibility = View.GONE
                } else {
                    text_note_title.text = title
                }
                text_note_content.text = mContentItem.note.note.content
            }
        }

        override fun onClick(view: View?) {
            mListener.onMainListItemClick(mContentItem.note)
        }

        override fun onLongClick(v: View?): Boolean {
            mListener.onMainListItemLongClick(mContentItem.note)
            return true
        }
    }
}