package com.application.dmaps.feat_core.presentation.services

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.application.dmaps.R
import com.application.dmaps.feat_core.data.SocketManager
import com.application.dmaps.feat_core.domain.LocationClient
import com.application.dmaps.feat_core.utils.Constants
import com.application.dmaps.feat_core.utils.Constants.GROUP_DATA_KEY
import com.application.dmaps.feat_core.utils.Constants.GROUP_UPDATE
import com.application.dmaps.feat_core.utils.Constants.LOCATION_INTERVAL
import com.application.dmaps.feat_core.utils.Constants.LOCATION_UPDATE
import com.application.dmaps.feat_core.utils.Constants.LOCATION_UPDATE_KEY
import com.application.dmaps.feat_core.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.application.dmaps.feat_core.utils.Constants.NOTIFICATION_ID
import com.application.dmaps.feat_map.data.dto.group.Location
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {
    companion object {
        var isRunning = false
    }
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Inject
    lateinit var locationClient: LocationClient

    @Inject
    lateinit var socketManager: SocketManager

    private lateinit var notification: NotificationCompat.Builder
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Constants.LOCATION_TRACKING_START -> {
                val groupCode: String = intent.getStringExtra(Constants.GROUP_CODE_KEY) ?: ""
                isRunning = true
                startLocationTracking(groupCode)
            }

            Constants.LOCATION_TRACKING_STOP -> {
                isRunning = false
                stopLocationTracking()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startLocationTracking(groupCode: String) {
        notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(this.getString(R.string.app_name) + "Started Tracking your Location")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)
        startForeground(NOTIFICATION_ID, notification.build())
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        socketManager.apply {
            onGroupConnected(groupCode)
            setOnGroupReceived {group->
                val intent = Intent(GROUP_UPDATE).apply {
                    putExtra(GROUP_DATA_KEY, Gson().toJson(group))
                }
                sendBroadcast(intent)
            }
            setOnGroupClosed {
                stopLocationTracking()
            }
        }
        locationClient.getLocationUpdates(LOCATION_INTERVAL)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val lat = location.latitude
                val lon = location.longitude

                val loc = Location(
                    latitude = lat,
                    longitude = lon,
                    name = ""
                )
                val intent = Intent(LOCATION_UPDATE).apply {
                    putExtra(LOCATION_UPDATE_KEY, Gson().toJson(loc))
                }
                sendBroadcast(intent)
                socketManager.sendLocationToGroup(loc)
            }
            .launchIn(serviceScope)
        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    private fun stopLocationTracking() {
        stopForeground(Service.STOP_FOREGROUND_REMOVE)
        stopSelf()
        socketManager.disconnect()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        serviceScope.cancel()
    }
}