package com.example.dilidiliactivity.data.remote.api

import com.example.dilidiliactivity.data.local.PopularVideoData.PopularVidelData
import retrofit2.http.GET

interface PopularVideoApi {
    @GET("x/web-interface/popular/precious")
    suspend fun getPopularVideo(): PopularVidelData
}