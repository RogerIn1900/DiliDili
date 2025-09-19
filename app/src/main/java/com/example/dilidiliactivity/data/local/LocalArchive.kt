package com.example.dilidiliactivity.data.local

import com.example.dilidiliactivity.data.local.archive.Archive
import com.example.dilidiliactivity.data.local.archive.Dimension
import com.example.dilidiliactivity.data.local.archive.Owner
import com.example.dilidiliactivity.data.local.archive.Rights
import com.example.dilidiliactivity.data.local.archive.Stat

//本地存储的 Archive 对象，方便网络卡顿的时候展示播放
object ArchiveSingleton {
    val archive = Archive(
        aid = 898762590,
        bvid = "BV1MN4y177PB",
        cid = 783037295,
        title = "回村三天，二舅治好了我的精神内耗",
        owner = Owner(
            mid = 170948267,
            name = "衣戈猜想",
            face = "https://i2.hdslb.com/bfs/face/0e922aee61f819b3945d1ec5e6a1397b469864d2.jpg"
        ),
        stat = Stat(
            aid = 898762590,
            view = 55859012,
            danmaku = 302382,
            reply = 63913,
            favorite = 2608859,
            coin = 7375185,
            share = 2510960,
            now_rank = 0,
            his_rank = 1,
            like = 6245071,
            dislike = 0,
            vt = 0,
            vv = 55859012,
            fav_g = 154,
            like_g = 50
        ),
        rights = Rights(
            bp = 0,
            elec = 0,
            download = 0,
            movie = 0,
            pay = 0,
            hd5 = 1,
            no_reprint = 1,
            autoplay = 1,
            ugc_pay = 0,
            is_cooperation = 0,
            ugc_pay_preview = 0,
            no_background = 0,
            arc_pay = 0,
            pay_free_watch = 0,
            0
        ),
        pic = "http://i2.hdslb.com/bfs/archive/349d11af2161fd4d734540df5206c66242b5e975.jpg",
        desc = "",
        tid = 21,
        tname = "日常",
        copyright = 1,
        mission_id = 739421,
        dynamic = "",
        dimension = Dimension(width = 1920, height = 1080, rotate = 0),
        short_link_v2 = "https://b23.tv/BV1MN4y177PB",
        first_frame = "http://i0.hdslb.com/bfs/storyff/n220724a23lei8ykkbd5bxhmzwye2gcu_firsti.jpg",
        pub_location = "北京",
        cover43 = "",
        tidv2 = 2166,
        tnamev2 = "农村生活",
        pid_v2 = 1023,
        pid_name_v2 = "三农",
        season_type = 0,
        is_ogv = false,
        ogv_info = null,
        rcmd_reason = "",
        enable_vt = 0,
        ai_rcmd = null,
        videos = 1,
        ctime = 1658707221,
        pubdate = 1658707200,
        state = 0,
        duration = 180,
    )

    // 用 Compose 的 mutableStateOf 包裹 Archive，使 UI 可以自动响应变化
//    var archive by mutableStateOf<Archive?>(null)
//        private set

    // 初始化方法
//    fun setArchiveFromJson(jsonString: String) {
//        archive = Gson().fromJson(jsonString, Archive::class.java)
//    }
}

