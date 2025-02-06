package com.application.dmaps.feat_core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.application.dmaps.feat_map.presentation.screens.MapScreen
import com.application.dmaps.feat_auth.presentation.screens.LoginScreen
import com.application.dmaps.feat_auth.presentation.screens.SplashScreen

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {

    NavHost(navController = navController, startDestination = Screen.Splash.route){
        composable(Screen.Splash.route){
            SplashScreen(navController)
        }
        composable(Screen.Login.route){
            LoginScreen(navController)
        }
        composable(Screen.MapHome.route){
            MapScreen(navController = navController)
        }
    }
}