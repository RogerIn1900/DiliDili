package com.example.dilidiliactivity.data.local.HomePageData

val tabs = listOf("直播","推荐", "热门","动画", "最新", "影视", "新征程")

sealed class HomeTopBarTabs(val route:String,val destination: String,val page:Int) {
    object Zhibo :HomeTopBarTabs("Zhibo","直播",0)
    object Tuijian :HomeTopBarTabs("Tuijian","推荐",1)
    object Remen :HomeTopBarTabs("Remen","热门",2)
    object Donghua :HomeTopBarTabs("Donghua","动画",3)
    object Zuixin :HomeTopBarTabs("Zuixin","最新",4)
    object Yingshi :HomeTopBarTabs("Yingshi","影视",5)
    object Xinzhengcheng :HomeTopBarTabs("Xinzhengcheng","新征程",6)

}