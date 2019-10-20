/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.gallery.fragments.pager

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.signature.ObjectKey
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.entity.MyImage
import com.furianrt.mydiary.view.general.GlideApp
import kotlinx.android.synthetic.main.gallery_pager_item.view.*

class GalleryPagerAdapter(
        private val images: ArrayList<MyImage> = ArrayList()
) : RecyclerView.Adapter<GalleryPagerAdapter.GalleryPagerViewHolder>() {

    fun submitImages(images: List<MyImage>) {
        val diffCallback = GalleryPagerDiffCallback(this.images, images)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.images.clear()
        this.images.addAll(images)
        diffResult.dispatchUpdatesTo(this)
    }

    fun getItem(position: Int): MyImage = images[position]

    override fun getItemCount(): Int = images.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryPagerViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.gallery_pager_item, parent, false)
        return GalleryPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryPagerViewHolder, position: Int) {
        holder.bind(images[position])
    }

    class GalleryPagerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(image: MyImage) {
            GlideApp.with(itemView)
                    .load(Uri.parse(image.path))
                    .signature(ObjectKey(image.editedTime))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(itemView.image_gallery)
        }
    }
}