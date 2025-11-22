package dev.dongwoo.sms_auto_responder.util

import android.content.Context
import android.telephony.SmsManager
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SmsSender @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun sendSms(phoneNumber: String, message: String): Boolean {
        return try {
            val smsManager = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                context.getSystemService(SmsManager::class.java)
            } else {
                SmsManager.getDefault()
            }

            // Divide message if too long
            val parts = smsManager.divideMessage(message)
            if (parts.size > 1) {
                smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null)
            } else {
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            }
            true
        } catch (e: Exception) {
            Log.e("SmsSender", "Failed to send SMS", e)
            false
        }
    }
}
