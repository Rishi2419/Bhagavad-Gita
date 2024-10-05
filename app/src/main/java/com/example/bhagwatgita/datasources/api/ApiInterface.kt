package com.example.bhagwatgita.datasources.api

import com.example.bhagwatgita.datasources.model.ChaptersItem
import com.example.bhagwatgita.datasources.model.VerseItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface ApiInterface {


    @GET("/v2/chapters/?limit=18")
    fun getAllChapters(): Call<List<ChaptersItem>>

    @GET("/v2/chapters/{chapterNumber}/verses/")
    fun getVerses(@Path("chapterNumber") chapterNumber: Int): Call<List<VerseItem>>

    @GET("/v2/chapters/{chapterNumber}/verses/{verseNumber}/")
    fun getParticularVerse(
        @Path("chapterNumber") chapterNumber: Int,
        @Path("verseNumber") verseNumber: Int
    ): Call<VerseItem>
}
