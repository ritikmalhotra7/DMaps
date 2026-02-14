package com.application.dmaps.feat_core.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.view.ViewCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.rememberNavController
import com.application.dmaps.feat_core.presentation.navigation.NavGraph
import com.application.dmaps.feat_core.presentation.services.LocationService
import com.application.dmaps.feat_core.utils.Constants
import com.application.dmaps.feat_core.utils.snackbar.ObserveAsEvents
import com.application.dmaps.feat_core.utils.snackbar.SnackbarController
import com.application.dmaps.feat_map.presentation.viewmodel.MapHomeViewModel
import com.application.dmaps.ui.theme.DMapsTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val scaffoldState = rememberScaffoldState()
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            navController.addOnDestinationChangedListener(object : NavController.OnDestinationChangedListener{
                override fun onDestinationChanged(
                    controller: NavController,
                    destination: NavDestination,
                    arguments: Bundle?
                ) {

                }
            })

            DMapsTheme {
                ObserveAsEvents(flow = SnackbarController.events, snackbarHostState) { event ->
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        val result = snackbarHostState.showSnackbar(message = event.message,
                            actionLabel = event.action?.name,
                            duration = event.action?.let { SnackbarDuration.Indefinite }
                                ?: SnackbarDuration.Short)
                        if (result == SnackbarResult.ActionPerformed) {
                            event.action?.action?.invoke()
                        }
                    }
                }
                Scaffold(
                    scaffoldState = scaffoldState,
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    modifier = Modifier.fillMaxSize().systemBarsPadding()
                ) { padding ->
                    NavGraph(navController = navController, modifier = Modifier.padding(padding))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(LocationService.isRunning){
            Intent(applicationContext,LocationService::class.java).apply {
                action = Constants.LOCATION_TRACKING_STOP
                startService(this)
            }
        }
    }
}