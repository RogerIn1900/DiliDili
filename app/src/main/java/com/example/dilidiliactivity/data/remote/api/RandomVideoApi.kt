package com.example.dilidiliactivity.data.remote.api

import com.example.dilidiliactivity.data.local.RandomVideoData.DynamicRegionResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomVideoApi {
    @GET("x/web-interface/dynamic/region")
    suspend fun getDynamicRegion(
        @Query("ps") ps: Int,
        @Query("rid") rid: Int
    ): DynamicRegionResponse
}