/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.note.fragments.mainnote

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.furianrt.mydiary.R
import com.furianrt.mydiary.model.entity.MyImage
import com.furianrt.mydiary.view.general.GlideApp
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteImageViewHolder =
            NoteImageViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.note_image_pager_item, parent, false))

    override fun onBindViewHolder(holder: NoteImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    inner class NoteImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(image: MyImage) {
            itemView.setOnClickListener { listener?.onImageClick(image) }
            GlideApp.with(itemView)
                    .load(Uri.parse(image.path))
                    .signature(ObjectKey(image.editedTime.toString() + image.name))
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(itemView.image_note)
        }
    }

    interface OnNoteImagePagerInteractionListener {
        fun onImageClick(image: MyImage)
    }
}