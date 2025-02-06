package com.application.dmaps.feat_map.presentation.screens

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.application.dmaps.feat_map.presentation.components.ConnectedPeopleHorizontal
import com.application.dmaps.feat_map.presentation.components.ConnectedPeopleVertical
import com.application.dmaps.feat_map.presentation.components.MapComp
import com.application.dmaps.feat_map.presentation.viewmodel.MapHomeViewModel
import com.application.dmaps.feat_map.presentation.viewmodel.MapIncomingEvent

@Composable
fun MapScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    val viewModel: MapHomeViewModel = hiltViewModel()
    val user by viewModel.user.collectAsStateWithLifecycle()
    val group by viewModel.groupData.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(initialValue = false)

    val isMapExpanded = remember { mutableStateOf(false) }
    val weightAnimate by animateFloatAsState(
        targetValue = if (isMapExpanded.value) 0.05f else 0.5f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 200f)
    )
    LaunchedEffect(key1 = user) {
        user?.let {
            Log.d("taget-user", user.toString())
        } ?: run {
            viewModel.getCurrentUser()
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            MapComp()
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically){
                Text(
                    text = "Hello, ".plus(user?.username ?: ""),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier
                        .weight(1f)
                )
                if(isLoading){
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        trackColor = Color.Blue,
                        color = Color.White
                    )
                }
            }
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color.White),
            ) {
                group?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Connected People",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                            )
                            Text(
                                text = "We are Connected with 4 Players",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier
                                    .padding(start = 8.dp, end = 8.dp)
                            )
                        }
                        Icon(
                            imageVector = if (!isMapExpanded.value) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                            contentDescription = "expand",
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(
                                    RoundedCornerShape(
                                        corner = CornerSize(8.dp)
                                    )
                                )
                                .background(Color.LightGray)
                                .alpha(0.5f)
                                .clickable {
                                    isMapExpanded.value = !isMapExpanded.value
                                }
                        )
                    }
                } ?: run {
                    Row(modifier = Modifier.padding(8.dp),verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Make a Group!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
                                .weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "expand",
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(
                                    RoundedCornerShape(
                                        corner = CornerSize(8.dp)
                                    )
                                )
                                .background(Color.LightGray)
                                .alpha(0.5f)
                                .clickable {
                                    viewModel.onEvent(MapIncomingEvent.CreateGroup)
                                }
                        )
                    }
                }
                HorizontalDivider()
                group?.let {
                    if (isMapExpanded.value) {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items((0..6).toList()) { item ->
                                ConnectedPeopleHorizontal()
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxHeight(weightAnimate),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items((0..6).toList()) { item ->
                                ConnectedPeopleVertical()
                            }
                        }
                    }
                }
            }
        }

    }
}