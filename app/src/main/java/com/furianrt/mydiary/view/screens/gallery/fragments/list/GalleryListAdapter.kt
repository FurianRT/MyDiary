package com.furianrt.mydiary.view.screens.gallery.fragments.list

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
import com.bumptech.glide.signature.ObjectKey
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.view.general.GlideApp
import com.furianrt.mydiary.view.general.ItemTouchHelperCallback
import kotlinx.android.synthetic.main.fragment_gallery_list_item.view.*

@SuppressLint("ClickableViewAccessibility")
class GalleryListAdapter(
        var listener: OnListItemInteractionListener,
        recyclerView: RecyclerView,
        var trashPoint: Point = Point(),
        var selectedImageNames: HashSet<String> = HashSet()
) : RecyclerView.Adapter<GalleryListAdapter.ViewHolder>() {

    data class ViewItem(
            val type: Int,
            val image: MyImage? = null
    ) {
        companion object {
            const val TYPE_IMAGE = 0
            const val TYPE_FOOTER = 1
        }
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

    private val mList: MutableList<ViewItem> = mutableListOf()

    fun getImages(): List<MyImage> = mList
            .filter { it.type == ViewItem.TYPE_IMAGE }
            .map { it.image!! }

    fun submitList(list: List<MyImage>) {
        val newViewItems = list
                .map { ViewItem(ViewItem.TYPE_IMAGE, it) }
                .toMutableList()
                .apply { add(ViewItem(ViewItem.TYPE_FOOTER)) }
        val diffCallback = GalleryListDiffCallback(mList, newViewItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        mList.clear()
        mList.addAll(newViewItems)
        diffResult.dispatchUpdatesTo(this)
    }

    fun deactivateSelection() {
        mList.map { it.image?.name }.forEach { imageName ->
            if (imageName != null && selectedImageNames.contains(imageName)) {
                notifyItemChanged(mList.indexOfFirst { it.image?.name == imageName })
            }
        }
        selectedImageNames.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            when (viewType) {
                ViewItem.TYPE_IMAGE -> ViewHolderImage(LayoutInflater.from(parent.context)
                        .inflate(R.layout.fragment_gallery_list_item, parent, false))
                else -> ViewHolderFooter(LayoutInflater.from(parent.context)
                        .inflate(R.layout.fragment_gallery_list_footer, parent, false))
            }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size

    override fun getItemViewType(position: Int): Int = mList[position].type

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
        abstract fun bind(item: ViewItem)
        abstract fun onItemClear()
        abstract fun onItemSelected()
        abstract fun onItemReleased()
    }

    inner class ViewHolderFooter(view: View) : ViewHolder(view) {
        override fun bind(item: ViewItem) {
            itemView.visibility = View.VISIBLE
        }

        override fun onItemClear() {}
        override fun onItemSelected() {}
        override fun onItemReleased() {}
    }

    inner class ViewHolderImage(view: View) : ViewHolder(view) {

        private lateinit var mImage: MyImage
        private var mIsTrashed = false

        override fun bind(item: ViewItem) {
            mImage = item.image!!
            itemView.alpha = 1f
            itemView.setOnClickListener { listener.onListItemClick(mImage, adapterPosition) }
            itemView.setOnLongClickListener {
                mItemTouchHelper.startDrag(this)
                return@setOnLongClickListener true
            }
            GlideApp.with(itemView)
                    .load(Uri.parse(mImage.uri))
                    .override(550, 400)
                    .signature(ObjectKey(mImage.editedTime))
                    .into(itemView.image_gallery_item)

            if (mIsTrashed) {
                itemView.visibility = View.GONE
            } else {
                itemView.visibility = View.VISIBLE
                if (selectedImageNames.contains(mImage.name)) {
                    itemView.layout_selection.visibility = View.VISIBLE
                } else {
                    itemView.layout_selection.visibility = View.INVISIBLE
                }
            }
        }

        override fun onItemClear() {
            for (i in 0 until mList.size) {
                mList[i].image?.order = i
            }
            listener.onListItemDropped(itemView)
            listener.onImagesOrderChange(mList.filter { it.type == ViewItem.TYPE_IMAGE }.map { it.image!! })
            mIsTrashed = false
        }

        override fun onItemSelected() {
            mDraggedItem = itemView
            listener.onListItemStartDrag(itemView)
        }

        override fun onItemReleased() {
            if (mIsOverTrash) {
                mIsTrashed = true
                mDraggedItem?.visibility = View.GONE
                listener.onItemTrashed(mImage)
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