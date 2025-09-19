package com.example.dilidiliactivity.ui.common.BottomBars

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.navigation.NavHostController

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun BottomNavigationBar(navController: NavHostController, selectedIndex: Int, onTabSelected: (Int) -> Unit) {
    val items = listOf(
        BottomNavItem("首页", Icons.Default.Home, "tab1"),
        BottomNavItem("搜索", Icons.Default.Search, "tab2"),
        BottomNavItem("收藏", Icons.Default.Favorite, "tab3"),
        BottomNavItem("我的", Icons.Default.Person, "tab4")
    )

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onTabSelected(index) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

class GameBottomBar {
}
