package com.example.dilidiliactivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.dilidiliactivity.data.local.ArchiveSingleton
import com.example.dilidiliactivity.ui.navigation.TrunckFrame.TrunckFrame
import dagger.hilt.android.HiltAndroidApp

//@HiltAndroidApp
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //视频数据初始化
        val jsonString = """{
    "aid": 898762590,
    "videos": 1,
    "tid": 21,
    "tname": "日常",
    "copyright": 1,
    "pic": "http://i2.hdslb.com/bfs/archive/349d11af2161fd4d734540df5206c66242b5e975.jpg",
    "title": "回村三天，二舅治好了我的精神内耗",
    "pubdate": 1658707200,
    "ctime": 1658707221,
    "desc": "",
    "state": 0,
    "duration": 688,
    "mission_id": 739421,
    "rights": {
        "bp": 0,
        "elec": 0,
        "download": 0,
        "movie": 0,
        "pay": 0,
        "hd5": 1,
        "no_reprint": 1,
        "autoplay": 1,
        "ugc_pay": 0,
        "is_cooperation": 0,
        "ugc_pay_preview": 0,
        "no_background": 0,
        "arc_pay": 0,
        "pay_free_watch": 0
    },
    "owner": {
        "mid": 170948267,
        "name": "衣戈猜想",
        "face": "https://i2.hdslb.com/bfs/face/0e922aee61f819b3945d1ec5e6a1397b469864d2.jpg"
    },
    "stat": {
        "aid": 898762590,
        "view": 55859012,
        "danmaku": 302382,
        "reply": 63913,
        "favorite": 2608859,
        "coin": 7375185,
        "share": 2510960,
        "now_rank": 0,
        "his_rank": 1,
        "like": 6245071,
        "dislike": 0,
        "vt": 0,
        "vv": 55859012,
        "fav_g": 154,
        "like_g": 50
    },
    "dynamic": "",
    "cid": 783037295,
    "dimension": {
        "width": 1920,
        "height": 1080,
        "rotate": 0
    },
    "short_link_v2": "https://b23.tv/BV1MN4y177PB",
    "first_frame": "http://i0.hdslb.com/bfs/storyff/n220724a23lei8ykkbd5bxhmzwye2gcu_firsti.jpg",
    "pub_location": "北京",
    "cover43": "",
    "tidv2": 2166,
    "tnamev2": "农村生活",
    "pid_v2": 1023,
    "pid_name_v2": "三农",
    "bvid": "BV1MN4y177PB",
    "season_type": 0,
    "is_ogv": false,
    "ogv_info": null,
    "rcmd_reason": "",
    "enable_vt": 0,
    "ai_rcmd": null
}"""

        // 初始化单例
//        ArchiveSingleton.setArchiveFromJson(jsonString)


        setContent {
            var currentScreen by remember { mutableStateOf("splash") }

//            广告跳转逻辑
//            when (currentScreen){
//                "splash" -> SplashScreen {
//                    currentScreen = "ad"
//                }
//                "ad" -> AdScreen {
//                    currentScreen = "truck"
//                }
//                "truck" -> TrunckFrame()
//            }
            TrunckFrame()
        }
    }
}

