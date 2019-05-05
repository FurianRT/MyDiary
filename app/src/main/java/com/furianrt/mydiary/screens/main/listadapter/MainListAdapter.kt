package com.furianrt.mydiary.screens.main.listadapter

import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.signature.ObjectKey
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.data.model.MyProfile
import com.furianrt.mydiary.general.GlideApp
import com.furianrt.mydiary.general.HeaderItemDecoration
import com.furianrt.mydiary.utils.*
import kotlinx.android.synthetic.main.activity_main_list_content.view.*
import kotlinx.android.synthetic.main.activity_main_list_header.view.*
import java.util.*

class MainListAdapter(
        var selectedNotes: ArrayList<MyNoteWithProp> = ArrayList(),
        val is24TimeFormat: Boolean
) : ListAdapter<MainListItem, MainListAdapter.MyViewHolder>(MainDiffCallback()),
        HeaderItemDecoration.StickyHeaderInterface {

    companion object {
        private const val PREVIEW_IMAGE_SIZE = 200
    }

    var listener: OnMainListItemInteractionListener? = null
    var profile: MyProfile? = null

    override fun bindHeaderData(header: View, headerPosition: Int) {
        if (headerPosition == RecyclerView.NO_POSITION) {
            header.layoutParams.height = 0
        }

        val time = (getItem(headerPosition) as MainHeaderItem).time
        header.text_date.text = header.context.getString(
                R.string.note_list_date_format,
                getMonth(time),
                getYear(time)
        )
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

    abstract class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: MainListItem)
    }

    inner class HeaderViewHolder(view: View) : MyViewHolder(view) {

        private lateinit var mHeaderItem: MainHeaderItem

        override fun bind(item: MainListItem) {
            mHeaderItem = item as MainHeaderItem
            itemView.text_date.text = itemView.context.getString(
                    R.string.note_list_date_format,
                    getMonth(mHeaderItem.time),
                    getYear(mHeaderItem.time)
            )
        }
    }

    inner class ContentViewHolder(view: View) : MyViewHolder(view) {

        override fun bind(item: MainListItem) {
            if (item !is MainContentItem) {
                return
            }
            with(itemView) {
                setOnClickListener {
                    listener?.onMainListItemClick(item.note, layoutPosition)
                }
                setOnLongClickListener {
                    listener?.onMainListItemLongClick(item.note, layoutPosition)
                    return@setOnLongClickListener true
                }
                text_day_of_week.text = getDayOfWeek(item.note.note.time)
                text_day.text = getDay(item.note.note.time)
                text_time.text = getTime(item.note.note.time, is24TimeFormat)
                text_tags.text = item.note.tags.filter { !it.isDeleted }.size.toString()
                text_images.text = item.note.images.filter { !it.isDeleted }.size.toString()
                setCategory(item)
                setSyncIcon(item)
                setPreviewImage(item)
                setTitle(item)
                text_note_content.text = item.note.note.content
                selectItem(item.note)
            }
        }

        private fun setCategory(item: MainContentItem) {
            with(itemView) {
                val categoryColor = item.note.category?.color
                if (categoryColor != null) {
                    text_day_of_week.setBackgroundColor(categoryColor)
                } else {
                    text_day_of_week.setBackgroundColor(Color.BLACK)
                }

                if (item.note.category != null) {
                    text_category.text = item.note.category?.name
                    text_category.visibility = View.VISIBLE
                } else {
                    text_category.visibility = View.INVISIBLE
                    text_category.text = ""
                }
            }
        }

        private fun setSyncIcon(item: MainContentItem) {
            with(itemView) {
                val tempProfile = profile
                if (tempProfile == null) {
                    image_sync.visibility = View.INVISIBLE
                } else {
                    if (item.note.note.isSync(tempProfile.email)
                            && item.note.images.find { !it.isSync(tempProfile.email) } == null
                            && item.note.tags.find { !it.isSync(tempProfile.email) } == null) {
                        image_sync.setImageResource(R.drawable.ic_cloud_done)
                        image_sync.setColorFilter(getThemeAccentColor(itemView.context), PorterDuff.Mode.SRC_IN)
                    } else {
                        image_sync.setImageResource(R.drawable.ic_cloud_off)
                        image_sync.setColorFilter(
                                ContextCompat.getColor(itemView.context, R.color.red),
                                PorterDuff.Mode.SRC_IN)
                    }
                    image_sync.visibility = View.VISIBLE
                }
            }
        }

        private fun setPreviewImage(item: MainContentItem) {
            with(itemView) {
                val notDeletedImages = item.note.images.filter { !it.isDeleted }
                if (notDeletedImages.isEmpty()) {
                    image_main_list.visibility = View.GONE
                    image_main_list.setImageDrawable(null)
                } else {
                    image_main_list.visibility = View.VISIBLE
                    GlideApp.with(itemView)
                            .load(Uri.parse(notDeletedImages.first().uri))
                            .override(PREVIEW_IMAGE_SIZE, PREVIEW_IMAGE_SIZE)
                            .signature(ObjectKey(notDeletedImages.first().editedTime))
                            .into(image_main_list)
                }
            }
        }

        private fun setTitle(item: MainContentItem) {
            with(itemView) {
                val title = item.note.note.title
                if (title.isEmpty()) {
                    text_note_title.visibility = View.GONE
                } else {
                    text_note_title.text = title
                    text_note_title.visibility = View.VISIBLE
                }
            }
        }

        private fun selectItem(note: MyNoteWithProp) {
            itemView.view_selected.visibility = if (selectedNotes.contains(note)) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
        }
    }

    interface OnMainListItemInteractionListener {
        fun onMainListItemClick(note: MyNoteWithProp, position: Int)
        fun onMainListItemLongClick(note: MyNoteWithProp, position: Int)
    }
}