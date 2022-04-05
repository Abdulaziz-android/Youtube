package com.abdulaziz.youtube.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "https://www.googleapis.com/youtube/v3/"
    private fun getRetrofit(base_url:String): Retrofit{
        return Retrofit.Builder()
            .baseUrl(base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }

    val apiService = getRetrofit(BASE_URL).create(ApiService::class.java)


}