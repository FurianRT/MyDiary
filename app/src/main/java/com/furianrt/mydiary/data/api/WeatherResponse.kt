package com.furianrt.mydiary.data.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Forecast(
        @SerializedName("weather") val weather: ArrayList<Weather>,
        @SerializedName("main") val main: Main,
        @SerializedName("wind") val wind: Wind,
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String,
        @SerializedName("cod") val cod: Int
) : Serializable

data class Main(
        @SerializedName("temp") val temp: Double,
        @SerializedName("humidity") val humidity: Int,
        @SerializedName("temp_min") val tempMin: Double,
        @SerializedName("temp_max") val tempMax: Double
) : Serializable

data class Weather(
        @SerializedName("id") val id: Int,
        @SerializedName("description") val description: String,
        @SerializedName("icon") val icon: String
) : Serializable

data class Wind(
        @SerializedName("speed") val speed: Double
) : Serializable