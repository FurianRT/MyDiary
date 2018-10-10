package com.furianrt.mydiary.gallery.fragments.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.general.GlideApp
import com.makeramen.dragsortadapter.DragSortAdapter
import kotlinx.android.synthetic.main.fragment_gallery_list_item.view.*

class GalleryListAdapter(
        private var mItems: MutableList<GalleryListItem>,
        var listener: OnListItemClickListener,
        recyclerView: androidx.recyclerview.widget.RecyclerView,
        var selectedImages: MutableList<MyImage> = ArrayList()
) : DragSortAdapter<GalleryListAdapter.GalleryListViewHolder>(recyclerView) {

    fun getImages(): List<MyImage> = mItems.asSequence()
            .map { it.image }
            .toList()

    fun submitList(list: List<MyImage>) {
        if (mItems.isEmpty()) {
            mItems = list.asSequence()
                    .map { GalleryListAdapter.GalleryListItem(it.order.toLong(), it) }
                    .toMutableList()
            notifyItemRangeInserted(0, list.size)
        } else {
            for (i in mItems.size - 1 downTo 0) {
                if (list.find { it.name == mItems[i].image.name } == null) {
                    mItems.removeAt(i)
                    notifyItemRemoved(i)
                }
            }
        }
    }

    fun deactivateSelection() {
        for (i in 0 until mItems.size) {
            if (selectedImages.contains(mItems[i].image)) {
                notifyItemChanged(i)
            }
        }
        selectedImages.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.fragment_gallery_list_item, parent, false)
        return GalleryListViewHolder(this, view)
    }

    override fun onBindViewHolder(holder: GalleryListViewHolder, position: Int) {
        holder.bind(mItems[position])
    }

    override fun getPositionForId(id: Long) = mItems.indexOf(mItems.find { it.id == id })

    override fun getItemCount() = mItems.size

    override fun getItemId(position: Int) = mItems[position].id

    override fun move(fromPosition: Int, toPosition: Int): Boolean {
        mItems.add(toPosition, mItems.removeAt(fromPosition))
        for (i in 0 until mItems.size) {
            mItems[i].image.order = i
        }
        return true
    }

    inner class GalleryListViewHolder(
            adapter: DragSortAdapter<GalleryListAdapter.GalleryListViewHolder>,
            private val mView: View
    ) : ViewHolder(adapter, mView), View.OnLongClickListener, View.OnClickListener {

        private lateinit var mImage: MyImage

        fun bind(item: GalleryListItem) {
            mImage = item.image
            mView.apply {
                setOnLongClickListener(this@GalleryListViewHolder)
                setOnClickListener(this@GalleryListViewHolder)

                GlideApp.with(mView)
                        .load(mImage.url)
                        .override(550, 400)
                        .into(image_gallery_item)
            }

            selectItem()
        }

        override fun onLongClick(v: View?): Boolean {
            startDrag()
            return true
        }

        override fun onClick(v: View?) {
            listener.onListItemClick(mImage, mImage.order)
        }

        private fun selectItem() {
            if (selectedImages.contains(mImage)) {
                mView.layout_selection.visibility = View.VISIBLE
            } else {
                mView.layout_selection.visibility = View.INVISIBLE
            }
        }
    }

    interface OnListItemClickListener {

        fun onListItemClick(image: MyImage, position: Int)
    }

    class GalleryListItem(val id: Long, val image: MyImage)
}