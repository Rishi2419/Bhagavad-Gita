package com.example.bhagwatgita.datasources

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("stored_chapters", Context.MODE_PRIVATE)
    }
    private val sharedPreferencesVerses: SharedPreferences by lazy {
        context.getSharedPreferences("stored_verses", Context.MODE_PRIVATE)
    }

    private val sharedPreferencesVerseOfTheDay: SharedPreferences by lazy {
        context.getSharedPreferences("stored_verse_of_the_day", Context.MODE_PRIVATE)
    }

    fun getAllKeys(): Set<String> {
        return sharedPreferences.all.keys
    }

    fun putChaptersItemId(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun deleteKey( key: String) {
        val editor = sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
    }

    //
    fun putSavedVersesNumber(key: String , value: String){
        sharedPreferencesVerses.edit().putString(key,value).apply()
    }

    fun getAllSavedVersesNumber() : Set<String>{
        return sharedPreferencesVerses.all.keys
    }

    fun deleteVerseNumber(key: String){
        sharedPreferencesVerses.edit().remove(key).apply()
    }

    //
    fun putVerseOfTheDay(key: String , value: String){
        sharedPreferencesVerseOfTheDay.edit().putString(key ,value ).apply()
    }

    fun getVerseOfTheDay() : Map<String,*>{
        return sharedPreferencesVerseOfTheDay.all
    }

    fun deleteVerseOfTheDay(key: String){
        sharedPreferencesVerseOfTheDay.edit().clear().apply()
    }

}