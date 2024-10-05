package com.example.bhagwatgita.datasources.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
/*
while changin the schema of a datatbase we have update the version number but not only
this we need to also provide the migration path so that we can get all the previous stored data

or there is a method we can put before build while building the data base fallbackToDestructiveMigration() , it deletes all the previous data

learn about proper migration.
 */
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