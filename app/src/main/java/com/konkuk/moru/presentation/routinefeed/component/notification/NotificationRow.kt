package com.konkuk.moru.presentation.routinefeed.component.notification

import android.view.Surface
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.presentation.routinefeed.data.Notification
import com.konkuk.moru.ui.theme.MORUTheme
import java.time.Duration
import java.time.LocalDateTime

@Composable
fun NotificationRow(
    notification: Notification,
    onProfileClick: (userId: String) -> Unit
) {
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
            // 1. 클릭 가능하도록 수정하고, 클릭 시 콜백 함수 호출
            // actingUserId는 Notification 데이터 클래스에 포함되어 있다고 가정합니다.
            .clickable { onProfileClick(notification.senderId) }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = Icons.Default.Person, // TODO: 실제 프로필 이미지로 교체 (Coil 등 사용)
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

@Composable
private fun formatTimestamp(timestamp: LocalDateTime): String {
    val now = LocalDateTime.now()
    val duration = Duration.between(timestamp, now)

    return when {
        duration.toMinutes() < 1 -> "방금 전"
        duration.toMinutes() < 60 -> "${duration.toMinutes()}분 전"
        duration.toHours() < 24 -> "${duration.toHours()}일 전"
        duration.toDays() < 30 -> "${duration.toDays()}일 전"
        else -> "오래 전"
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationRowPreview() {
    // 2. 프리뷰용 더미 데이터 생성
    val dummyNotification = Notification(
        id = "루틴1", // 프로필 이동에 사용할 더미 ID
        senderId = "user-1",
        actingUser = "MORU",
        targetName = "아침 조깅 루틴",
        messageAction = "을 좋아합니다.",
        timestamp = LocalDateTime.now().minusHours(2),
        userImageUrl = ""
    )

    // 3. 앱 테마로 감싸서 실제 UI와 비슷하게 보이도록 함
    MORUTheme {
        Surface {
            NotificationRow(
                notification = dummyNotification,
                onProfileClick = { userId ->
                    // 프리뷰에서는 클릭 액션을 로그로 확인
                    println("Profile clicked: $userId")
                }
            )
        }
    }
}