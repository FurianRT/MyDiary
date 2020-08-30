/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.signature.ObjectKey
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.furianrt.mydiary.R
import com.furianrt.mydiary.model.entity.MyNoteWithProp
import com.furianrt.mydiary.utils.applyTextSpans
import com.furianrt.mydiary.utils.getColorCompat
import com.furianrt.mydiary.utils.getDay
import com.furianrt.mydiary.utils.getDayOfWeek
import com.furianrt.mydiary.presentation.general.GlideApp
import com.furianrt.mydiary.presentation.general.StickyHeaderItemDecoration
import com.furianrt.mydiary.presentation.screens.main.adapter.entity.BaseNoteListItem
import com.furianrt.mydiary.presentation.screens.main.adapter.entity.NoteItemDate
import com.furianrt.mydiary.presentation.screens.main.adapter.entity.NoteItemWithImage
import com.furianrt.mydiary.presentation.screens.main.adapter.entity.NoteItemWithText
import kotlinx.android.synthetic.main.activity_main_list_header.view.*
import kotlinx.android.synthetic.main.activity_main_list_note_image.view.*
import kotlinx.android.synthetic.main.activity_main_list_note_text.view.*
import java.text.SimpleDateFormat
import java.util.*

class NoteListAdapter(context: Context) : ListAdapter<BaseNoteListItem,
        NoteListAdapter.BaseNoteViewHolder>(AsyncDifferConfig.Builder(NoteListDiffCallback()).build()),
        ListPreloader.PreloadModelProvider<BaseNoteListItem>,
        StickyHeaderItemDecoration.StickyHeaderInterface {

    companion object {
        private const val MAX_PRELOAD = 20

        private const val TYPE_NOTE_WITH_TEXT = 0
        private const val TYPE_NOTE_WITH_IMAGE = 1
        private const val TYPE_NOTE_DATE = 2
    }

    var listener: OnMainListItemInteractionListener? = null
    var syncEmail: String? = null

    private val mSizeProvider = ViewPreloadSizeProvider<BaseNoteListItem>()
    private val mGlideBuilder = GlideApp.with(context)

    val preloader = RecyclerViewPreloader(Glide.with(context), this, mSizeProvider, MAX_PRELOAD)

    override fun getPreloadItems(position: Int): List<BaseNoteListItem> = listOf(getItem(position))

    override fun getPreloadRequestBuilder(item: BaseNoteListItem): RequestBuilder<*>? =
            if (item is NoteItemWithImage) {
                val image = item.note.images.first()
                mGlideBuilder
                        .load(Uri.parse(image.path))
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .signature(ObjectKey(image.editedTime.toString() + image.name))
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            } else {
                null
            }

    override fun getHeaderLayout(headerPosition: Int): Int = R.layout.activity_main_list_header

    override fun bindHeaderData(header: View, headerPosition: Int) {
        if (headerPosition == RecyclerView.NO_POSITION) {
            header.layoutParams.height = 0
        }

        val item = getItem(headerPosition)

        if (item !is NoteItemDate) return

        header.text_header_date.text = SimpleDateFormat("LLLL yyyy", Locale.getDefault())
                .format(item.time)
                .capitalize(Locale.getDefault())
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is NoteItemWithText -> TYPE_NOTE_WITH_TEXT
        is NoteItemWithImage -> TYPE_NOTE_WITH_IMAGE
        is NoteItemDate -> TYPE_NOTE_DATE
        else -> throw IllegalStateException("Unsupported view type")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseNoteViewHolder =
            when (viewType) {
                TYPE_NOTE_WITH_TEXT -> NoteTextViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.activity_main_list_note_text, parent, false))
                TYPE_NOTE_WITH_IMAGE -> NoteImageViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.activity_main_list_note_image, parent, false)
                        .apply { mSizeProvider.setView(image_main_list) })
                TYPE_NOTE_DATE -> NoteHeaderViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.activity_main_list_header, parent, false))
                else ->
                    throw IllegalStateException("Unsupported view type")
            }

    override fun isHeader(itemPosition: Int): Boolean = if (itemPosition < 0) {
        false
    } else {
        getItem(itemPosition) is NoteItemDate
    }

    override fun getHeaderPositionForItem(itemPosition: Int): Int =
            (itemPosition downTo 0)
                    .map { Pair(isHeader(it), it) }
                    .firstOrNull { it.first }?.second ?: RecyclerView.NO_POSITION

    override fun onBindViewHolder(holder: BaseNoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    abstract class BaseNoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        abstract fun bind(item: BaseNoteListItem)
    }

    inner class NoteTextViewHolder(view: View) : BaseNoteViewHolder(view) {

        override fun bind(item: BaseNoteListItem) {
            with((item as NoteItemWithText)) {
                itemView.setOnClickListener { listener?.onMainListItemClick(this.note) }
                itemView.setOnLongClickListener {
                    listener?.onMainListItemLongClick(this.note)
                    return@setOnLongClickListener true
                }
                itemView.text_note_text_day_of_week.text = getDayOfWeek(this.note.note.time)
                itemView.text_note_text_day.text = getDay(this.note.note.time)
                itemView.text_note_content.setText(this.note.note.content.applyTextSpans(this.note.textSpans), TextView.BufferType.SPANNABLE)
                setTags(this.note)
                setCategory(this.note)
                setSyncIcon(this.note)
                setTitle(this.note)
                selectItem(this)
            }
        }

        private fun setTitle(note: MyNoteWithProp) {
            if (note.note.title.isEmpty()) {
                itemView.text_note_title.visibility = View.GONE
            } else {
                itemView.text_note_title.text = note.note.title
                itemView.text_note_title.visibility = View.VISIBLE
            }
        }

        private fun setTags(note: MyNoteWithProp) {
            val tagsCount = note.tags.count()
            if (tagsCount == 0) {
                itemView.text_tags.visibility = View.GONE
            } else {
                itemView.text_tags.text = tagsCount.toString()
                itemView.text_tags.visibility = View.VISIBLE
            }
        }

        private fun setCategory(note: MyNoteWithProp) {
            val category = note.category
            if (category == null) {
                val noCategoryColor = itemView.context.getColorCompat(R.color.grey_dark)
                itemView.text_note_text_day_of_week.setTextColor(noCategoryColor)
                itemView.text_note_text_day.setTextColor(noCategoryColor)
                itemView.image_note_text_sync.imageTintList = ColorStateList.valueOf(noCategoryColor)
                itemView.view_note_text_category.setBackgroundColor(itemView.context.getColorCompat(R.color.white))
                itemView.text_category.visibility = View.GONE
            } else {
                val withCategoryColor = itemView.context.getColorCompat(R.color.white)
                itemView.text_note_text_day_of_week.setTextColor(withCategoryColor)
                itemView.text_note_text_day.setTextColor(withCategoryColor)
                itemView.image_note_text_sync.imageTintList = ColorStateList.valueOf(withCategoryColor)
                itemView.view_note_text_category.setBackgroundColor(category.color)
                itemView.text_category.text = category.name
                itemView.text_category.visibility = View.VISIBLE
            }
        }

        private fun setSyncIcon(note: MyNoteWithProp) {
            val email = syncEmail
            if (email != null) {
                itemView.image_note_text_sync.visibility = View.VISIBLE
                if (note.isSync(email)) {
                    itemView.image_note_text_sync.setImageResource(R.drawable.ic_cloud_done)
                } else {
                    itemView.image_note_text_sync.setImageResource(R.drawable.ic_cloud_off)
                }
            } else {
                itemView.image_note_text_sync.visibility = View.GONE
            }
        }

        private fun selectItem(item: NoteItemWithText) {
            itemView.layout_note_text_selected.visibility = if (item.selected) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    inner class NoteImageViewHolder(view: View) : BaseNoteViewHolder(view) {

        override fun bind(item: BaseNoteListItem) {
            with((item as NoteItemWithImage)) {
                itemView.setOnClickListener { listener?.onMainListItemClick(this.note) }
                itemView.setOnLongClickListener {
                    listener?.onMainListItemLongClick(this.note)
                    return@setOnLongClickListener true
                }
                itemView.text_note_image_day_of_week.text = getDayOfWeek(this.note.note.time)
                itemView.text_note_image_day.text = getDay(this.note.note.time)
                itemView.text_image_note_content.setText(this.note.note.content.applyTextSpans(this.note.textSpans), TextView.BufferType.SPANNABLE)
                itemView.text_images.text = this.note.images.count().toString()
                setTitle(this.note)
                setTags(this.note)
                setCategory(this.note)
                setSyncIcon(this.note)
                selectItem(this)
                val image = this.note.images.first()
                mGlideBuilder
                        .load(Uri.parse(image.path))
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .signature(ObjectKey(image.editedTime.toString() + image.name))
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(itemView.image_main_list)
            }
        }

        private fun setTitle(note: MyNoteWithProp) {
            if (note.note.title.isEmpty()) {
                itemView.text_image_note_title.visibility = View.GONE
            } else {
                itemView.text_image_note_title.text = note.note.title
                itemView.text_image_note_title.visibility = View.VISIBLE
            }
        }

        private fun setTags(note: MyNoteWithProp) {
            val tagsCount = note.tags.count()
            if (tagsCount == 0) {
                itemView.text_image_tags.visibility = View.GONE
            } else {
                itemView.text_image_tags.text = tagsCount.toString()
                itemView.text_image_tags.visibility = View.VISIBLE
            }
        }

        private fun setCategory(note: MyNoteWithProp) {
            val category = note.category
            if (category == null) {
                val noCategoryColor = itemView.context.getColorCompat(R.color.grey_dark)
                itemView.text_note_image_day_of_week.setTextColor(noCategoryColor)
                itemView.text_note_image_day.setTextColor(noCategoryColor)
                itemView.image_note_image_sync.imageTintList = ColorStateList.valueOf(noCategoryColor)
                itemView.view_note_image_category.setBackgroundColor(itemView.context.getColorCompat(R.color.white))
                itemView.text_image_category.visibility = View.GONE
            } else {
                val withCategoryColor = itemView.context.getColorCompat(R.color.white)
                itemView.text_note_image_day_of_week.setTextColor(withCategoryColor)
                itemView.text_note_image_day.setTextColor(withCategoryColor)
                itemView.image_note_image_sync.imageTintList = ColorStateList.valueOf(withCategoryColor)
                itemView.view_note_image_category.setBackgroundColor(category.color)
                itemView.text_image_category.text = category.name
                itemView.text_image_category.visibility = View.VISIBLE
            }
        }

        private fun setSyncIcon(note: MyNoteWithProp) {
            val email = syncEmail
            if (email != null) {
                itemView.image_note_image_sync.visibility = View.VISIBLE
                if (note.isSync(email)) {
                    itemView.image_note_image_sync.setImageResource(R.drawable.ic_cloud_done)
                } else {
                    itemView.image_note_image_sync.setImageResource(R.drawable.ic_cloud_off)
                }
            } else {
                itemView.image_note_image_sync.visibility = View.GONE
            }
        }

        private fun selectItem(item: NoteItemWithImage) {
            itemView.layout_note_image_selected.visibility = if (item.selected) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    class NoteHeaderViewHolder(view: View) : BaseNoteViewHolder(view) {

        override fun bind(item: BaseNoteListItem) {
            with((item as NoteItemDate).time) {
                itemView.text_header_date.text = SimpleDateFormat("LLLL yyyy", Locale.getDefault())
                        .format(this)
                        .capitalize(Locale.getDefault())
            }
        }
    }

    interface OnMainListItemInteractionListener {
        fun onMainListItemClick(note: MyNoteWithProp)
        fun onMainListItemLongClick(note: MyNoteWithProp)
    }
}