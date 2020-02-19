/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.source.api.images

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImagesResponse(
        @SerializedName("hits") val images: List<ImageResponse>? = null,
        @SerializedName("total") val total: Int? = null,
        @SerializedName("totalHits") val totalImages: Int? = null
) : Parcelable

@Parcelize
data class ImageResponse(
        @SerializedName("comments") val comments: Int? = null,
        @SerializedName("downloads") val downloads: Int? = null,
        @SerializedName("favorites") val favorites: Int? = null,
        @SerializedName("id") val id: Int? = null,
        @SerializedName("imageHeight") val imageHeight: Int? = null,
        @SerializedName("imageSize") val imageSize: Int? = null,
        @SerializedName("imageWidth") val imageWidth: Int? = null,
        @SerializedName("largeImageURL") val largeImageURL: String? = null,
        @SerializedName("likes") val likes: Int? = null,
        @SerializedName("pageURL") val pageURL: String? = null,
        @SerializedName("previewHeight") val previewHeight: Int? = null,
        @SerializedName("previewURL") val previewURL: String? = null,
        @SerializedName("previewWidth") val previewWidth: Int? = null,
        @SerializedName("tags") val tags: String? = null,
        @SerializedName("type") val type: String? = null,
        @SerializedName("user") val user: String? = null,
        @SerializedName("userImageURL") val userImageURL: String? = null,
        @SerializedName("user_id") val userId: Int? = null,
        @SerializedName("views") val views: Int? = null,
        @SerializedName("webformatHeight") val webformatHeight: Int? = null,
        @SerializedName("webformatURL") val webformatURL: String? = null,
        @SerializedName("webformatWidth") val webformatWidth: Int? = null
) : Parcelable