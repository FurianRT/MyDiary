package com.furianrt.mydiary.data.api

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Forecast(
        @SerializedName("weather") val weather: List<Weather>,
        @SerializedName("main") val main: Main,
        @SerializedName("wind") val wind: Wind,
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String,
        @SerializedName("cod") val cod: Int
) : Parcelable {

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    constructor(parcel: Parcel) : this(
            parcel.createTypedArrayList(Weather),
            parcel.readParcelable(Main::class.java.classLoader),
            parcel.readParcelable(Wind::class.java.classLoader),
            parcel.readInt(),
            parcel.readString() ?: "",
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(weather)
        parcel.writeParcelable(main, flags)
        parcel.writeParcelable(wind, flags)
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(cod)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Forecast> {
        override fun createFromParcel(parcel: Parcel): Forecast {
            return Forecast(parcel)
        }

        override fun newArray(size: Int): Array<Forecast?> {
            return arrayOfNulls(size)
        }
    }
}

data class Main(
        @SerializedName("temp") val temp: Double,
        @SerializedName("humidity") val humidity: Int,
        @SerializedName("temp_min") val tempMin: Double,
        @SerializedName("temp_max") val tempMax: Double
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readDouble(),
            parcel.readInt(),
            parcel.readDouble(),
            parcel.readDouble())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(temp)
        parcel.writeInt(humidity)
        parcel.writeDouble(tempMin)
        parcel.writeDouble(tempMax)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Main> {
        override fun createFromParcel(parcel: Parcel): Main {
            return Main(parcel)
        }

        override fun newArray(size: Int): Array<Main?> {
            return arrayOfNulls(size)
        }
    }
}

data class Weather(
        @SerializedName("id") val id: Int,
        @SerializedName("description") val description: String,
        @SerializedName("icon") val icon: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString() ?: "",
            parcel.readString() ?: "")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(description)
        parcel.writeString(icon)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Weather> {
        override fun createFromParcel(parcel: Parcel): Weather {
            return Weather(parcel)
        }

        override fun newArray(size: Int): Array<Weather?> {
            return arrayOfNulls(size)
        }
    }
}

data class Wind(
        @SerializedName("speed") val speed: Double
) : Parcelable {

    constructor(parcel: Parcel) : this(parcel.readDouble())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(speed)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Wind> {
        override fun createFromParcel(parcel: Parcel): Wind {
            return Wind(parcel)
        }

        override fun newArray(size: Int): Array<Wind?> {
            return arrayOfNulls(size)
        }
    }
}