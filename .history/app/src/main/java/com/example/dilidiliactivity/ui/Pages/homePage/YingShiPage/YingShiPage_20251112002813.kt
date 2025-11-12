package com.example.dilidiliactivity.ui.Pages.homePage.YingShiPage

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.example.dilidiliactivity.R
import com.example.dilidiliactivity.data.remote.ApiClient.ApiClient
import com.example.dilidiliactivity.ui.Pages.homePage.RandomVideo.RandomVideoViewModel
import com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage.VideoPlayerWithCustomTopBar


private const val TAG = "YingShiPage"

// 方式一：获取 progressive 直链（优先 data.durl[0].url）
@OptIn(UnstableApi::class)
suspend fun getPlayUrl(cid: String, bvid: String, qn: Int = 80): String? {
	val TAG = "YingShiPage2"
	return try {
		val response = ApiClient.api.getPlayUrl(cid = cid, bvid = bvid, qn = qn)
		if (!response.isSuccessful) return null
		val body = response.body() ?: return null
		Log.d(TAG, "getPlayUrl: ${body.data.durl.firstOrNull()?.url}")
		body.data.durl.firstOrNull()?.url
	} catch (_: Exception) {
		null
	}
}

// 方式二：获取 progressive 备选直链（来自 durl[0].backup_url）
@OptIn(UnstableApi::class)
suspend fun getBackupPlayUrl(cid: String, bvid: String, qn: Int = 80): String? {
	val TAG = "YingShiPage2"
	return try {
		val response = ApiClient.api.getPlayUrl(cid = cid, bvid = bvid, qn = qn)
		if (!response.isSuccessful) return null
		val body = response.body() ?: return null
		val backup = body.data.durl.firstOrNull()?.backup_url?.firstOrNull()
		Log.d(TAG, "getBackupPlayUrl: $backup")
		backup
	} catch (_: Exception) {
		null
	}
}

// 方式三：按清晰度降级选择可用直链（尝试 120/116/80/64）
@OptIn(UnstableApi::class)
suspend fun getBestAvailableProgressiveUrl(cid: String, bvid: String): String? {
	val preferredQualities = listOf(120, 116, 80, 64)
	for (quality in preferredQualities) {
		val primary = getPlayUrl(cid = cid, bvid = bvid, qn = quality)
		if (!primary.isNullOrEmpty()) return primary
		val backup = getBackupPlayUrl(cid = cid, bvid = bvid, qn = quality)
		if (!backup.isNullOrEmpty()) return backup
	}
	return null
}

@OptIn(UnstableApi::class)
@Composable
fun YingShiPage2(modifier: Modifier = Modifier) {

	val context = LocalContext.current
	val viewModel = remember { RandomVideoViewModel() }
	val exoPlayer = remember { ExoPlayer.Builder(context).build() }

	// 加载随机视频，获取播放链接列表
	LaunchedEffect(Unit) {
		viewModel.loadRandomVideos(ps = 7, rid = 1)
	}

	// 当 uiState 有数据后，调用播放地址接口获取直链，并携带必要请求头播放
	val uiState = viewModel.uiState
	LaunchedEffect(uiState) {
		val archives = uiState?.data?.archives ?: return@LaunchedEffect
		if (archives.isEmpty()) return@LaunchedEffect

		val first = archives.first()
		val bvid = first.bvid
		val cid = first.cid.toString()

		// 依次尝试多种方式获取直链：优选高画质，其次备选源
		val playUrl = getBackupPlayUrl(cid = cid, bvid = bvid)
		if (!playUrl.isNullOrEmpty()) {
			val httpFactory = DefaultHttpDataSource.Factory()
				.setDefaultRequestProperties(
					mapOf(
						// 使用带 bvid 的页面作为引用页，部分 CDN 会校验
						"Referer" to "https://www.bilibili.com/video/$bvid/",
						"Origin" to "https://www.bilibili.com",
						"User-Agent" to "Mozilla/5.0 (Android) ExoPlayer",
					)
				)

			val mediaItem = MediaItem.fromUri(playUrl)
			val mediaSource = ProgressiveMediaSource.Factory(httpFactory)
				.createMediaSource(mediaItem)

			exoPlayer.setMediaSource(mediaSource)
			exoPlayer.prepare()
			exoPlayer.playWhenReady = true
		}
	}

	AndroidView(
		modifier = modifier,
		factory = {
			PlayerView(context).apply {
				player = exoPlayer
				useController = true
			}
		}
	)

	DisposableEffect(exoPlayer) {
		onDispose { exoPlayer.release() }
	}
}


