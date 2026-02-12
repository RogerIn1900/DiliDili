package com.example.dilidiliactivity.ui.navigation

/**
 * 强类型安全
 *
 * 避免字符串硬编码
 *
 * 定义唯一的 route 标识, 更易于维护，新增页面只需在 Screen 中定义
 *
 * 保存可显示名称/描述, 顶部标题/Tab 文本可以直接用 destination
 */

sealed class Screen( val route: String,val  destination: String) {
    object LoginPage : Screen(route = "LoginPage",destination = "登陆")
    object HomePage : Screen(route = "HomePage",destination = "首页")
    object FriendsPage : Screen(route = "FriendsPage",destination = "朋友")
    object PublishPage : Screen(route = "PublishPage",destination = "发布")
    object MessagePage : Screen(route = "MessagePage",destination = "消息")
    object MinePage : Screen(route = "MinePage",destination = "我的")
}

/**
 * 集中管理导航路由常量，消除硬编码字符串
 */
object Routes {
    const val PLAYER = "player/{videoId}"
    const val PLAYER_LOCAL = "playerLocal/{videoId}"
    const val FULL_SCREEN = "FullScreenPage"

    fun player(bvid: String) = "player/$bvid"
    fun playerLocal(videoId: String) = "playerLocal/$videoId"
}
