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

package com.furianrt.mydiary.presentation.screens.gallery.fragments.list.adapter

import android.annotation.SuppressLint
import android.graphics.Point
import android.graphics.Rect
import android.net.Uri
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.furianrt.mydiary.R
import com.furianrt.mydiary.model.entity.MyImage
import com.furianrt.mydiary.presentation.general.GlideApp
import com.furianrt.mydiary.presentation.general.ItemTouchHelperCallback
import com.furianrt.mydiary.presentation.screens.gallery.fragments.list.adapter.entity.BaseImageListItem
import com.furianrt.mydiary.presentation.screens.gallery.fragments.list.adapter.entity.ImageListFooter
import com.furianrt.mydiary.presentation.screens.gallery.fragments.list.adapter.entity.ImageListItem
import kotlinx.android.synthetic.main.fragment_gallery_list_item.view.*

@SuppressLint("ClickableViewAccessibility")
class GalleryListAdapter(
        var listener: OnListItemInteractionListener,
        recyclerView: RecyclerView,
        var trashPoint: Point = Point(),
        var selectedImageNames: HashSet<String> = HashSet()
) : RecyclerView.Adapter<GalleryListAdapter.ViewHolder>() {

    companion object {
        const val TYPE_IMAGE = 0
        const val TYPE_FOOTER = 1
    }

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

    private val mList = mutableListOf<BaseImageListItem>()

    fun getImages(): List<MyImage> = mList
            .filterIsInstance(ImageListItem::class.java)
            .map { it.image }

    fun submitList(list: List<MyImage>) {
        val newViewItems = list
                .map { ImageListItem(it) }
                .toMutableList<BaseImageListItem>()
                .apply { add(ImageListFooter()) }
        val diffResult = DiffUtil.calculateDiff(GalleryListDiffCallback(mList, newViewItems))
        mList.clear()
        mList.addAll(newViewItems)
        diffResult.dispatchUpdatesTo(this)
    }

    fun deactivateSelection() {
        mList.forEachIndexed { index, item ->
            if (item is ImageListItem && selectedImageNames.contains(item.image.name)) {
                notifyItemChanged(index)
            }
        }
        selectedImageNames.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = when (viewType) {
        TYPE_IMAGE -> ViewHolderImage(LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_gallery_list_item, parent, false))
        TYPE_FOOTER -> ViewHolderFooter(LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_gallery_list_footer, parent, false))
        else -> throw IllegalStateException("Unsupported item type")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size

    override fun getItemViewType(position: Int): Int = when (mList[position]) {
        is ImageListItem -> TYPE_IMAGE
        is ImageListFooter -> TYPE_FOOTER
        else -> throw IllegalStateException("Unsupported item type")
    }

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

    abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: BaseImageListItem)
        abstract fun onItemClear()
        abstract fun onItemSelected()
        abstract fun onItemReleased()
    }

    class ViewHolderFooter(view: View) : ViewHolder(view) {
        override fun bind(item: BaseImageListItem) {
            itemView.visibility = View.VISIBLE
        }

        override fun onItemClear() {}
        override fun onItemSelected() {}
        override fun onItemReleased() {}
    }

    inner class ViewHolderImage(view: View) : ViewHolder(view) {

        private var mImage: MyImage? = null
        private var mIsTrashed = false

        override fun bind(item: BaseImageListItem) {
            with((item as ImageListItem).image) {
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
            mList.filterIsInstance(ImageListItem::class.java).forEachIndexed { index, item ->
                item.image.order = index
            }
            listener.onListItemDropped(itemView)
            listener.onImagesOrderChange(mList.filterIsInstance(ImageListItem::class.java).map { it.image })
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