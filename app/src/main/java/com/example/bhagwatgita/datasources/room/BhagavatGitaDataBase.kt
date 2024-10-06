package com.example.bhagwatgita.datasources.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ChaptersInEnglish::class , VersesInEnglish::class] , version = 2, exportSchema = false)
@TypeConverters(VerseItemConverter::class)
abstract class BhagavatGitaDataBase : RoomDatabase(){

    abstract fun englishChapterDao() : EnglishChapterDao
    abstract fun englishVersesDao() : EnglishVerseDao


    companion object{

        @Volatile
        var INSTANCE  : BhagavatGitaDataBase? = null

        fun getDatabaseInstance(context: Context) : BhagavatGitaDataBase? {
            val tempInstance = INSTANCE
            if(INSTANCE != null) return tempInstance
            synchronized(this){
                val roomdb = Room.databaseBuilder(context, BhagavatGitaDataBase::class.java, "BhagavatGitaDataBase").fallbackToDestructiveMigration().build()
                INSTANCE = roomdb
                return roomdb
            }
        }
    }



}