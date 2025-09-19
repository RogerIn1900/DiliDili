package com.example.dilidiliactivity.ui.navigation.TrunckFrame

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.dilidiliactivity.ui.navigation.RootNavHost

@Composable

fun TrunckFrame() {
    val navHostController: NavHostController = rememberNavController()
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) {
            RootNavHost(navHostController = navHostController, paddingValues = it)
        }
    }
}