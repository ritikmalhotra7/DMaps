package com.application.dmaps.feat_core.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object Constants {
    const val DATA_STORE_NAME = "DATA_STORE"

    const val NOTIFICATION_ID = 1
    const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
    const val NOTIFICATION_CHANNEL_NAME = "Location Channel"

    const val LOCATION_TRACKING_START = "LOCATION_TRACKING_START"
    const val LOCATION_TRACKING_STOP = "LOCATION_TRACKING_STOP"
    const val LOCATION_UPDATE = "LOCATION_UPDATE"
    const val GROUP_UPDATE = "GROUP_UPDATE"
    const val GROUP_ID_KEY = "GROUP_ID_KEY"
    const val GROUP_DATA_KEY = "GROUP_DATA_KEY"
    const val SOCKET_CLOSED_KEY = "SOCKET_CLOSED_KEY"
    const val LOCATION_UPDATE_KEY = "LOCATION_UPDATE_KEY"

    const val LOCATION_INTERVAL = 10000L

    fun checkPermissionManually(context: Context, vararg permission: String): Boolean {
        return permission.all {permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}