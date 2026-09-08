package com.example.dilidiliactivity.data

import com.example.dilidiliactivity.data.local.archive.ArchiveDao
import com.example.dilidiliactivity.data.remote.api.BilibiliApi
import com.example.dilidiliactivity.domain.repository.VideoRepository
import io.mockk.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class VideoApiContractTest {
    private val server = MockWebServer()
    private lateinit var api: BilibiliApi
    @Before fun setUp() {
        server.start()
        api = Retrofit.Builder().baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create()).build().create(BilibiliApi::class.java)
    }
    @After fun tearDown() = server.shutdown()
    @Test fun `region request encodes existing query contract and parses empty snapshot`() = runBlocking {
        server.enqueue(MockResponse().setBody("""{"code":0,"message":"0","ttl":1,"data":{"page":{"num":1,"size":10,"count":0},"archives":[]}}"""))
        assertTrue(api.getDynamicRegion(10, 1).data.archives.isEmpty())
        val request = server.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/x/web-interface/dynamic/region?ps=10&rid=1", request.path)
    }
    @Test fun `business rejection never replaces cached feed`() = runBlocking {
        server.enqueue(MockResponse().setBody("""{"code":-1,"message":"denied","ttl":1,"data":{"archives":[]}}"""))
        val dao = mockk<ArchiveDao>(relaxed = true)
        val error = runCatching { VideoRepository(dao, api).getVideoList(10, 1) }.exceptionOrNull()
        assertTrue(error is java.io.IOException)
        coVerify(exactly = 0) { dao.replaceRegion(any(), any()) }
    }
    @Test fun `http failure never erases cached data`() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(503))
        val dao = mockk<ArchiveDao>(relaxed = true)
        assertTrue(runCatching { VideoRepository(dao, api).getVideoList(10, 1) }.isFailure)
        coVerify(exactly = 0) { dao.replaceRegion(any(), any()) }
    }
    @Test fun `media cancellation is propagated instead of reported as missing URL`() = runBlocking {
        val cancelledApi = mockk<BilibiliApi>()
        coEvery { cancelledApi.getPlayUrl(any(), any(), any(), any()) } throws CancellationException()
        val error = runCatching { VideoRepository(mockk(), cancelledApi).getPlayUrl("1", "id") }.exceptionOrNull()
        assertTrue(error is CancellationException)
    }
}
