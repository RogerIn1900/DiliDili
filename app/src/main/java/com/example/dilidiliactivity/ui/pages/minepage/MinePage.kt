package com.example.dilidiliactivity.ui.pages.minepage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.dilidiliactivity.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinePage(paddingValues: PaddingValues,rootNavController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("首页") },
                actions = {
                    Button(onClick = { rootNavController.navigate(Routes.FULL_SCREEN) }) {
                        Text("跳转全屏页面")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("MyApp 内容")

        }
    }
}

