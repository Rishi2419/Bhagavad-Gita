package com.example.bhagwatgita.datasources.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiUtilities {
    private const val BASE_URL = "https://bhagavad-gita3.p.rapidapi.com"

    private val headers = mapOf(
        "Accept" to "application/json",
        "x-rapidapi-key" to "YOUR API KEY",
        "x-rapidapi-host" to "bhagavad-gita3.p.rapidapi.com"
    )

    private val client = OkHttpClient.Builder().apply {
        addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .apply {
                    headers.forEach { (key, value) -> addHeader(key, value) }
                }
                .build()
            chain.proceed(request)
        }
    }.build()



    val api: ApiInterface by lazy { 
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }
    

}
