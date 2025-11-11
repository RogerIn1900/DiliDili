package com.example.dilidiliactivity.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dilidiliactivity.data.local.archive.AppDatabase
import com.example.dilidiliactivity.domain.repository.VideoRepository
import com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage.SharedVideoViewModel
import com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage.VideoPlayerScreen
import com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage.VideoPlayerScreen2
import com.example.dilidiliactivity.ui.Pages.minePage.FullScreenPage
import com.example.dilidiliactivity.ui.navigation.TrunckFrame.MainFrame
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
        startDestination = TrunckScreen.MainFrame.route
    ) {
        // 原底部导航栏页面
        composable( TrunckScreen.MainFrame.route) {
            MainFrame( navHostController,paddingValues = paddingValues,rootNavController = rootNavController,sharedVm)
        }

        // 全屏覆盖页面
        composable(
            TrunckScreen.FullScreenPage.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            FullScreenPage(rootNavController)
        }

//        //视频播放页面
//        composable(
//            "player/{videoId}",
//            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
//            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
//            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
//            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
//        ) { backStackEntry ->
//            val videoId = backStackEntry.arguments?.getString("videoId") ?: ""
//            VideoPlayerScreen(videoId, sharedVm)
//        }
        // NavHost 中的跳转
        composable(
            "player/{videoId}",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getString("videoId") ?: ""

            // 获取 Context 和 Repository
            val context = LocalContext.current
            val repository = VideoRepository(AppDatabase.getInstance(context).archiveDao())

            // 调用 VideoPlayerScreen
            VideoPlayerScreen(
                rootNavController = rootNavController,
                videoId = videoId,
                repository = repository,
                onBack = {
                    rootNavController.popBackStack()
                },
                onExpand = {
                    rootNavController.navigate(TrunckScreen.FullScreenPage.route)
                }
            )
        }

        composable(
            "playerLocal/{videoId}",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getString("videoId") ?: ""

            // 获取 Context 和 Repository
            val context = LocalContext.current
            val repository = VideoRepository(AppDatabase.getInstance(context).archiveDao())

            // 调用 VideoPlayerScreen
            VideoPlayerScreen2(
                rootNavController = rootNavController,
                videoId = videoId,
                repository = repository,
                onBack = {
                    rootNavController.popBackStack()
                },
                onExpand = {
                    rootNavController.navigate(TrunckScreen.FullScreenPage.route)
                }
            )
        }
    }
}

