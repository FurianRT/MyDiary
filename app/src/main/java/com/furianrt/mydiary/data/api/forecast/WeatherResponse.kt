package com.furianrt.mydiary.data.api.forecast

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Forecast(
        @SerializedName("weather") val weather: List<Weather>,
        @SerializedName("main") val main: Main,
        @SerializedName("wind") val wind: Wind,
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String,
        @SerializedName("cod") val cod: Int
) : Parcelable

@Parcelize
data class Main(
        @SerializedName("temp") val temp: Double,
        @SerializedName("humidity") val humidity: Int,
        @SerializedName("temp_min") val tempMin: Double,
        @SerializedName("temp_max") val tempMax: Double
) : Parcelable

@Parcelize
data class Weather(
        @SerializedName("id") val id: Int,
        @SerializedName("description") val description: String,
        @SerializedName("icon") val icon: String
) : Parcelable

@Parcelize
data class Wind(
        @SerializedName("speed") val speed: Double
) : Parcelable