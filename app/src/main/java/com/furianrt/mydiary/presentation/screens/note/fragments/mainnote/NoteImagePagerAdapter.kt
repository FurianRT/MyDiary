/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.note.fragments.mainnote

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import com.furianrt.mydiary.R
import com.furianrt.mydiary.model.entity.MyImage
import com.furianrt.mydiary.presentation.general.GlideApp
import com.furianrt.mydiary.utils.animateAlpha
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
            itemView.image_note.alpha = 0f
            GlideApp.with(itemView)
                    .asBitmap()
                    .load(Uri.parse(image.path))
                    .centerCrop()
                    .signature(ObjectKey(image.editedTime.toString() + image.name))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(object : CustomViewTarget<ImageView, Bitmap>(itemView.image_note) {
                        override fun onResourceCleared(placeholder: Drawable?) = Unit
                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            listener?.onImageLoadFailed()
                        }

                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            listener?.onImageLoaded()
                            itemView.image_note.setImageBitmap(resource)
                            itemView.image_note.animateAlpha(0f, 1f)
                        }
                    })
        }
    }

    interface OnNoteImagePagerInteractionListener {
        fun onImageClick(image: MyImage)
        fun onImageLoadFailed()
        fun onImageLoaded()
    }
}