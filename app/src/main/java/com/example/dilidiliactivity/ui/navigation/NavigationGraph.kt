package com.example.dilidiliactivity.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dilidiliactivity.ui.Pages.PublishPage.PublishPage
import com.example.dilidiliactivity.ui.Pages.frendsPage.FrendsPage
import com.example.dilidiliactivity.ui.Pages.homePage.HomePage
import com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage.SharedVideoViewModel
import com.example.dilidiliactivity.ui.Pages.messagePage.MessagePage
import com.example.dilidiliactivity.ui.Pages.minePage.MinePage


@Composable
fun NavigationGraph(navHostController: NavHostController,
                    paddingValues : PaddingValues,
                    rootNavController: NavHostController,
                    startDestination:String = Screen.HomePage.route,
                    sharedVm: SharedVideoViewModel
) {
    NavHost(navController = navHostController,startDestination = startDestination){
        composable(route = Screen.HomePage.route){
            HomePage(paddingValues,sharedVm, rootNavController)
        }
        composable(route = Screen.FrendsPage.route){
            FrendsPage(paddingValues)
        }
        composable(route = Screen.PublishPage.route){
            PublishPage(paddingValues)
        }
        composable(route = Screen.MessagePage.route){
            MessagePage(paddingValues)
        }
        composable(route = Screen.MinePage.route){
            MinePage(paddingValues,rootNavController)
        }


    }
}
