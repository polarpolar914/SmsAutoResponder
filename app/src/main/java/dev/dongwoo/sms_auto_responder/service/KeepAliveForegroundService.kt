package dev.dongwoo.sms_auto_responder.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import dev.dongwoo.sms_auto_responder.R

@AndroidEntryPoint
class KeepAliveForegroundService : Service() {

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startForegroundService() {
        val channelId = "SmsAutoResponderServiceChannel"
        val channelName = "SMS Auto Responder Service"
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("SMS Auto Responder Active")
            .setContentText("Monitoring notifications to send auto-replies.")
            .setSmallIcon(R.mipmap.ic_launcher) // Using default icon for now
            .build()

        startForeground(1, notification)
    }
}
