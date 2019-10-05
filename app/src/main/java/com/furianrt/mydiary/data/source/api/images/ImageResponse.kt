/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.data.source.api.images

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageResponse(
        @SerializedName("hits") val images: List<Image>,
        @SerializedName("total") val total: Int,
        @SerializedName("totalHits") val totalImages: Int
) : Parcelable

@Parcelize
data class Image(
        @SerializedName("comments") val comments: Int,
        @SerializedName("downloads") val downloads: Int,
        @SerializedName("favorites") val favorites: Int,
        @SerializedName("id") val id: Int,
        @SerializedName("imageHeight") val imageHeight: Int,
        @SerializedName("imageSize") val imageSize: Int,
        @SerializedName("imageWidth") val imageWidth: Int,
        @SerializedName("largeImageURL") val largeImageURL: String,
        @SerializedName("likes") val likes: Int,
        @SerializedName("pageURL") val pageURL: String,
        @SerializedName("previewHeight") val previewHeight: Int,
        @SerializedName("previewURL") val previewURL: String,
        @SerializedName("previewWidth") val previewWidth: Int,
        @SerializedName("tags") val tags: String,
        @SerializedName("type") val type: String,
        @SerializedName("user") val user: String,
        @SerializedName("userImageURL") val userImageURL: String,
        @SerializedName("user_id") val userId: Int,
        @SerializedName("views") val views: Int,
        @SerializedName("webformatHeight") val webformatHeight: Int,
        @SerializedName("webformatURL") val webformatURL: String,
        @SerializedName("webformatWidth") val webformatWidth: Int
) : Parcelable