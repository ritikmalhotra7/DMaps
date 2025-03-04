package com.application.dmaps.feat_map.presentation.screens

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.application.dmaps.feat_core.presentation.components.PermissionDialog
import com.application.dmaps.feat_core.presentation.services.LocationService
import com.application.dmaps.feat_core.utils.Constants
import com.application.dmaps.feat_core.utils.Constants.GROUP_CODE_KEY
import com.application.dmaps.feat_core.utils.Constants.checkPermissionManually
import com.application.dmaps.feat_core.utils.logd
import com.application.dmaps.feat_map.data.dto.group.Group
import com.application.dmaps.feat_map.presentation.components.ConnectedPeopleHorizontal
import com.application.dmaps.feat_map.presentation.components.ConnectedPeopleVertical
import com.application.dmaps.feat_map.presentation.components.MapComp
import com.application.dmaps.feat_map.presentation.viewmodel.MapHomeViewModel
import com.application.dmaps.feat_map.presentation.viewmodel.MapScreenEvent
import com.application.dmaps.feat_profile.data.dto.user.User
import com.google.android.gms.maps.model.CameraPosition
import com.google.gson.Gson
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState

@Preview
@Composable
fun MapScreen(modifier: Modifier = Modifier, navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current.applicationContext
    val activity = LocalContext.current as Activity
    val viewModel: MapHomeViewModel = hiltViewModel()

    val user by viewModel.user.collectAsStateWithLifecycle()
    val group by viewModel.groupData.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(initialValue = false)
    val location by viewModel.currentLocation.collectAsStateWithLifecycle()

    var isJoiningDialogShown by remember { mutableStateOf(false) }
    var groupCode by remember { mutableStateOf("") }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 10f)
    }

    val isMapExpanded = remember { mutableStateOf(false) }
    val mapWeight by animateFloatAsState(
        targetValue = if (isMapExpanded.value) 0.0f else 0.5f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f)
    )

    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            viewModel.permissionsRequired.forEach { permission ->
                viewModel.onPermissionResult(permission, perms[permission] == true)
            }
        }
    )

    val groupDataReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val groupData = intent?.getStringExtra(Constants.GROUP_DATA_KEY).run {
                    Gson().fromJson(this@run, Group::class.java)
                }
                viewModel.updateGroup(groupData)
            }
        }
    }
    val webSocketClosedReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {

            }
        }
    }

    DisposableEffect(true) {
        val filter = IntentFilter(Constants.GROUP_UPDATE)
        context.registerReceiver(groupDataReceiver, filter, Context.RECEIVER_EXPORTED)

        onDispose {
            context.unregisterReceiver(groupDataReceiver) // Cleanup
        }
    }
    DisposableEffect(true) {
        val filter = IntentFilter(Constants.SOCKET_CLOSED_KEY)
        context.registerReceiver(webSocketClosedReceiver, filter, Context.RECEIVER_EXPORTED)

        onDispose {
            context.unregisterReceiver(webSocketClosedReceiver) // Cleanup
        }
    }
    LaunchedEffect(key1 = true) {
        if (!checkPermissionManually(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            multiplePermissionResultLauncher.launch(
                viewModel.permissionsRequired
            )
        }
    }
    LaunchedEffect(key1 = true) {
        "recomposed".logd()
        viewModel.eventFlow.collect { event ->
            when (event) {
                is MapScreenEvent.OnCreateGroup -> {
                    if(!LocationService.isRunning) {
                        Intent(context, LocationService::class.java).apply {
                            action = Constants.LOCATION_TRACKING_START
                            putExtra(GROUP_CODE_KEY, event.group.groupCode)
                            activity.startService(this)
                        }
                    }
                }

                is MapScreenEvent.OnGroupJoined -> {
                    isJoiningDialogShown = false
                    if(!LocationService.isRunning) {
                        Intent(context, LocationService::class.java).apply {
                            action = Constants.LOCATION_TRACKING_START
                            putExtra(GROUP_CODE_KEY, groupCode)
                            activity.startService(this)
                        }
                    }
                }
                is MapScreenEvent.OnGroupClosed->{
                    if(LocationService.isRunning) {
                        Intent(context, LocationService::class.java).apply {
                            action = Constants.LOCATION_TRACKING_STOP
                            activity.startService(this)
                        }
                    }
                }
            }
        }
    }
    LaunchedEffect(key1 = Unit) {
        "recomposed".logd()
        group?.logd()
    }
    MapScreenUI(
        group = group,
        user = user,
        cameraPositionState = cameraPositionState,
        isLoading = isLoading,
        isMapExpanded = isMapExpanded.value,
        mapWeight = mapWeight,
        onCreateGroup = viewModel::onClickCreateGroup,
        onJoinGroup = {
            isJoiningDialogShown = true
        },
        onCloseGroup = {},
        onUpdateDestination = {},
        toggleMapExtension = {
            isMapExpanded.value = !isMapExpanded.value
        }
    ){
        viewModel.permissionsNotAccepted.reversed().forEach { permission ->
            PermissionDialog(
                permission = permission,
                isPermanentlyDeclined = !shouldShowRequestPermissionRationale(activity, permission),
                onDismiss = { viewModel.onDismissPermissionDialog() },
                onOkClicked = {
                    viewModel.onDismissPermissionDialog()
                    multiplePermissionResultLauncher.launch(
                        arrayOf(permission)
                    )
                },
                onGoToAppSetting = {
                    viewModel.onDismissPermissionDialog()
                    activity.apply {
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", packageName, null)
                        ).also(::startActivity)
                    }
                }
            )
        }

        if (isJoiningDialogShown) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Dialog(onDismissRequest = { }) {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(size = 16.dp))
                            .background(Color.White),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "Enter group code to join",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(8.dp)
                        )
                        HorizontalDivider()
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            value = groupCode,
                            onValueChange = { groupCode = it },
                            label = { Text("Group Code") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Groups, // Email icon
                                    contentDescription = "Username Icon"
                                )
                            }
                        )
                        Row(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Button(
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .height(32.dp),
                                onClick = {
                                    groupCode = ""
                                    isJoiningDialogShown = false
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text(
                                    text = "Cancel",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                            Button(
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .height(32.dp),
                                onClick = {
                                    viewModel.onJoinClicked(groupCode = groupCode)
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text(
                                    text = "Join",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MapScreenUI(
    group: Group?,
    user: User?,
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    isLoading: Boolean = false,
    isMapExpanded:Boolean = true,
    mapWeight: Float = 0f,
    onCreateGroup:()->Unit = {},
    onJoinGroup:()->Unit = {},
    onCloseGroup:()->Unit = {},
    onUpdateDestination:()->Unit = {},
    toggleMapExtension:()->Unit = {},
    content: @Composable () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            MapComp(
                modifier = Modifier.fillMaxHeight(group?.run { 1f - mapWeight } ?: run { 1f }),
                cameraPositionState = cameraPositionState
            )
            Row(
                modifier = Modifier.background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.DarkGray, Color.Transparent)
                    )
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hello, ".plus(user?.username ?: ""),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                )
                if (isLoading) {
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
                                text = "We are Connected with ${group?.users?.size ?: 0} Players",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier
                                    .padding(start = 8.dp, end = 8.dp)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "close",
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
                                    onCloseGroup.invoke()
                                }
                        )
                        Icon(
                            imageVector = if (!isMapExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
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
                                    toggleMapExtension.invoke()
                                }
                        )
                    }
                } ?: run {
                    Text(
                        text = "Make a Group or Join!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
                    )
                    Row(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .height(32.dp),
                            onClick = onCreateGroup,
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(
                                text = "Create Group",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                        Button(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .height(32.dp),
                            onClick = onJoinGroup,
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(
                                text = "Join Group",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
                HorizontalDivider()
                group?.let {
                    if (isMapExpanded) {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(it.users) { item ->
                                ConnectedPeopleHorizontal(item)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxHeight(mapWeight),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(it.users) { item ->
                                ConnectedPeopleVertical(item)
                            }
                        }
                    }
                }
            }
        }
        content()
    }
}