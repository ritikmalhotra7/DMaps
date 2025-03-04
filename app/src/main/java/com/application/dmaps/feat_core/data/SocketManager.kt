package com.application.dmaps.feat_core.data

import com.application.dmaps.feat_core.utils.logd
import com.application.dmaps.feat_map.data.dto.group.Group
import com.application.dmaps.feat_map.data.dto.group.Location
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class SocketManager(private val socketUrl: String, private val token: String) {
    private var socket: WebSocket? = null
    private var client = OkHttpClient.Builder()
        .pingInterval(15,TimeUnit.SECONDS)
        .build()

    private var onGroupReceived: ((Group) -> Unit)? = null
    fun setOnGroupReceived(callback: (Group) -> Unit) {
        onGroupReceived = callback
    }

    private var onGroupClosed: (() -> Unit)? = null
    fun setOnGroupClosed(callback: () -> Unit) {
        onGroupClosed = callback
    }

    fun disconnect() {
        socket?.close(1000, "Closing Web Socket")
    }

    fun onGroupConnected(groupCode: String) {
        val request = Request.Builder()
            .addHeader("Authorization","Bearer $token")
            .url("${socketUrl}/connect-group/$groupCode")
            .build()
        socket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                val data = Gson().fromJson(text, Group::class.java)
                onGroupReceived?.invoke(data)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                "onFailure".logd()
                t.message?.logd("error")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                onGroupClosed?.invoke()
            }
        })
    }

    fun sendLocationToGroup(request: Location) {
        val data = Gson().toJson(request)
        data.logd()
        val isSent = socket?.send(data)?:false
        isSent.logd("isSent")
    }
}