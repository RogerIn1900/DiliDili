package com.example.dilidiliactivity.data.remote.ApiClient

import com.example.dilidiliactivity.data.remote.api.PopularVideoApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

object PopularVideoApiClient {
    private const val BASE_URL = "https://api.bilibili.com/"
    val api: PopularVideoApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PopularVideoApi::class.java)
    }
}