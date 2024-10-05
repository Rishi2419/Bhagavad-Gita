package com.example.bhagwatgita.datasources.room

import androidx.room.TypeConverter
import com.example.bhagwatgita.datasources.model.Commentary
import com.example.bhagwatgita.datasources.model.Translation

import com.example.bhagwatgita.datasources.model.VerseItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Collections

class VerseItemConverter {

    private val gson = Gson()
    @TypeConverter
    fun fromListToString(list : List<String>) : String{
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromStringToList(string : String) : List<String>{
        return Gson().fromJson<List<String>>(string, object : TypeToken<List<String>>() {}.type)
    }



    @TypeConverter
    fun translationToString(someObjects: List<Translation?>?): String? {
        return gson.toJson(someObjects)
    }
    @TypeConverter
    fun stringToTranslation(data: String?): List<Translation?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType = object :
            TypeToken<List<Translation?>?>() {}.type
        return gson.fromJson<List<Translation?>>(data, listType)
    }


    @TypeConverter
    fun commentaryToString(someObjects: List<Commentary?>?): String? {
        return gson.toJson(someObjects)
    }
    @TypeConverter
    fun stringToCommentary(data: String?): List<Commentary?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType = object :
            TypeToken<List<Commentary?>?>() {}.type
        return gson.fromJson<List<Commentary?>>(data, listType)
    }

}

