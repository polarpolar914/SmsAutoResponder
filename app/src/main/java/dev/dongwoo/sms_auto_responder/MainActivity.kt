package dev.dongwoo.sms_auto_responder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import dev.dongwoo.sms_auto_responder.ui.navigation.NavGraph
import dev.dongwoo.sms_auto_responder.ui.theme.SmsAutoResponderTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmsAutoResponderTheme {
                NavGraph()
            }
        }
    }
}
