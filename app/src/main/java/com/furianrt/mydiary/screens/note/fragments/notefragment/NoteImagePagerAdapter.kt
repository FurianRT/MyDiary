package com.furianrt.mydiary.screens.note.fragments.notefragment

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.signature.ObjectKey
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.general.GlideApp
import kotlinx.android.synthetic.main.note_image_pager_item.view.*

class NoteImagePagerAdapter(
        private var images: ArrayList<MyImage> = ArrayList(),
        var listener: OnNoteImagePagerInteractionListener? = null
) : RecyclerView.Adapter<NoteImagePagerAdapter.NoteImageViewHolder>() {

    fun submitImages(images: List<MyImage>) {
        val diffCallback = NoteImagePagerDiffCallback(this.images, images)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.images.clear()
        this.images.addAll(images)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = images.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteImageViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.note_image_pager_item, parent, false)
        return NoteImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    inner class NoteImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(image: MyImage) {
            itemView.setOnClickListener { listener?.onImageClick(image) }
            GlideApp.with(itemView)
                    .load(Uri.parse(image.uri))
                    .signature(ObjectKey(image.editedTime))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(itemView.image_note)
        }
    }

    interface OnNoteImagePagerInteractionListener {
        fun onImageClick(image: MyImage)
    }
}