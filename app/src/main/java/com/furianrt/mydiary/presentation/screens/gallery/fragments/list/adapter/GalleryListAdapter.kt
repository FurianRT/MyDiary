/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.gallery.fragments.list.adapter

import android.annotation.SuppressLint
import android.graphics.Point
import android.graphics.Rect
import android.net.Uri
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.furianrt.mydiary.R
import com.furianrt.mydiary.model.entity.MyImage
import com.furianrt.mydiary.presentation.general.GlideApp
import com.furianrt.mydiary.presentation.general.ItemTouchHelperCallback
import kotlinx.android.synthetic.main.fragment_gallery_list_item.view.*

@SuppressLint("ClickableViewAccessibility")
class GalleryListAdapter(
        var listener: OnListItemInteractionListener,
        recyclerView: RecyclerView,
        var trashPoint: Point = Point(),
        var selectedImageNames: HashSet<String> = HashSet()
) : ListAdapter<ImagesListItem, GalleryListAdapter.BaseViewHolder>(
        AsyncDifferConfig.Builder(GalleryListDiffCallback())
                .build()
) {
    private val mItemTouchHelper: ItemTouchHelper
    private var mDraggedItem: View? = null
    private val mDraggedItemPosition = Rect()
    private var mIsOverTrash = false

    init {
        val callback = ItemTouchHelperCallback(this)
        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper.attachToRecyclerView(recyclerView)
        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_MOVE) {
                mDraggedItem?.let {
                    it.getDrawingRect(mDraggedItemPosition)
                    mDraggedItemPosition.offset(it.x.toInt(), it.y.toInt())
                    if (mDraggedItemPosition.contains(trashPoint.x, trashPoint.y)) {
                        if (!mIsOverTrash) {
                            mIsOverTrash = true
                            listener.onItemDragOverTrash(it)
                        }
                    } else {
                        if (mIsOverTrash) {
                            mIsOverTrash = false
                            listener.onItemDragOutOfTrash(it)
                        }
                    }
                }
            }
            return@setOnTouchListener false
        }
    }

    private val mList = mutableListOf<ImagesListItem>()

    fun getImages(): List<MyImage> = mList
            .filterIsInstance(ImagesListItem.Image::class.java)
            .map { it.image }

    override fun submitList(list: List<ImagesListItem>?) {
        submitList(list, null)
    }

    override fun submitList(list: List<ImagesListItem>?, commitCallback: Runnable?) {
        mList.clear()
        mList.addAll(list ?: emptyList())
        super.submitList(list?.map { it.clone() }, commitCallback)
    }

    /*fun submitList(list: List<MyImage>) {
        val newViewItems = list
                .map { ImagesListItem.Image(it) }
                .toMutableList<ImagesListItem>()
                .apply { add(ImagesListItem.Footer()) }
        val diffResult = DiffUtil.calculateDiff(GalleryListDiffCallback(mList, newViewItems))
        mList.clear()
        mList.addAll(newViewItems)
        diffResult.dispatchUpdatesTo(this)
    }*/

    fun deactivateSelection() {
        mList.forEachIndexed { index, item ->
            if (item is ImagesListItem.Image && selectedImageNames.contains(item.image.name)) {
                notifyItemChanged(index)
            }
        }
        selectedImageNames.clear()
    }

    override fun getItemViewType(position: Int): Int = getItem(position).getTypeId()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder = when (viewType) {
        ImagesListItem.Image.TYPE_ID -> ViewHolderImage(LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_gallery_list_item, parent, false))
        ImagesListItem.Footer.TYPE_ID -> ViewHolderFooter(LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_gallery_list_footer, parent, false))
        else -> throw IllegalStateException("Unsupported item type")
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < 0 || toPosition < 0 || fromPosition >= mList.size || toPosition >= mList.size) {
            return
        }
        mList.add(toPosition, mList.removeAt(fromPosition))
        notifyItemMoved(fromPosition, toPosition)
    }

    fun onItemDismiss(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
    }

    abstract class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: ImagesListItem)
        abstract fun onItemClear()
        abstract fun onItemSelected()
        abstract fun onItemReleased()
    }

    class ViewHolderFooter(view: View) : BaseViewHolder(view) {
        override fun bind(item: ImagesListItem) {
            itemView.visibility = View.VISIBLE
        }

        override fun onItemClear() {}
        override fun onItemSelected() {}
        override fun onItemReleased() {}
    }

    inner class ViewHolderImage(view: View) : BaseViewHolder(view) {

        private var mImage: MyImage? = null
        private var mIsTrashed = false

        override fun bind(item: ImagesListItem) {
            with((item as ImagesListItem.Image).image) {
                mImage = this
                itemView.alpha = 1f
                itemView.setOnClickListener { listener.onListItemClick(this, adapterPosition) }
                itemView.setOnLongClickListener {
                    mItemTouchHelper.startDrag(this@ViewHolderImage)
                    return@setOnLongClickListener true
                }
                GlideApp.with(itemView)
                        .load(Uri.parse(this.path))
                        .signature(ObjectKey(this.editedTime.toString() + this.name))
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(itemView.image_gallery_item)

                if (mIsTrashed) {
                    itemView.visibility = View.GONE
                } else {
                    itemView.visibility = View.VISIBLE
                    if (selectedImageNames.contains(this.name)) {
                        itemView.layout_selection.visibility = View.VISIBLE
                    } else {
                        itemView.layout_selection.visibility = View.INVISIBLE
                    }
                }
            }
        }

        override fun onItemClear() {
            mList.filterIsInstance(ImagesListItem.Image::class.java).forEachIndexed { index, item ->
                item.image.order = index
            }
            listener.onListItemDropped(itemView)
            listener.onImagesOrderChange(mList.filterIsInstance(ImagesListItem.Image::class.java).map { it.image })
            mIsTrashed = false
        }

        override fun onItemSelected() {
            mDraggedItem = itemView
            listener.onListItemStartDrag(itemView)
        }

        override fun onItemReleased() {
            if (mIsOverTrash) {
                mImage?.let { image ->
                    mIsTrashed = true
                    mDraggedItem?.visibility = View.GONE
                    listener.onItemTrashed(image)
                }
            }
            mIsOverTrash = false
            mDraggedItem = null
        }
    }

    interface OnListItemInteractionListener {
        fun onListItemClick(image: MyImage, position: Int)
        fun onImagesOrderChange(images: List<MyImage>)
        fun onListItemStartDrag(item: View)
        fun onListItemDropped(item: View)
        fun onItemDragOverTrash(item: View)
        fun onItemDragOutOfTrash(item: View)
        fun onItemTrashed(image: MyImage)
    }
}