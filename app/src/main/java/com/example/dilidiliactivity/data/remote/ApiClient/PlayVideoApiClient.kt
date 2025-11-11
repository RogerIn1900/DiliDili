package com.example.dilidiliactivity.data.remote.ApiClient

object PlayVideoApiClient {
    private const val BASE_URL = "https://player.bilibili.com/player.html"



	// 通过 bvid + cid 请求播放地址接口，返回可直接播放的直链（优先 durl[0].url）
	suspend fun getPlayUrl(cid: String, bvid: String, qn: Int = 80): String? {
		return try {
			val resp = ApiClient.api.getPlayUrl(cid = cid, bvid = bvid, qn = qn)
			if (!resp.isSuccessful) return null
			val body = resp.body() ?: return null
			body.data.durl.firstOrNull()?.url
		} catch (_: Exception) {
			null
		}
	}
}