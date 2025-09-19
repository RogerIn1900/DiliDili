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
    object FrendsPage : Screen(route = "FrendsPage",destination = "朋友")
    object PublishPage : Screen(route = "PublishPage",destination = "发布")
    object MessagePage : Screen(route = "MessagePage",destination = "消息")
    object MinePage : Screen(route = "MinePage",destination = "我的")
}