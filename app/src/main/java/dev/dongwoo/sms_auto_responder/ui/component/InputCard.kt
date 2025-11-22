package dev.dongwoo.sms_auto_responder.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.dongwoo.sms_auto_responder.ui.theme.SurfaceDark
import dev.dongwoo.sms_auto_responder.ui.theme.TextHighEmphasisOnDark
import dev.dongwoo.sms_auto_responder.ui.theme.Typography

@Composable
fun InputCard(
    title: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = SurfaceDark,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = Typography.titleMedium,
            color = TextHighEmphasisOnDark,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        content()
    }
}