@Composable
fun YingShiPage(modifier: Modifier = Modifier) {

	val context = LocalContext.current
	val contentResolver = context.contentResolver

	val rawVideos = remember {
		listOf(
			RawVideoItem(
				label = "内置演示视频",
				resourceId = R.raw.video,
				uri = RawResourceDataSource.buildRawResourceUri(R.raw.video)
			)
		)
	}

	var selectedVideoUri by remember { mutableStateOf<Uri?>(rawVideos.firstOrNull()?.uri) }
	var selectedVideoLabel by remember { mutableStateOf(rawVideos.firstOrNull()?.label ?: "请选择视频") }
	var pickerError by remember { mutableStateOf<String?>(null) }
	val localVideos = remember { mutableStateListOf<LocalVideoItem>() }

	val openDocumentLauncher =
		rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
			if (uri != null) {
				try {
					contentResolver.takePersistableUriPermission(
						uri,
						Intent.FLAG_GRANT_READ_URI_PERMISSION
					)
				} catch (securityException: SecurityException) {
					Log.w(
						TAG,
						"无法获取持久读取权限: ${securityException.message}"
					)
				}

				val displayName = resolveDisplayName(contentResolver, uri)
				val existingIndex = localVideos.indexOfFirst { it.uri == uri }
				if (existingIndex >= 0) {
					localVideos[existingIndex] =
						localVideos[existingIndex].copy(displayName = displayName)
				} else {
					localVideos.add(0, LocalVideoItem(uri = uri, displayName = displayName))
				}

				selectedVideoUri = uri
				selectedVideoLabel = displayName
				pickerError = null
			} else if (selectedVideoUri == null) {
				pickerError = "请选择一个可播放的视频文件"
			}
		}

	val exoPlayer = remember(context) {
		ExoPlayer.Builder(context).build().apply {
			playWhenReady = true
		}
	}

	DisposableEffect(exoPlayer) {
		onDispose {
			exoPlayer.release()
		}
	}

	LaunchedEffect(selectedVideoUri) {
		val uri = selectedVideoUri
		if (uri != null) {
			Log.d(TAG, "加载视频：$uri")
			exoPlayer.setMediaItem(MediaItem.fromUri(uri))
			exoPlayer.prepare()
		} else {
			exoPlayer.stop()
			exoPlayer.clearMediaItems()
		}
	}

	LazyColumn(
		modifier = modifier.fillMaxSize()
	) {
		item {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.background(Color.Black)
			) {
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.height(220.dp),
					contentAlignment = Alignment.Center
				) {
					if (selectedVideoUri == null) {
						Text(
							text = "请选择要播放的视频文件",
							color = Color.White
						)
					} else {
						VideoPlayerWithCustomTopBar(
							exoPlayer = exoPlayer,
							onBack = {},
							onExpand = {}
						)
					}
				}
				Spacer(modifier = Modifier.height(12.dp))
				VideoSourceSelector(
					currentLabel = selectedVideoLabel,
					onPlayRaw = {
						val firstRaw = rawVideos.firstOrNull()
						if (firstRaw != null) {
							selectedVideoUri = firstRaw.uri
							selectedVideoLabel = firstRaw.label
							pickerError = null
						} else {
							pickerError = "没有可用的内置视频"
						}
					},
					onPickLocal = {
						pickerError = null
						openDocumentLauncher.launch(arrayOf("video/*"))
					}
				)
				if (pickerError != null) {
					Text(
						text = pickerError ?: "",
						color = Color(0xFFFF8A80),
						modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
						fontSize = 12.sp
					)
				}
			}
		}

		item {
			RawVideoBrowser(
				items = rawVideos,
				currentUri = selectedVideoUri,
				onSelect = { item ->
					selectedVideoUri = item.uri
					selectedVideoLabel = item.label
					pickerError = null
				}
			)
		}

		item {
			LocalVideoBrowser(
				videos = localVideos,
				currentUri = selectedVideoUri,
				onAddLocalClick = {
					pickerError = null
					openDocumentLauncher.launch(arrayOf("video/*"))
				},
				onSelect = { item ->
					selectedVideoUri = item.uri
					selectedVideoLabel = item.displayName
					pickerError = null
				}
			)
		}
	}
}

