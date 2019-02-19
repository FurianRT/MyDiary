package com.furianrt.mydiary.data.room
import androidx.room.TypeConverter
import com.furianrt.mydiary.data.api.forecast.Forecast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RoomTypeConverter {

    @TypeConverter
    fun stringToForecast(value: String?): Forecast? {
        val type = object : TypeToken<Forecast>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun forecastToString(forecast: Forecast?): String? {
        return Gson().toJson(forecast)
    }
}