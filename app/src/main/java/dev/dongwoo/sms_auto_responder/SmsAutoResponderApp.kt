package dev.dongwoo.sms_auto_responder

import android.app.Application
import android.content.Intent
import android.os.Build
import dagger.hilt.android.HiltAndroidApp
import dev.dongwoo.sms_auto_responder.service.KeepAliveService

@HiltAndroidApp
class SmsAutoResponderApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Start KeepAliveService
        val serviceIntent = Intent(this, KeepAliveService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
}
