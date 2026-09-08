package com.example.dilidiliactivity.ui.playback

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ReportDrawn
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dilidiliactivity.R
import com.example.dilidiliactivity.ui.pages.homepage.videoplayerpage.PlayerControlStyle
import com.example.dilidiliactivity.ui.pages.homepage.videoplayerpage.VideoPlayerWithCustomTopBar
import dagger.hilt.android.AndroidEntryPoint

// A fixed local fixture makes lifecycle and performance runs independent of remote video APIs.
private const val DEMO_LIST_SIZE = 60

@AndroidEntryPoint
class PlaybackDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { PlaybackDemo() } }
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
private fun PlaybackDemo(model: LocalPlaybackViewModel = hiltViewModel()) {
    var selected by rememberSaveable { mutableStateOf<Int?>(null) }
    var fullscreen by rememberSaveable { mutableStateOf(false) }
    val error by model.error.collectAsState()
    val context = LocalContext.current
    BackHandler(selected != null) {
        if (fullscreen) fullscreen = false else selected = null
    }
    Surface(Modifier.fillMaxSize().semantics { testTagsAsResourceId = true }) {
        if (selected == null) {
            LazyColumn(Modifier.fillMaxSize().safeDrawingPadding().testTag("demo_list")) {
                item {
                    Text("本地播放体验", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))
                    Text("固定测试素材，不需要联网", modifier = Modifier.padding(horizontal = 16.dp))
                }
                items((0 until DEMO_LIST_SIZE).toList(), key = { it }) { index ->
                    ListItem(headlineContent = { Text("演示片段 ${index + 1}") },
                        supportingContent = { Text("播放 / 全屏 / 进度恢复") },
                        modifier = Modifier.testTag("demo_item_$index").clickable { selected = index })
                }
            }
            ReportDrawn()
        } else {
            PlaybackLifecycle(model.session)
            LaunchedEffect(Unit) { model.select("android.resource://${context.packageName}/${R.raw.playback_fixture}") }
            val engine by model.session.engine.collectAsState()
            val player = (engine as? Media3PlaybackEngine)?.player
            Column(Modifier.fillMaxSize().safeDrawingPadding()) {
                TextButton(onClick = { fullscreen = false; selected = null }, modifier = Modifier.testTag("back_to_list")) { Text("返回列表") }
                error?.let { Text(it) }
                if (player != null) {
                    VideoPlayerWithCustomTopBar(player,
                        onBack = { fullscreen = false; selected = null },
                        onExpand = { fullscreen = !fullscreen },
                        style = PlayerControlStyle.YingShi,
                        isFullScreen = fullscreen,
                        modifier = if (fullscreen) Modifier.fillMaxSize().testTag("demo_player")
                        else Modifier.fillMaxWidth().height(240.dp).testTag("demo_player"))
                }
            }
        }
    }
}
