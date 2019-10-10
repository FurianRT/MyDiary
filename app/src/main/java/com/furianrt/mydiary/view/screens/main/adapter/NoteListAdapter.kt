/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.main.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
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
import com.furianrt.mydiary.data.entity.MyNoteWithProp
import com.furianrt.mydiary.utils.applyTextSpans
import com.furianrt.mydiary.utils.getDay
import com.furianrt.mydiary.utils.getDayOfWeek
import com.furianrt.mydiary.view.general.GlideApp
import com.furianrt.mydiary.view.general.StickyHeaderItemDecoration
import kotlinx.android.synthetic.main.activity_main_list_header.view.*
import kotlinx.android.synthetic.main.activity_main_list_note_image.view.*
import kotlinx.android.synthetic.main.activity_main_list_note_text.view.*
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*

class NoteListAdapter(
        context: Context,
        var selectedNoteIds: HashSet<String> = HashSet()
) : RecyclerView.Adapter<NoteListAdapter.BaseNoteViewHolder>(),
        ListPreloader.PreloadModelProvider<NoteListAdapter.NoteItemView>,
        StickyHeaderItemDecoration.StickyHeaderInterface {

    companion object {
        private const val MAX_PRELOAD = 20
    }

    class NoteItemView(
            val type: Int,
            val note: MyNoteWithProp? = null,
            val time: Long? = null
    ) {
        companion object {
            const val TYPE_NOTE_WITH_IMAGE = 0
            const val TYPE_NOTE_WITH_TEXT = 1
            const val TYPE_HEADER = 2
        }
    }

    var listener: OnMainListItemInteractionListener? = null
    var syncEmail: String? = null

    private val mItems = mutableListOf<NoteItemView>()
    private val mSizeProvider = ViewPreloadSizeProvider<NoteItemView>()
    private val mGlideBuilder = GlideApp.with(context)

    val preloader = RecyclerViewPreloader<NoteItemView>(
            Glide.with(context), this, mSizeProvider, MAX_PRELOAD
    )

    fun submitList(items: List<NoteItemView>) {
        val diffResult = DiffUtil.calculateDiff(NoteListDiffCallback(mItems, items))
        mItems.clear()
        mItems.addAll(items)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getPreloadItems(position: Int): MutableList<NoteItemView> =
            mItems.subList(position, position + 1)

    override fun getPreloadRequestBuilder(item: NoteItemView): RequestBuilder<*>? =
            if (item.type == NoteItemView.TYPE_NOTE_WITH_IMAGE) {
                mGlideBuilder
                        .load(Uri.parse(item.note!!.images.first().uri))
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .signature(ObjectKey(item.note.images.first().editedTime))
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
        header.text_header_date.text = SimpleDateFormat("LLLL yyyy", Locale.getDefault())
                .format(mItems[headerPosition].time)
                .capitalize()
    }

    override fun getItemCount(): Int = mItems.size

    override fun getItemViewType(position: Int): Int = mItems[position].type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseNoteViewHolder =
            when (viewType) {
                NoteItemView.TYPE_NOTE_WITH_TEXT ->
                    NoteTextViewHolder(LayoutInflater.from(parent.context)
                            .inflate(R.layout.activity_main_list_note_text, parent, false))
                NoteItemView.TYPE_NOTE_WITH_IMAGE ->
                    NoteImageViewHolder(LayoutInflater.from(parent.context)
                            .inflate(R.layout.activity_main_list_note_image, parent, false)
                            .apply { mSizeProvider.setView(image_main_list) })
                NoteItemView.TYPE_HEADER ->
                    NoteHeaderViewHolder(LayoutInflater.from(parent.context)
                            .inflate(R.layout.activity_main_list_header, parent, false))
                else ->
                    throw IllegalArgumentException()
            }

    override fun isHeader(itemPosition: Int) = mItems[itemPosition].type == NoteItemView.TYPE_HEADER

    override fun getHeaderPositionForItem(itemPosition: Int): Int =
            (itemPosition downTo 0)
                    .map { Pair(isHeader(it), it) }
                    .firstOrNull { it.first }?.second ?: RecyclerView.NO_POSITION

    override fun onBindViewHolder(holder: BaseNoteViewHolder, position: Int) {
        when (holder) {
            is NoteTextViewHolder -> mItems[position].note?.let { holder.bind(it) }
            is NoteImageViewHolder -> mItems[position].note?.let { holder.bind(it) }
            is NoteHeaderViewHolder -> mItems[position].time?.let { holder.bind(it) }
        }
    }

    abstract class BaseNoteViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class NoteTextViewHolder(view: View) : BaseNoteViewHolder(view) {

        fun bind(note: MyNoteWithProp) {
            itemView.setOnClickListener { listener?.onMainListItemClick(note) }
            itemView.setOnLongClickListener {
                listener?.onMainListItemLongClick(note)
                return@setOnLongClickListener true
            }
            itemView.text_note_text_day_of_week.text = getDayOfWeek(note.note.time)
            itemView.text_note_text_day.text = getDay(note.note.time)
            itemView.text_note_content.setText(note.note.content.applyTextSpans(note.textSpans), TextView.BufferType.EDITABLE)
            setCategory(note)
            setSyncIcon(note)
            setTitle(note)
            selectItem(note.note.id)
        }

        private fun setTitle(note: MyNoteWithProp) {
            if (note.note.title.isEmpty()) {
                itemView.text_note_title.visibility = View.GONE
            } else {
                itemView.text_note_title.text = note.note.title
                itemView.text_note_title.visibility = View.VISIBLE
            }
        }

        private fun setCategory(note: MyNoteWithProp) {
            val color = note.category?.color
            if (color == null) {
                val noCategoryColor = ContextCompat.getColor(itemView.context, R.color.grey_dark)
                itemView.text_note_text_day_of_week.setTextColor(noCategoryColor)
                itemView.text_note_text_day.setTextColor(noCategoryColor)
                itemView.image_note_text_sync.imageTintList = ColorStateList.valueOf(noCategoryColor)
                itemView.view_note_text_category.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.white))
            } else {
                val withCategoryColor = ContextCompat.getColor(itemView.context, R.color.white)
                itemView.text_note_text_day_of_week.setTextColor(withCategoryColor)
                itemView.text_note_text_day.setTextColor(withCategoryColor)
                itemView.image_note_text_sync.imageTintList = ColorStateList.valueOf(withCategoryColor)
                itemView.view_note_text_category.setBackgroundColor(color)
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

        private fun selectItem(noteId: String) {
            itemView.layout_note_text_selected.visibility = if (selectedNoteIds.contains(noteId)) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    inner class NoteImageViewHolder(view: View) : BaseNoteViewHolder(view) {

        fun bind(note: MyNoteWithProp) {
            itemView.setOnClickListener { listener?.onMainListItemClick(note) }
            itemView.setOnLongClickListener {
                listener?.onMainListItemLongClick(note)
                return@setOnLongClickListener true
            }
            itemView.text_note_image_day_of_week.text = getDayOfWeek(note.note.time)
            itemView.text_note_image_day.text = getDay(note.note.time)
            setCategory(note)
            setSyncIcon(note)
            selectItem(note.note.id)
            mGlideBuilder
                    .load(Uri.parse(note.images.first().uri))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .signature(ObjectKey(note.images.first().editedTime))
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(itemView.image_main_list)
        }

        private fun setCategory(note: MyNoteWithProp) {
            val color = note.category?.color
            if (color == null) {
                val noCategoryColor = ContextCompat.getColor(itemView.context, R.color.grey_dark)
                itemView.text_note_image_day_of_week.setTextColor(noCategoryColor)
                itemView.text_note_image_day.setTextColor(noCategoryColor)
                itemView.image_note_image_sync.imageTintList = ColorStateList.valueOf(noCategoryColor)
                itemView.view_note_image_category.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.white))
            } else {
                val withCategoryColor = ContextCompat.getColor(itemView.context, R.color.white)
                itemView.text_note_image_day_of_week.setTextColor(withCategoryColor)
                itemView.text_note_image_day.setTextColor(withCategoryColor)
                itemView.image_note_image_sync.imageTintList = ColorStateList.valueOf(withCategoryColor)
                itemView.view_note_image_category.setBackgroundColor(color)
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

        private fun selectItem(noteId: String) {
            itemView.layout_note_image_selected.visibility = if (selectedNoteIds.contains(noteId)) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    inner class NoteHeaderViewHolder(view: View) : BaseNoteViewHolder(view) {

        fun bind(time: Long) {
            itemView.text_header_date.text = SimpleDateFormat("LLLL yyyy", Locale.getDefault())
                    .format(time)
                    .capitalize()
        }
    }

    interface OnMainListItemInteractionListener {
        fun onMainListItemClick(note: MyNoteWithProp)
        fun onMainListItemLongClick(note: MyNoteWithProp)
    }
}