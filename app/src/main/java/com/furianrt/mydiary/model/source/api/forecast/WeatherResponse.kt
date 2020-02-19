/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.source.api.forecast

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ForecastResponse(
        @SerializedName("weather") val weather: List<WeatherResponse>? = null,
        @SerializedName("main") val main: MainResponse? = null,
        @SerializedName("wind") val wind: WindResponse? = null,
        @SerializedName("id") val id: Int? = null,
        @SerializedName("name") val name: String? = null,
        @SerializedName("cod") val cod: Int? = null
) : Parcelable

@Parcelize
data class MainResponse(
        @SerializedName("temp") val temp: Double? = null,
        @SerializedName("humidity") val humidity: Int? = null,
        @SerializedName("temp_min") val tempMin: Double? = null,
        @SerializedName("temp_max") val tempMax: Double? = null
) : Parcelable

@Parcelize
data class WeatherResponse(
        @SerializedName("id") val id: Int? = null,
        @SerializedName("description") val description: String? = null,
        @SerializedName("icon") val icon: String? = null
) : Parcelable

@Parcelize
data class WindResponse(
        @SerializedName("speed") val speed: Double? = null
) : Parcelable