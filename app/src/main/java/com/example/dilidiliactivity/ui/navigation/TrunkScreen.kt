package com.example.dilidiliactivity.ui.navigation

sealed class TrunkScreen( val route: String,val  destination: String) {
    object MainFrame : Screen(route = "MainFrame",destination = "基本主页面导航")
    object FullScreenPage : Screen(route = Routes.FULL_SCREEN,destination = "全屏页面")
    object VideoPlayer : Screen(route = "VideoPlayer",destination = "视频播放")
}
