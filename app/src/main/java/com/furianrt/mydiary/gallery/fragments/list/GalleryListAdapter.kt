package com.furianrt.mydiary.gallery.fragments.list

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.general.GlideApp
import com.furianrt.mydiary.utils.animateScale
import kotlinx.android.synthetic.main.fragment_gallery_list_item.view.*

class GalleryListAdapter(
        var listener: OnListItemClickListener,
        recyclerView: RecyclerView,
        var selectedImages: MutableList<MyImage> = ArrayList()
) : RecyclerView.Adapter<GalleryListAdapter.GalleryListViewHolder>() {

    companion object {
        private const val ITEM_DRAGGING_SCALE = 1.04f
        private const val ITEM_DEFAULT_SCALE = 1f
        private const val ITEM_SCALE_DURATION = 250L
    }

    private val mItemTouchHelper: ItemTouchHelper

    init {
        val callback = ItemTouchHelperCallback(this)
        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private var mList: MutableList<MyImage> = mutableListOf()

    fun getImages(): List<MyImage> = mList

    fun submitList(list: List<MyImage>) {
        val diffCallback = GalleryListDiffCallback(mList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        mList.clear()
        mList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    fun deactivateSelection() {
        for (i in 0 until mList.size) {
            if (selectedImages.contains(mList[i])) {
                notifyItemChanged(i)
            }
        }
        selectedImages.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryListViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_gallery_list_item, parent, false)
        return GalleryListViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryListViewHolder, position: Int) {
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

    inner class GalleryListViewHolder(
            view: View
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var mImage: MyImage

        fun bind(item: MyImage) {
             mImage = item
            itemView.setOnClickListener(this)
            GlideApp.with(itemView)
                    .load(Uri.parse(mImage.uri))
                    .override(550, 400)
                    .into(itemView.image_gallery_item)

            selectItem()
        }

        override fun onClick(v: View?) {
            listener.onListItemClick(mImage, adapterPosition)
        }

        private fun selectItem() {
            if (selectedImages.contains(mImage)) {
                itemView.layout_selection.visibility = View.VISIBLE
            } else {
                itemView.layout_selection.visibility = View.INVISIBLE
            }
        }

        fun onItemClear() {
            itemView.animateScale(ITEM_DRAGGING_SCALE, ITEM_DEFAULT_SCALE, ITEM_SCALE_DURATION)
            for (i in 0 until mList.size) {
                mList[i].order = i
            }
            listener.onImagesOrderChange(mList)
        }

        fun onItemSelected() {
            itemView.animateScale(ITEM_DEFAULT_SCALE, ITEM_DRAGGING_SCALE, ITEM_SCALE_DURATION)
        }
    }

    interface OnListItemClickListener {

        fun onListItemClick(image: MyImage, position: Int)

        fun onImagesOrderChange(images: List<MyImage>)
    }
}