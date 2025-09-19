package com.example.dilidiliactivity.ui.common.animatePart

import android.util.Size
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.dilidiliactivity.ui.theme.BiliPink
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.List
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.example.dilidiliactivity.data.mapper.toDisplayCount


//R.drawable.favorite_icon
@Composable
fun Like(
    count: Int,
    onToggle: (Boolean) -> Unit
    ) {

    burstImage(Icons.Default.Favorite, Icons.Default.Favorite, count,onToggle)
}

@Composable
fun DisLike(count: Int,    onToggle: (Boolean) -> Unit) {
    burstImage(Icons.Default.Close, Icons.Default.Close, count,onToggle)
}

@Composable
fun Coin(count: Int,    onToggle: (Boolean) -> Unit) {
    burstImage(Icons.Default.CheckCircle, Icons.Default.CheckCircle, count,onToggle)
}

@Composable
fun Star(count: Int,    onToggle: (Boolean) -> Unit) {
    burstImage(Icons.Default.Star, Icons.Default.Star, count,onToggle)
}

@Composable
fun Share(count: Int,    onToggle: (Boolean) -> Unit) {
    burstImage(Icons.Default.Share, Icons.Default.Share, count,onToggle)
}

@Composable
fun burstImage(
    mainImage: ImageVector = Icons.Default.Favorite,
    burstImage: ImageVector = Icons.Default.Favorite,
    count: Int,
    onToggle: (Boolean) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LikeButtonWithBurst(40.dp, mainImage, burstImage,onToggle)
        Text("${count.toDisplayCount()}")
    }
}


//迸飞图案的基本组件
@Composable
fun LikeButtonWithBurst(
    size: Dp,
    mainImage: ImageVector = Icons.Default.Favorite,
    burstImage: ImageVector = Icons.Default.Favorite,
    onToggle: (Boolean) -> Unit
) {

    var liked by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val heartColor = if (liked) BiliPink else Color.Gray
    val scale = remember { Animatable(1f) }
    var explode by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(size)
            .clickable (
                indication = null, // 去掉点击效果（灰色方框/波纹）
                interactionSource = remember { MutableInteractionSource() }
            ){
                if (!liked) {
                    liked = true
                    onToggle(liked)
                    scope.launch {
                        // step 1: 膨胀 (触发爆炸)
                        explode = true
                        scale.animateTo(
                            targetValue = 1.4f,
                            animationSpec = tween(250, easing = LinearEasing)
                        )
                        // step 2: 开始缩小时，小心心消失
                        explode = false
                        scale.animateTo(
                            targetValue = 1.1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                        // step 3: 两次回弹
                        scale.animateTo(
                            targetValue = 1.2f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                        scale.animateTo(
                            targetValue = 1.05f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                        scale.animateTo(
                            targetValue = 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                    }
                } else {
                    liked = false
                    onToggle(liked)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // 小心心迸发（只在膨胀过程中存在）
        if (explode) {
            ExplodingHeartsDuringGrowth(color = heartColor, burstImage = burstImage)
        }

        // 主心
        Icon(
            imageVector = mainImage,
            contentDescription = "Like",
            tint = heartColor,
            modifier = Modifier
                .size(size * 0.6f)
                .scale(scale.value)
        )
    }
}

@Composable
fun ExplodingHeartsDuringGrowth(color: Color, burstImage: ImageVector) {
    val heartCount = 6
    val animatables = remember {
        List(heartCount) { Animatable(0f) } // 半径
    }

    // 小心心开始飞散
    LaunchedEffect(Unit) {
        animatables.forEach { radius ->
            launch {
                radius.animateTo(
                    targetValue = 120f,
                    animationSpec = tween(250, easing = LinearEasing) // 和大心膨胀同步
                )
            }
        }
    }

    animatables.forEachIndexed { index, radius ->
        val angle = (360f / heartCount) * index
        val radian = Math.toRadians(angle.toDouble())
        val x = (cos(radian) * radius.value).toFloat()
        val y = (sin(radian) * radius.value).toFloat()

        // 透明度跟随半径增长而下降
        val alpha = 1f - (radius.value / 120f)

        Icon(
            imageVector = burstImage,
            contentDescription = null,
            tint = color.copy(alpha = alpha.coerceIn(0f, 1f)),
            modifier = Modifier
                .size(16.dp)
                .offset { IntOffset(x.toInt(), y.toInt()) }
        )
    }
}
