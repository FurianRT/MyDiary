package com.furianrt.mydiary.gallery.fragments.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.general.GlideApp
import com.makeramen.dragsortadapter.DragSortAdapter
import kotlinx.android.synthetic.main.fragment_gallery_list_item.view.*

class GalleryListAdapter(
        var items: ArrayList<GalleryListItem>,
        var listener: OnListItemClickListener,
        recyclerView: RecyclerView
) : DragSortAdapter<GalleryListAdapter.GalleryListViewHolder>(recyclerView) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.fragment_gallery_list_item, parent, false)
        return GalleryListViewHolder(this, view)
    }

    override fun onBindViewHolder(holder: GalleryListViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getPositionForId(id: Long) = items.indexOf(items.find { it.id == id })

    override fun getItemCount() = items.size

    override fun getItemId(position: Int) = items[position].id

    override fun move(fromPosition: Int, toPosition: Int): Boolean {
        items.add(toPosition, items.removeAt(fromPosition))
        for (i in 0 until items.size) {
            items[i].image.order = i
        }
        return true
    }

    inner class GalleryListViewHolder(
            adapter: DragSortAdapter<GalleryListAdapter.GalleryListViewHolder>,
            private val view: View
    ) : ViewHolder(adapter, view), View.OnLongClickListener, View.OnClickListener {

        private lateinit var mImage: MyImage

        fun bind(item: GalleryListItem) {
            mImage = item.image
            view.apply {
                setOnLongClickListener(this@GalleryListViewHolder)
                setOnClickListener(this@GalleryListViewHolder)

                GlideApp.with(view)
                        .load(mImage.url)
                        .override(550, 400)
                        .into(image_gallery_item)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            startDrag()
            return true
        }

        override fun onClick(v: View?) {
            listener.onListItemClick(mImage, layoutPosition)
        }
    }

    interface OnListItemClickListener {

        fun onListItemClick(image: MyImage, position: Int)
    }

    class GalleryListItem(val id: Long, val image: MyImage)
}