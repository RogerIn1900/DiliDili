package com.example.dilidiliactivity.di

import android.content.Context
import com.example.dilidiliactivity.data.local.archive.AppDatabase
import com.example.dilidiliactivity.data.local.archive.ArchiveDao
import com.example.dilidiliactivity.data.remote.api.BilibiliApi
import com.example.dilidiliactivity.data.remote.api.PopularVideoApi
import com.example.dilidiliactivity.domain.repository.VideoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://api.bilibili.com/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideBilibiliApi(retrofit: Retrofit): BilibiliApi =
        retrofit.create(BilibiliApi::class.java)

    @Provides
    @Singleton
    fun providePopularVideoApi(retrofit: Retrofit): PopularVideoApi =
        retrofit.create(PopularVideoApi::class.java)

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideArchiveDao(db: AppDatabase): ArchiveDao =
        db.archiveDao()

    @Provides
    @Singleton
    fun provideVideoRepository(dao: ArchiveDao, api: BilibiliApi): VideoRepository =
        VideoRepository(dao, api)
}
