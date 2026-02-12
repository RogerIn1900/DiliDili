package com.example.dilidiliactivity.ui.navigation.trunkframe

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.dilidiliactivity.ui.pages.homepage.videoplayerpage.SharedVideoViewModel
import com.example.dilidiliactivity.ui.common.bottombars.BottomBar
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