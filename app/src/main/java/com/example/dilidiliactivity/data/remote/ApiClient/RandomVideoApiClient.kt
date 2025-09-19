package com.example.dilidiliactivity.data.remote.ApiClient

import com.example.dilidiliactivity.data.remote.api.RandomVideoApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RandomVideoAPICLIENT {
    private const val BASE_URL = "https://api.bilibili.com/"
    val api:RandomVideoApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RandomVideoApi::class.java)
    }
}

