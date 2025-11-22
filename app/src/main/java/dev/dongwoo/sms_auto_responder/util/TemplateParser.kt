package dev.dongwoo.sms_auto_responder.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TemplateParser {
    fun parse(template: String, title: String, text: String): String {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.KOREA)
        val currentTime = timeFormat.format(Date())

        return template
            .replace("{{title}}", title)
            .replace("{{text}}", text)
            .replace("{{time}}", currentTime)
    }
}
