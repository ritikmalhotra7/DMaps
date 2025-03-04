package com.application.dmaps.feat_auth.presentation.screens

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.application.dmaps.feat_core.presentation.navigation.Screen
import com.application.dmaps.feat_auth.presentation.viewmodel.SplashViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull

@Composable
fun SplashScreen(navController:NavController) {
    val viewModel : SplashViewModel = hiltViewModel()
    val isUserLoggedIn = viewModel.isLoggedIn
    val isVisible  = remember { mutableStateOf(false) }

    // Animate the alpha value
    val alpha by animateFloatAsState(
        targetValue = if (isVisible.value) 1f else 0f,
        animationSpec = tween(durationMillis = 1000) // 1-second animation
    )

    LaunchedEffect(key1 = true) {
        isVisible.value = true
        viewModel.isLoggedIn.collectLatest {
            isVisible.value = false
            navController.navigate(
                if (!it.isNullOrBlank()) Screen.MapHome.route
                else Screen.Login.route
            )
        }
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White),
        contentAlignment = Alignment.Center){
        Text(
            text = "Welcome to DMaps",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier
                .padding(8.dp)
                .graphicsLayer(alpha = alpha)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "WELCOME TO DMAPS",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier
                .padding(8.dp),
        )
    }
}