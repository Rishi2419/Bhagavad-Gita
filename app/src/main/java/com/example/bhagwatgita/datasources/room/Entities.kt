package com.example.bhagwatgita.datasources.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.bhagwatgita.datasources.model.Commentary
import com.example.bhagwatgita.datasources.model.Translation
import com.example.bhagwatgita.datasources.model.VerseItem


@Entity(tableName = "ChaptersInEnglish")
data class ChaptersInEnglish(
    val chapter_number : Int ? = null,
    val chapter_summary: String ? = null,
    val chapter_summary_hindi: String ? = null,
    @PrimaryKey
    val id: Int ? =  null,
    val name: String ? = null,
    val name_meaning: String ? = null,
    val name_translated: String?= null,
    val name_transliterated: String? = null,
    val slug: String ? = null,
    val verses_count: Int ? = null,
   val verses : List<String> ? = null
)


@Entity(tableName = "VersesInEnglish")
data class VersesInEnglish (
    val chapter_number: Int,
    val commentaries: List<Commentary>,
    @PrimaryKey
    val id: Int,
    val slug: String,
    val text: String,
    val translations: List<Translation>,
    val transliteration: String,
    val verse_number: Int,
    val word_meanings: String
)