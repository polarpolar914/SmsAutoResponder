package dev.dongwoo.sms_auto_responder.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.dongwoo.sms_auto_responder.data.entity.HistoryEntity
import dev.dongwoo.sms_auto_responder.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val historyList by viewModel.history.collectAsState()

    Scaffold(
        containerColor = DeepMidnight,
        topBar = {
            TopAppBar(
                title = { Text("발송 기록", color = TextHighEmphasisOnDark) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextHighEmphasisOnDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepMidnight)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(historyList) { history ->
                HistoryItem(history)
            }
        }
    }
}

@Composable
fun HistoryItem(history: HistoryEntity) {
    val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA)
    val dateStr = dateFormat.format(Date(history.sentAt))

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                history.ruleName,
                style = Typography.titleMedium,
                color = TextHighEmphasisOnDark,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                history.content,
                style = Typography.bodyMedium,
                color = TextMediumEmphasisOnDark
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(dateStr, style = Typography.labelLarge, color = TextMediumEmphasisOnDark)
                Text(
                    if (history.status == "SUCCESS") "발송 성공" else "발송 실패",
                    style = Typography.labelLarge,
                    color = if (history.status == "SUCCESS") Color.Green else Color.Red, // Spec says Blue or Green
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
