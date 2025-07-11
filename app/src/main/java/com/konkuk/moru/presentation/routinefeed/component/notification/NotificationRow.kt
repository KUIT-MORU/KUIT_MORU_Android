package com.konkuk.moru.presentation.routinefeed.component.notification

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.konkuk.moru.presentation.routinefeed.data.Notification

import java.time.Duration
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationRow(notification: Notification) {
    // 구조화된 데이터를 조합하여 AnnotatedString을 만듭니다.
    val annotatedMessage = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(notification.actingUser)
        }
        notification.targetName?.let {
            append("이 ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(it)
            }
        }
        append(notification.messageAction)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = Icons.Default.Person,
            contentDescription = "프로필 이미지",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // AnnotatedString을 Text에 적용
        Text(
            text = annotatedMessage,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = formatTimestamp(notification.timestamp),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

// 이 파일의 다른 부분은 이전과 동일합니다.
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun formatTimestamp(timestamp: LocalDateTime): String {
    val now = LocalDateTime.now()
    val duration = Duration.between(timestamp, now)

    return when {
        duration.toMinutes() < 1 -> "방금 전"
        duration.toMinutes() < 60 -> "${duration.toMinutes()}분 전"
        duration.toHours() < 24 -> "${duration.toHours()}시간 전"
        duration.toDays() < 30 -> "${duration.toDays()}일 전"
        else -> "오래 전"
    }
}