package com.example.bhagwatgita.datasources.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface EnglishChapterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnglishChapter(englishChapters: ChaptersInEnglish)

    @Query("SELECT  * FROM ChaptersInEnglish")
    fun getAllEnglishChapters() : LiveData<List<ChaptersInEnglish>>

    @Query("DELETE FROM ChaptersInEnglish WHERE id = :id")
    suspend fun deleteOneEnglishChapter(id : Int)

    @Query("SELECT * FROM ChaptersInEnglish WHERE chapter_number = :chapter_number")
     fun getOneEnglishChapter(chapter_number : Int) : LiveData<ChaptersInEnglish>
}

@Dao
interface EnglishVerseDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnglishVerse(versesInEnglish: VersesInEnglish)

    @Query("SELECT * FROM VersesInEnglish")
    fun getAllEnglishVerse() : LiveData<List<VersesInEnglish>>

    @Query("SELECT * FROM VersesInEnglish WHERE chapter_number = :chapterNumber AND verse_number = :verseNumber")
    fun getParticularVerse(chapterNumber : Int , verseNumber: Int) : LiveData<VersesInEnglish>

    @Query("DELETE  FROM VersesInEnglish WHERE chapter_number = :chapterNumber AND verse_number = :verseNumber")
    suspend fun deleteAParticularVerse(chapterNumber : Int , verseNumber: Int)

}