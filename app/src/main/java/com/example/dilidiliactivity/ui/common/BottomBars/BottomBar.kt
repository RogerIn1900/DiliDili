package com.example.dilidiliactivity.ui.common.BottomBars

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dilidiliactivity.R
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.dilidiliactivity.ui.common.BottomBars.GlobalPageState.selectedIndex
import com.example.dilidiliactivity.ui.navigation.Screen
import com.example.dilidiliactivity.ui.theme.BiliPink

@Composable
fun BottomBar(navHostController: NavController, modifier: Modifier) {
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
//    var selected by remember {
//        mutableStateOf(selected)
//    }

    var color by remember {
        mutableStateOf(Color.Black)
    }

//    color = if (currentRoute == Screen.HomePage.route || currentRoute == Screen.FrendsPage.route) Color.Black else Color.White
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(color)
    ) {
        BottomBarItem(
            text = "首页",
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .noRippleClickable {
                    selectedIndex = 1
                    navHostController.navigate(Screen.HomePage.route)
                },
            selected = selectedIndex == 1,
            color = if (color == Color.Black) Color.White else Color.Black,
            id = 0
        )
        BottomBarItem(
            text = "朋友",
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .noRippleClickable {
                    selectedIndex = 2
                    navHostController.navigate(Screen.FrendsPage.route)
                },
            selected = selectedIndex == 2,
            color = if (color == Color.Black) Color.White else Color.Black,
            id = 1
        )
        BottomBarItem(
            text = "发布",
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .noRippleClickable {
                    selectedIndex = 3
                    navHostController.navigate(Screen.PublishPage.route)
                },
            selected = selectedIndex == 3,
            color = if (color == Color.Black) Color.White else Color.Black,
            id = 2
        )
        BottomBarItem(
            text = "消息",
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .noRippleClickable {
                    selectedIndex = 4
                    navHostController.navigate(Screen.MessagePage.route)
                },
            selected = selectedIndex == 4,
            color = if (color == Color.Black) Color.White else Color.Black,
            id = 3
        )
        BottomBarItem(
            text = "我的",
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .noRippleClickable {
                    selectedIndex = 5
                    navHostController.navigate(Screen.MinePage.route)
                },
            selected = selectedIndex == 5,
            color = if (color == Color.Black) Color.White else Color.Black,
            id = 4
        )
    }


}

val bottomIconWhtie = listOf(
    R.drawable.home_foreground,
    R.drawable.dynamic_w,
    R.drawable.add_circle_icon,
    R.drawable.shop_white,
    R.drawable.material_icons_monitor__1_,
)
val bottomIconPink = listOf(
    R.drawable.home_pink,
    R.drawable.dynamic_p,
    R.drawable.add_circle_icon,
    R.drawable.shop_foreground,
    R.drawable.material_icons_monitor,
)

@Composable
fun BottomBarItem(
    text: String,
    modifier: Modifier,
    selected: Boolean = false,
    color: Color,
    id: Int
) {
    if (id != 2) {
        Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = if (selected) bottomIconPink[id] else bottomIconWhtie[id]),
                contentDescription = text,
                modifier = Modifier
                    .weight(2f)
            )
            Text(
                text = text,
                fontFamily = FontFamily(Font(R.font.cute)),
                fontSize = if (selected) 10.sp else 10.sp,
                color = if (selected) BiliPink else Color.Gray,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }
    } else {
        Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = if (selected) bottomIconPink[id] else bottomIconWhtie[id]),
                contentDescription = text,
                modifier = Modifier.weight(1f)
            )
        }

    }

}

private fun ColumnScope.getIcon(): Int {
    return 0
}

inline fun Modifier.noRippleClickable(
    crossinline onClick: () -> Unit
): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}