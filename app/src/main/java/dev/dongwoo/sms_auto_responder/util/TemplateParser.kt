package dev.dongwoo.sms_auto_responder.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TemplateParser {
    fun parse(
        template: String,
        keyword: String,
        appName: String,
        notificationText: String,
        timestamp: Long
    ): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateString = format.format(date)

        return template
            .replace("{{keyword}}", keyword)
            .replace("{{app}}", appName)
            .replace("{{notification}}", notificationText)
            .replace("{{timestamp}}", dateString)
    }
}
