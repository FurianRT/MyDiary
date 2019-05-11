package com.furianrt.mydiary.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RoomTypeConverter {

    @TypeConverter
    fun stringToList(value: String): List<String> {
        if (value.isBlank()) {
            return emptyList()
        }
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun stringToList(list: List<String>): String {
        return Gson().toJson(list)
    }
}