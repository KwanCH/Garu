package com.example.ak24project.presentation

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import com.example.ak24project.R

class ExerciseService : Service() {
    companion object {
        const val START = "com.example.ak24project.action.START"
        const val STOP = "com.example.ak24project.action.STOP"
        // Other actions as necessary
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    //This function is called first whenever startService() is called
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TheService", "Received action: ${intent?.action}")

        when(intent?.action){
            START -> startForegroundService()
            STOP -> stopActiveService()
        }

        // Initialize and start your exercise tracking here
        return START_STICKY
    }


    private fun startForegroundService() {

        Log.d("TheService", "Service started!")
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("my_service", "My Background Service")
        } else {
            ""
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setOngoing(true)
            .setSmallIcon(R.drawable.splash_icon) // Set your own icon
            .setContentTitle("Exercise Tracking")
            .setContentText("Tracking your exercise in the background")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        startForeground(101, notificationBuilder.build())
    }
    private fun stopActiveService(){
        Log.d("TheService", "Service stopped!")
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()


    }

    private fun createNotificationChannel(channelId: String, channelName: String): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.description = "Exercise tracking"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        return channelId
    }



}



