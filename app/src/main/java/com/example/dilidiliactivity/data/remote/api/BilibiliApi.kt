package com.example.dilidiliactivity.data.remote.api

import com.example.dilidiliactivity.data.local.PopularPreciousResponse.PopularPreciousResponse
import com.example.dilidiliactivity.data.local.RandomVideoData.DynamicRegionResponse
import com.example.dilidiliactivity.data.local.RelatedVideos.RelatedVideosResponse
import com.example.dilidiliactivity.data.local.VideoPlayerData.FansResponse
import com.example.dilidiliactivity.data.local.VideoPlayerData.PlayUrlResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

interface BilibiliApi {
    @GET("x/web-interface/dynamic/region")
    suspend fun getDynamicRegion(
        @Query("ps") ps: Int,
        @Query("rid") rid: Int
    ): DynamicRegionResponse

    @GET("x/player/playurl")
    suspend fun getPlayUrl(
        @Query("cid") cid: String,
        @Query("bvid") bvid: String,
        @Query("qn") qn: Int = 80,
        @Query("otype") otype: String = "json"
    ): Response<PlayUrlResponse>

    @GET("x/web-interface/popular/precious")
    suspend fun getPopularPrecious(): PopularPreciousResponse

    @GET("x/relation/stat")
    suspend fun getFollowers(
        @Query("vmid") mid: Long
    ): FansResponse

    @GET("/x/web-interface/archive/related")
    suspend fun getRelatedVideo(
        @Query("aid") aid: String,
        @Query("bvid") bvid: String
    ): RelatedVideosResponse
}
