package com.furianrt.mydiary.main.listadapter

import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.general.GlideApp
import com.furianrt.mydiary.general.HeaderItemDecoration
import com.furianrt.mydiary.utils.*
import kotlinx.android.synthetic.main.activity_main_list_content.view.*
import kotlinx.android.synthetic.main.activity_main_list_date.view.*
import kotlinx.android.synthetic.main.activity_main_list_header.view.*
import java.util.*

class MainListAdapter(
        var selectedNotes: ArrayList<MyNoteWithProp> = ArrayList(),
        val is24TimeFormat: Boolean
) : ListAdapter<MainListItem, MainListAdapter.MyViewHolder>(MainDiffCallback()),
        HeaderItemDecoration.StickyHeaderInterface {

    var listener: OnMainListItemInteractionListener? = null
    var hasPremium = false

    override fun bindHeaderData(header: View, headerPosition: Int) {
        if (headerPosition == RecyclerView.NO_POSITION) {
            header.layoutParams.height = 0
        }

        val time = (getItem(headerPosition) as MainHeaderItem).time
        val date = getMonth(time).toUpperCase(Locale.getDefault()) + ", " + getYear(time)

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

    override fun getHeaderLayout(headerPosition: Int): Int = R.layout.activity_main_list_header

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int = getItem(position).getType()

    interface OnMainListItemInteractionListener {

        fun onMainListItemClick(note: MyNoteWithProp, position: Int)

        fun onMainListItemLongClick(note: MyNoteWithProp, position: Int)
    }

    abstract class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: MainListItem)
    }

    inner class HeaderViewHolder(private val view: View) : MyViewHolder(view) {

        private lateinit var mHeaderItem: MainHeaderItem

        override fun bind(item: MainListItem) {
            mHeaderItem = item as MainHeaderItem
            val date = getMonth(mHeaderItem.time)
                    .toUpperCase(Locale.getDefault()) + ", " + getYear(mHeaderItem.time)
            view.apply {
                text_date.text = date
            }
        }
    }

    inner class ContentViewHolder(
            private val mView: View
    ) : MyViewHolder(mView), View.OnClickListener, View.OnLongClickListener {

        private lateinit var mContentItem: MainContentItem

        override fun bind(item: MainListItem) {
            mContentItem = item as MainContentItem
            val time = mContentItem.note.note.time
            mView.apply {
                setOnClickListener(this@ContentViewHolder)
                setOnLongClickListener(this@ContentViewHolder)
                text_day_of_week.text = getDayOfWeek(time)
                val categoryColor = mContentItem.note.category?.color
                if (categoryColor != null) {
                    text_day_of_week.setBackgroundColor(categoryColor)
                } else {
                    text_day_of_week.setBackgroundColor(Color.BLACK)
                }
                categoryColor?.let { text_day_of_week.setBackgroundColor(it) }
                text_day.text = getDay(time)
                text_time.text = getTime(time, is24TimeFormat)
                text_tags.text = mContentItem.note.tags.filter { !it.isDeleted }.size.toString()
                text_images.text = mContentItem.note.images.filter { !it.isDeleted }.size.toString()

                if (mContentItem.note.category != null) {
                    text_category.text = mContentItem.note.category?.name
                    text_category.visibility = View.VISIBLE
                } else {
                    text_category.visibility = View.INVISIBLE
                    text_category.text = ""
                }

                if (!hasPremium) {
                    image_sync.visibility = View.INVISIBLE
                } else {
                    if (item.note.note.isSync
                            && item.note.images.find { !it.isSync || it.isDeleted } == null
                            && item.note.tags.find { !it.isSync || it.isDeleted } == null) {
                        image_sync.setImageResource(R.drawable.ic_cloud_done)
                        image_sync.setColorFilter(getThemeAccentColor(mView.context), PorterDuff.Mode.SRC_IN)
                    } else {
                        image_sync.setImageResource(R.drawable.ic_cloud_off)
                        image_sync.setColorFilter(
                                ContextCompat.getColor(itemView.context, R.color.red),
                                PorterDuff.Mode.SRC_IN)
                    }
                    image_sync.visibility = View.VISIBLE
                }

                if (mContentItem.note.images.isEmpty()) {
                    image_main_list.setImageDrawable(null)
                } else {
                    GlideApp.with(itemView)
                            .load(Uri.parse(mContentItem.note.images[0].uri))
                            .override(200, 200)
                            .into(image_main_list)
                }

                val title = mContentItem.note.note.title
                if (title.isEmpty()) {
                    text_note_title.visibility = View.GONE
                } else {
                    text_note_title.text = title
                    text_note_title.visibility = View.VISIBLE
                }
                text_note_content.text = mContentItem.note.note.content

                selectItem(mContentItem.note)
            }
        }

        override fun onClick(view: View?) {
            listener?.onMainListItemClick(mContentItem.note, layoutPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            listener?.onMainListItemLongClick(mContentItem.note, layoutPosition)
            return true
        }

        private fun selectItem(note: MyNoteWithProp) {
            val cardView = mView as CardView
            if (selectedNotes.contains(note)) {
                cardView.setCardBackgroundColor(getThemeAccentColor(mView.context))
            } else {
                cardView.setCardBackgroundColor(Color.WHITE)
            }
        }
    }
}