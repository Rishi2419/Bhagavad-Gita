package com.example.bhagwatgita.datasources.model

data class VerseItem(
    val chapter_number: Int,
    val commentaries: List<Commentary>,
    val id: Int,
    val slug: String,
    val text: String,
    val translations: List<Translation>,
    val transliteration: String,
    val verse_number: Int,
    val word_meanings: String,

)