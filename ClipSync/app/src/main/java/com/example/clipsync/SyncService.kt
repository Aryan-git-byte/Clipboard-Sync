package com.example.clipsync

import android.app.Service
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Build
import android.content.ClipboardManager
import android.content.ClipData
import android.os.Handler
import android.os.Looper
import okhttp3.*
import org.json.JSONObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class SyncService : Service() {

    private val client = OkHttpClient()
    private lateinit var laptopIP: String
    private var lastText = ""

    override fun onCreate() {
        super.onCreate()

        val prefs = getSharedPreferences("clipsync_prefs", MODE_PRIVATE)
        laptopIP = prefs.getString("laptop_ip", "") ?: ""

        createNotification()


        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        clipboard.addPrimaryClipChangedListener {
            val text = clipboard.primaryClip?.getItemAt(0)?.text.toString()
            pushToLaptop(text)
        }

        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                pullFromLaptop()
                handler.postDelayed(this, 2000)
            }
        })
    }

    private fun pushToLaptop(text: String) {
        val json = JSONObject().put("text", text)
        val body = json.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("http://$laptopIP/push")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {}
            override fun onResponse(call: Call, response: Response) {}
        })
    }

    private fun pullFromLaptop() {
        val request = Request.Builder()
            .url("http://$laptopIP/pull")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: java.io.IOException) {}

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: return
                val json = JSONObject(body)
                val serverText = json.getString("text")

                if (serverText.isNotEmpty() && serverText != lastText) {
                    lastText = serverText

                    val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("remote", serverText)
                    clipboard.setPrimaryClip(clip)
                }
            }
        })
    }

    private fun createNotification() {
        val channelId = "clipsync_service"
        val manager = getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Clipboard Sync",
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }

        val notification = Notification.Builder(this, channelId)
            .setContentTitle("Clipboard Sync Running")
            .setSmallIcon(android.R.drawable.stat_sys_upload)
            .build()

        startForeground(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
