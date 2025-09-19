package com.example.dilidiliactivity.ui.navigation.TrunckFrame

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage.SharedVideoViewModel
import com.example.dilidiliactivity.ui.common.BottomBars.BottomBar
import com.example.dilidiliactivity.ui.navigation.NavigationGraph


@Composable
fun MainFrame (ronavHostController : NavHostController,
               paddingValues : PaddingValues,
               rootNavController : NavHostController,
               sharedVm: SharedVideoViewModel
){
    val navHostController:NavHostController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            bottomBar = { BottomBar(navHostController, modifier = Modifier) },
            modifier = Modifier.fillMaxSize()
        ){
            NavigationGraph(navHostController = navHostController,paddingValues = it,rootNavController = rootNavController,sharedVm = sharedVm)
        }
    }

}