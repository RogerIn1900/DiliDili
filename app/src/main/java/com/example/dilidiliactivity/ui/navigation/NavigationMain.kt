package com.example.dilidiliactivity.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dilidiliactivity.anr.AnrMonitor
import com.example.dilidiliactivity.anr.AnrStage
import com.example.dilidiliactivity.ui.pages.homepage.videoplayerpage.SharedVideoViewModel
import com.example.dilidiliactivity.ui.pages.homepage.videoplayerpage.VideoPlayerScreen
import com.example.dilidiliactivity.ui.pages.homepage.videoplayerpage.VideoPlayerScreen2
import com.example.dilidiliactivity.ui.pages.minepage.FullScreenPage
import com.example.dilidiliactivity.ui.navigation.trunkframe.MainFrame
import com.google.accompanist.navigation.animation.AnimatedNavHost

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RootNavHost(navHostController: NavHostController,
                paddingValues : PaddingValues,
                startDestination:String = Screen.HomePage.route,
                sharedVm: SharedVideoViewModel = SharedVideoViewModel()
) {
    val rootNavController = rememberNavController()
    val navHostController:NavHostController = navHostController

    AnimatedNavHost(
        navController = rootNavController,
        startDestination = TrunkScreen.MainFrame.route
    ) {
        composable( TrunkScreen.MainFrame.route) {
            AnrMonitor.setStage(AnrStage.NAVIGATION)
            MainFrame( navHostController,paddingValues = paddingValues,rootNavController = rootNavController,sharedVm)
        }

        composable(
            TrunkScreen.FullScreenPage.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            AnrMonitor.setStage(AnrStage.NAVIGATION)
            FullScreenPage(rootNavController)
        }

        composable(
            Routes.PLAYER,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) { backStackEntry ->
            AnrMonitor.setStage(AnrStage.NAVIGATION)
            val videoId = backStackEntry.arguments?.getString("videoId") ?: ""

            VideoPlayerScreen(
                rootNavController = rootNavController,
                videoId = videoId,
                onBack = {
                    rootNavController.popBackStack()
                },
                onExpand = {
                    rootNavController.navigate(TrunkScreen.FullScreenPage.route)
                }
            )
        }

        composable(
            Routes.PLAYER_LOCAL,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) { backStackEntry ->
            AnrMonitor.setStage(AnrStage.NAVIGATION)
            val videoId = backStackEntry.arguments?.getString("videoId") ?: ""

            VideoPlayerScreen2(
                rootNavController = rootNavController,
                videoId = videoId,
                onBack = {
                    rootNavController.popBackStack()
                },
                onExpand = {
                    rootNavController.navigate(TrunkScreen.FullScreenPage.route)
                }
            )
        }
    }
}
