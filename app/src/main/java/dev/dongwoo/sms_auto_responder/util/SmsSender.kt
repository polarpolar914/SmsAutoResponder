package dev.dongwoo.sms_auto_responder.util

import android.content.Context
import android.telephony.SmsManager
import android.util.Log

class SmsSender {
    fun sendSms(context: Context, phoneNumber: String, message: String) {
        try {
            val smsManager: SmsManager = context.getSystemService(SmsManager::class.java)
            val parts = smsManager.divideMessage(message)
            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null)
            Log.d("SmsSender", "Sent SMS to $phoneNumber: $message")
        } catch (e: Exception) {
            Log.e("SmsSender", "Failed to send SMS to $phoneNumber", e)
        }
    }
}
