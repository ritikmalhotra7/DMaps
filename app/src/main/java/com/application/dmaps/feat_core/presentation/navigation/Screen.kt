package com.application.dmaps.feat_core.presentation.navigation

sealed class Screen(val route:String) {
    data object Splash: Screen(route = "splash_screen")
    data object Login: Screen(route = "login_screen")
    data object MapHome: Screen(route = "map_home_screen")
}