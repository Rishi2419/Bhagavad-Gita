package com.example.bhagwatgita.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Dao
import com.example.bhagwatgita.datasources.SharedPreferencesManager
import com.example.bhagwatgita.datasources.api.ApiUtilities
import com.example.bhagwatgita.datasources.model.ChaptersItem
import com.example.bhagwatgita.datasources.model.VerseItem
import com.example.bhagwatgita.datasources.room.ChaptersInEnglish
import com.example.bhagwatgita.datasources.room.EnglishChapterDao
import com.example.bhagwatgita.datasources.room.EnglishVerseDao
import com.example.bhagwatgita.datasources.room.VersesInEnglish
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppRepository(private val englishChapterDao: EnglishChapterDao, val englishVerseDao: EnglishVerseDao , val sharedPreferencesManager: SharedPreferencesManager) {


    fun getChapter(): Flow<List<ChaptersItem>> = callbackFlow {
        val callback = object : Callback<List<ChaptersItem>> {
            override fun onResponse(call: Call<List<ChaptersItem>>, response: Response<List<ChaptersItem>>) {
                if(response.isSuccessful && response.body() != null){
                    trySend(response.body()!!)
                    close()
                }
                else{
                }
            }

            override fun onFailure(call: Call<List<ChaptersItem>>, t: Throwable) {
                close(t)
            }
        }
        ApiUtilities.api.getAllChapters().enqueue(callback)
        awaitClose { }
    }


    fun getVerseOfAChapter(chapterNumber : Int) : Flow<List<VerseItem>> = callbackFlow {
        val callBack = object : Callback<List<VerseItem>>{
            override fun onResponse(
                call: Call<List<VerseItem>>,
                response: Response<List<VerseItem>>
            ) {
                if (response.isSuccessful && response.body() != null){
                    trySend(response.body()!!)
                }
                else{
                }
            }

            override fun onFailure(call: Call<List<VerseItem>>, t: Throwable) {
                close(t)
            }
        }

        ApiUtilities.api.getVerses(chapterNumber).enqueue(callBack)
        awaitClose{}
    }

    fun getParticularVerse(chapterNumber: Int , verseNumber: Int) : Flow<VerseItem> = callbackFlow {
        val callBack = object  : Callback<VerseItem>{
            override fun onResponse(call: Call<VerseItem>, response: Response<VerseItem>) {
                if(response.isSuccessful && response.body() != null){
                    trySend(response.body()!!)
                }
                else{
                    Log.d("TAG" , "Not fetched")
                }
            }

            override fun onFailure(call: Call<VerseItem>, t: Throwable) {
                Log.d("TAG" , t.toString())
                close(t)
            }
        }

        ApiUtilities.api.getParticularVerse(chapterNumber,verseNumber).enqueue(callBack)
        awaitClose {  }
    }

    // chapter saving
    suspend fun insertEnglishChapter(englishChapters: ChaptersInEnglish)  = englishChapterDao.insertEnglishChapter(englishChapters)

    fun getTheSavedChapters() : LiveData<List<ChaptersInEnglish>> = englishChapterDao.getAllEnglishChapters()

    fun getOneEnglishChapter(chapter_number : Int) :  LiveData<ChaptersInEnglish> = englishChapterDao.getOneEnglishChapter(chapter_number)

    suspend fun deleteOneEnglishChapter(id : Int) = englishChapterDao.deleteOneEnglishChapter(id)


    // verse saving
    suspend fun insertEnglishVerse(versesInEnglish: VersesInEnglish)  = englishVerseDao.insertEnglishVerse(versesInEnglish)

    fun getAllEnglishVerse() : LiveData<List<VersesInEnglish>> = englishVerseDao.getAllEnglishVerse()

    fun getAParticularVerse(chapterNumber : Int , verseNumber: Int) : LiveData<VersesInEnglish> = englishVerseDao.getParticularVerse(chapterNumber,verseNumber)

    suspend fun deleteAParticularVerse(chapterNumber : Int , verseNumber: Int) = englishVerseDao.deleteAParticularVerse(chapterNumber,verseNumber)


    fun getAllKeys(): Set<String>  = sharedPreferencesManager.getAllKeys()

    fun putChaptersItemId(key: String, value: Int)  = sharedPreferencesManager.putChaptersItemId(key,value)

    fun deleteKeyFromSp(key: String) = sharedPreferencesManager.deleteKey(key)

    //
    fun putSavedVersesNumber(key: String , value: String) = sharedPreferencesManager.putSavedVersesNumber(key,value)

    fun getAllSavedVersesNumber() : Set<String> =  sharedPreferencesManager.getAllSavedVersesNumber()

    fun deleteVerseNumber(key: String) = sharedPreferencesManager.deleteVerseNumber(key)

    //
    fun putVerseOfTheDay(key: String , value: String) = sharedPreferencesManager.putVerseOfTheDay(key,value)
    fun getVerseOfTheDay() : Map<String,*> = sharedPreferencesManager.getVerseOfTheDay()
    fun deleteVerseOfTheDay(key: String) = sharedPreferencesManager.deleteVerseOfTheDay(key)

}