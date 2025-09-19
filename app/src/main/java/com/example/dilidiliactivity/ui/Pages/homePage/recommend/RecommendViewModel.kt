package com.example.dilidiliactivity.ui.Pages.homePage.recommend

import android.util.Log
import java.security.MessageDigest

class RecommendViewModel {
}



fun buildBiliVideoUrl(
    videoId: String,          // 视频ID，例如 31214207828 或 28858387332
    partPath: String,         // 分片路径，例如 "28/78" 或 "32/73"
    m4sFile: String,          // 分片文件名，例如 "31214207828-1-100047.m4s"
    trid: String,             // 请求唯一标识
    deadline: Long,           // URL有效期时间戳
    upsig: String,            // URL签名
    bw: Int                   // 码率相关
): String {
    val baseUrl = "https://upos-hz-mirrorakam.akamaized.net/upgcxcode"
    return "$baseUrl/$partPath/$videoId/$m4sFile" +
            "?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=" +
            "&og=hw" +
            "&platform=pc" +
            "&mid=0" +
            "&oi=3113718418" +
            "&nbs=1" +
            "&deadline=$deadline" +
            "&uipk=5" +
            "&trid=$trid" +
            "&gen=playurlv3" +
            "&os=akam" +
            "&upsig=$upsig" +
            "&uparams=e,og,platform,mid,oi,nbs,deadline,uipk,trid,gen,os" +
            "&hdnts=exp=$deadline~hmac=47e5ada55c39b3957ed23c3105ac3026e95af6a0c57240f85458ab2ae67b2d2d" +
            "&bvc=vod" +
            "&nettype=0" +
            "&bw=$bw" +
            "&agrr=0" +
            "&buvid=9A02441B-4C93-D048-AE13-0D6C1242520073956infoc" +
            "&build=0" +
            "&dl=0" +
            "&f=u_0_0" +
            "&orderid=0,2"
}

// 示例调用
fun main() {
    val url1 = buildBiliVideoUrl(
        videoId = "31214207828",
        partPath = "28/78",
        m4sFile = "31214207828-1-100047.m4s",
        trid = "ae13851b05364790b78d895de130413u",
        deadline = 1755621000,
        upsig = "724554851e0b1f26653c9c5721e4ec68",
        bw = 531214
    )

    val url2 = buildBiliVideoUrl(
        videoId = "28858387332",
        partPath = "32/73",
        m4sFile = "28858387332-1-30032.m4s",
        trid = "5dec9862b8af479ab60a62dd7b41fe5u",
        deadline = 1755621456,
        upsig = "c772eb6ad58b52a9860f709256df3bb6",
        bw = 89556
    )

    println("URL1: $url1")
    println("URL2: $url2")
}



fun getMixinKey(orig: String): String {
    val mixinKeyEncTab = listOf(
        46,47,18,2,53,8,23,32,15,50,10,31,58,3,45,35,
        27,43,5,49,33,9,42,19,29,28,14,39,12,38,41,13,
        37,48,7,16,24,55,40,61,26,17,0,1,60,51,30,4,
        22,25,54,21,56,59,6,63,57,62,11,36,20,34,44,52
    )

    // 根据索引表打乱字符串
    val sb = StringBuilder()
    for (i in mixinKeyEncTab) {
        if (i < orig.length) {
            sb.append(orig[i])
        }
    }

    // 截取前 32 个字符作为最终 wbi_key
    return sb.toString().substring(0, 32)
}

// 示例
fun getBKey(): String {
    val imgKey = "7cd084941338484aae1ad9425b84077c"
    val subKey = "4932caff0ff746eab6f01bf08b70ac45"
    val wbiKey = imgKey + subKey

    val mixinKey = getMixinKey(wbiKey)
    Log.d("wbiKey", mixinKey)
    return mixinKey
    println("最终密钥: $mixinKey")
}



fun md5(input: String): String {
    val md = MessageDigest.getInstance("MD5")
    val bytes = md.digest(input.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

fun signParams(params: MutableMap<String, String>, wbiKey: String): Map<String, String> {
    // 添加 _wts 时间戳
    val ts = (System.currentTimeMillis() / 1000).toString()
    params["_wts"] = ts

    // 按 key 排序并拼接
    val sorted = params.toSortedMap()
    val query = sorted.entries.joinToString("&") { "${it.key}=${it.value}" }

    // 计算签名
    val wRid = md5(query + wbiKey)

    // 返回带签名的参数
    return sorted + mapOf("w_rid" to wRid)
}

fun getBUrl():String {
    val wbiKey = "ea1db124af3c7062474693fa704f4ff8" // 你生成的密钥
    val params = mutableMapOf(
        "bvid" to "BV1RDhGzREHy",  // 替换为你的视频 BV
        "cid" to "123456789",      // 替换为通过接口拿到的 CID
        "qn" to "80",              // 清晰度
        "fnval" to "16"            // dash 格式
    )

    val signedParams = signParams(params, wbiKey)
    val url = "https://api.bilibili.com/x/player/wbi/playurl?" +
            signedParams.entries.joinToString("&") { "${it.key}=${it.value}" }
    return url
    Log.d("getBUrl", url)
    println("最终请求接口: $url")
}