private data class RawVideoItem(
	val label: String,
	val resourceId: Int,
	val uri: Uri
)

private data class LocalVideoItem(
	val uri: Uri,
	val displayName: String
)

@Composable
private fun RawVideoBrowser(
	items: List<RawVideoItem>,
	currentUri: Uri?,
	onSelect: (RawVideoItem) -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.background(Color.White)
			.padding(horizontal = 16.dp, vertical = 20.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Text(
			text = "内置视频资源",
			color = Color.Black,
			fontSize = 16.sp
		)
		if (items.isEmpty()) {
			Text(
				text = "暂无内置视频资源",
				color = Color.Gray,
				fontSize = 12.sp
			)
			return@Column
		}
		items.forEach { item ->
			val isSelected = currentUri == item.uri
			Card(
				modifier = Modifier
					.fillMaxWidth()
					.clickable { onSelect(item) },
				colors = CardDefaults.cardColors(
					containerColor = if (isSelected) Color(0xFFFFEBEE) else Color(0xFFF5F5F5)
				)
			) {
				Column(
					modifier = Modifier.padding(16.dp),
					verticalArrangement = Arrangement.spacedBy(6.dp)
				) {
					Text(
						text = item.label,
						color = Color.Black,
						fontSize = 14.sp
					)
					Text(
						text = "资源 ID: ${item.resourceId}",
						color = Color.Gray,
						fontSize = 12.sp
					)
				}
			}
		}
	}
}

@Composable
private fun LocalVideoBrowser(
	videos: List<LocalVideoItem>,
	currentUri: Uri?,
	onAddLocalClick: () -> Unit,
	onSelect: (LocalVideoItem) -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.background(Color.White)
			.padding(horizontal = 16.dp, vertical = 20.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Text(
			text = "本地视频",
			color = Color.Black,
			fontSize = 16.sp
		)
		Button(onClick = onAddLocalClick) {
			Text("添加本地视频")
		}
		if (videos.isEmpty()) {
			Text(
				text = "尚未选择本地视频",
				color = Color.Gray,
				fontSize = 12.sp
			)
			return@Column
		}
		videos.forEach { item ->
			val isSelected = currentUri == item.uri
			Card(
				modifier = Modifier
					.fillMaxWidth()
					.clickable { onSelect(item) },
				colors = CardDefaults.cardColors(
					containerColor = if (isSelected) Color(0xFFE3F2FD) else Color(0xFFF5F5F5)
				)
			) {
				Column(
					modifier = Modifier.padding(16.dp),
					verticalArrangement = Arrangement.spacedBy(6.dp)
				) {
					Text(
						text = item.displayName,
						color = Color.Black,
						fontSize = 14.sp
					)
					Text(
						text = item.uri.toString(),
						color = Color.Gray,
						fontSize = 12.sp
					)
				}
			}
		}
	}
}

@Composable
private fun VideoSourceSelector(
	currentLabel: String,
	onPlayRaw: () -> Unit,
	onPickLocal: () -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp)
	) {
		Text(
			text = "当前视频来源：$currentLabel",
			color = Color.White,
			fontSize = 14.sp
		)
		Spacer(modifier = Modifier.height(8.dp))
		Row(
			horizontalArrangement = Arrangement.spacedBy(12.dp)
		) {
			Button(onClick = onPlayRaw) {
				Text("播放内置视频")
			}
			Button(onClick = onPickLocal) {
				Text("选择本地视频")
			}
		}
	}
}

private fun resolveDisplayName(contentResolver: ContentResolver, uri: Uri): String {
	return runCatching {
		contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
			?.use { cursor ->
				if (cursor.moveToFirst()) {
					val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
					if (index >= 0) cursor.getString(index) else null
				} else {
					null
				}
			}
	}.getOrNull() ?: uri.lastPathSegment ?: "本地视频"
}