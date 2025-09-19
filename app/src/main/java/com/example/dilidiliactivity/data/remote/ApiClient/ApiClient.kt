package com.example.dilidiliactivity.data.remote.ApiClient

import com.example.dilidiliactivity.data.remote.api.BilibiliApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://api.bilibili.com/"

    val api: BilibiliApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BilibiliApi::class.java)
    }
}
