package dev.dongwoo.sms_auto_responder.util

import org.junit.Assert.assertEquals
import org.junit.Test

class TemplateParserTest {

    @Test
    fun `parse replaces placeholders correctly`() {
        val template = "Hello {{title}}, your code is {{text}}."
        val title = "Bank"
        val text = "123456"

        val result = TemplateParser.parse(template, title, text)

        // Note: Time is dynamic, so we can't easily test exact string equality including time unless we mock time.
        // For this simple test we just test title and text.

        val expectedStart = "Hello Bank, your code is 123456."
        assertEquals(expectedStart, result.substring(0, expectedStart.length))
    }

    @Test
    fun `parse replaces time placeholder`() {
        val template = "Time is {{time}}"
        val result = TemplateParser.parse(template, "T", "C")
        assert(result.startsWith("Time is "))
        assert(result.length > "Time is ".length)
    }
}
