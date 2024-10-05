package com.example.bhagwatgita.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.bhagwatgita.datasources.SharedPreferencesManager
import com.example.bhagwatgita.datasources.model.ChaptersItem
import com.example.bhagwatgita.datasources.model.VerseItem
import com.example.bhagwatgita.repository.AppRepository
import com.example.bhagwatgita.datasources.room.BhagavatGitaDataBase
import com.example.bhagwatgita.datasources.room.ChaptersInEnglish
import com.example.bhagwatgita.datasources.room.VersesInEnglish
import kotlinx.coroutines.flow.Flow

class MainViewModel(application: Application) : AndroidViewModel(application) {


    private val englishChapterDao = BhagavatGitaDataBase.getDatabaseInstance(application)?.englishChapterDao()
    private val englishVerseDao = BhagavatGitaDataBase.getDatabaseInstance(application)?.englishVersesDao()
    private val sharedPreferencesManager = SharedPreferencesManager(application)
    private val appRepository: AppRepository =AppRepository(englishChapterDao!! , englishVerseDao!! , sharedPreferencesManager)



    suspend fun insertEnglishChapter(englishChapters: ChaptersInEnglish) {
        appRepository.insertEnglishChapter(englishChapters)
    }


    fun getAllChapters(): Flow<List<ChaptersItem>> = appRepository.getChapter()

    fun getVerseOfAChapter(chapterNumber: Int): Flow<List<VerseItem>> =
        appRepository.getVerseOfAChapter(chapterNumber)

    fun getParticularVerse(chapterNumber: Int, verseNumber: Int): Flow<VerseItem> = appRepository.getParticularVerse(chapterNumber, verseNumber)

    fun getTheSavedChapters() : LiveData<List<ChaptersInEnglish>> = appRepository.getTheSavedChapters()

    suspend fun getOneEnglishChapter(chapter_number : Int) :  LiveData<ChaptersInEnglish> = appRepository.getOneEnglishChapter(chapter_number)

    suspend fun insertEnglishVerse(versesInEnglish: VersesInEnglish)   = appRepository.insertEnglishVerse(versesInEnglish)

    fun getAllEnglishVerse() : LiveData<List<VersesInEnglish>> = appRepository.getAllEnglishVerse()

    fun getAParticularVerse(chapterNumber : Int , verseNumber: Int) : LiveData<VersesInEnglish> = appRepository.getAParticularVerse(chapterNumber,verseNumber)

    suspend fun deleteAParticularVerse(chapterNumber : Int , verseNumber: Int) = appRepository.deleteAParticularVerse(chapterNumber,verseNumber)


    // sp

    fun putChaptersItemId(key: String, value: Int)  = appRepository.putChaptersItemId(key,value)

    fun getAllKeys(): Set<String>  = appRepository.getAllKeys()


    suspend fun deleteOneEnglishChapter(id : Int) = appRepository.deleteOneEnglishChapter(id)

    fun deleteKeyFromSp(key: String) = appRepository.deleteKeyFromSp(key)

    //

    fun putSavedVersesNumber(key: String , value: String) = appRepository.putSavedVersesNumber(key,value)

    fun getAllSavedVersesNumber() : Set<String> =  appRepository.getAllSavedVersesNumber()

    fun deleteVerseNumber(key: String) = appRepository.deleteVerseNumber(key)


    //

    fun putVerseOfTheDay(key: String , value: String) = appRepository.putVerseOfTheDay(key,value)
    fun getVerseOfTheDay() : Map<String,*> = appRepository.getVerseOfTheDay()
    fun deleteVerseOfTheDay(key: String) = appRepository.deleteVerseOfTheDay(key)
}