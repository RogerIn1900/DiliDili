package com.example.dilidiliactivity.ui.Pages.homePage.recommend

import android.util.Log
import java.security.MessageDigest

fun buildBiliVideoUrl(
    videoId: String,
    partPath: String,
    m4sFile: String,
    trid: String,
    deadline: Long,
    upsig: String,
    bw: Int
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

fun getMixinKey(orig: String): String {
    val mixinKeyEncTab = listOf(
        46,47,18,2,53,8,23,32,15,50,10,31,58,3,45,35,
        27,43,5,49,33,9,42,19,29,28,14,39,12,38,41,13,
        37,48,7,16,24,55,40,61,26,17,0,1,60,51,30,4,
        22,25,54,21,56,59,6,63,57,62,11,36,20,34,44,52
    )

    val sb = StringBuilder()
    for (i in mixinKeyEncTab) {
        if (i < orig.length) {
            sb.append(orig[i])
        }
    }

    return sb.toString().substring(0, 32)
}

fun getBKey(): String {
    val imgKey = "7cd084941338484aae1ad9425b84077c"
    val subKey = "4932caff0ff746eab6f01bf08b70ac45"
    val wbiKey = imgKey + subKey

    val mixinKey = getMixinKey(wbiKey)
    Log.d("wbiKey", mixinKey)
    return mixinKey
}

fun md5(input: String): String {
    val md = MessageDigest.getInstance("MD5")
    val bytes = md.digest(input.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

fun signParams(params: MutableMap<String, String>, wbiKey: String): Map<String, String> {
    val ts = (System.currentTimeMillis() / 1000).toString()
    params["_wts"] = ts

    val sorted = params.toSortedMap()
    val query = sorted.entries.joinToString("&") { "${it.key}=${it.value}" }

    val wRid = md5(query + wbiKey)

    return sorted + mapOf("w_rid" to wRid)
}

fun getBUrl(): String {
    val wbiKey = "ea1db124af3c7062474693fa704f4ff8"
    val params = mutableMapOf(
        "bvid" to "BV1RDhGzREHy",
        "cid" to "123456789",
        "qn" to "80",
        "fnval" to "16"
    )

    val signedParams = signParams(params, wbiKey)
    val url = "https://api.bilibili.com/x/player/wbi/playurl?" +
            signedParams.entries.joinToString("&") { "${it.key}=${it.value}" }
    Log.d("getBUrl", url)
    return url
}
