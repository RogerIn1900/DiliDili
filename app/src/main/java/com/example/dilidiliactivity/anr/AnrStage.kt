package com.example.dilidiliactivity.anr

/**
 * 用于 ANR 采集的五个阶段标识。
 * 在 WatchDog 与 SIGQUIT 上报时带上当前阶段，便于定位卡顿发生的环节。
 */
enum class AnrStage(val tag: String) {
    /** 1. Application#onCreate、MainActivity#onCreate、首帧 setContent */
    APP_START("app_start"),

    /** 2. 页面跳转：NavHost 切换、navigate、popBackStack */
    NAVIGATION("navigation"),

    /** 3. 视频播放：ExoPlayer 创建/准备/播放、全屏等 */
    VIDEO_PLAY("video_play"),

    /** 4. 图片加载：Coil 请求、解码、Compose 绘制 */
    IMAGE_LOAD("image_load"),

    /** 5. UI 滑动：LazyColumn/HorizontalPager 滑动、重组 */
    UI_SCROLL("ui_scroll"),

    /** 未区分或空闲 */
    IDLE("idle");
}
