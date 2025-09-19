package com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CommentCard(
    avatar: Painter,
    userName: String,
    level: String,
    isUp: Boolean,
    isPinned: Boolean,
    time: String,
    content: String,
    likeCount: Int,
    replyCount: Int,
    onLikeClick: () -> Unit,
    onReplyClick: () -> Unit,
    onMoreClick: () -> Unit,
    data:String = ""
){
    Row (
        modifier = Modifier.background(Color.Black)
            .fillMaxWidth()
            .padding(12.dp)
    ){
        //评论者的头像
        Image(
            painter = avatar,
            contentDescription = "avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
//        右侧的评论者的名称、登记、UP、特殊标识
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = userName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Lv$level",
                    color = Color(0xFF9CDCFE),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(Color(0x3329B6F6), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
                if (isUp) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "UP",
                        color = Color(0xFFFF6699),
                        fontSize = 12.sp,
                        modifier = Modifier
                            .background(Color(0x33FF6699), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row (
            ){
                if (isPinned) {
                    Text(
                        text = "置顶 ",
                        color = Color(0xFFFFC107),
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = content,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
            //评论标识和评论内容
            Row (
                verticalAlignment = Alignment.CenterVertically
            ){
                //点赞
                IconButton(onClick = onLikeClick) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "like",
                        tint = Color.Gray
                    )
                }
                Text(
                    text = likeCount.toString(),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(8.dp))

                //点踩

                Spacer(modifier = Modifier.width(16.dp))
                //回复
                TextButton(onClick = onReplyClick) {
                    Text("回复", color = Color.Gray, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
                //日期和地点
                Text(
                    text = time,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                //拓展（三个点）
                IconButton(onClick = onMoreClick) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "more",
                        tint = Color.Gray
                    )
                }
            }
            //评论楼中的其他评论
            //部分展示
            //展开。。
        }
    }

}