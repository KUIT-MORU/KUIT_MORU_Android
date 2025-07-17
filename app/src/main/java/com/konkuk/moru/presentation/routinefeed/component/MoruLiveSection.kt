package com.konkuk.moru.presentation.routinefeed.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.konkuk.moru.R
import com.konkuk.moru.presentation.routinefeed.data.LiveUserInfo

@Composable
fun MoruLiveSection(
    modifier: Modifier = Modifier,
    liveUsers: List<LiveUserInfo>,
    onUserClick: (Int) -> Unit,
    onTitleClick: () -> Unit
) {
    Column(modifier = modifier.padding(top = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTitleClick() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "MORU LIVE", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(2.dp))
            Image(
                painter = painterResource(id = if (liveUsers.isNotEmpty()) R.drawable.ic_antenna_color else R.drawable.ic_antenna),
                contentDescription = "Live Status",
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (liveUsers.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // [수정] 데이터 모델 변경에 따라 `items`의 key를 `userId`로 변경
                items(liveUsers, key = { it.userId }) { user ->
                    // [수정] 기존 ProfileCard와 동일한 UI를 Column과 AsyncImage로 직접 구현
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.clickable { onUserClick(user.userId) }
                    ) {
                        AsyncImage(
                            model = user.profileImageUrl,
                            contentDescription = "${user.name}의 프로필 사진",
                            placeholder = painterResource(id = R.drawable.ic_profile_with_background),
                            error = painterResource(id = R.drawable.ic_profile_with_background),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                        )
                        Text(
                            text = user.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = user.tag,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_antenna),
                    contentDescription = "No Live",
                    modifier = Modifier.size(48.dp)
                )
                Text(text = "진행중인 라이브가 없어요", color = Color.Gray, fontSize = 16.sp)
            }
        }
    }
}

// [수정] Preview가 정상적으로 보이도록 샘플 데이터 추가
@Preview(showBackground = true, name = "라이브 있는 경우")
@Composable
private fun MoruLiveSectionPreview() {
    val sampleUsers = listOf(
        LiveUserInfo(1, "운동하는 제니", "#오운완", "https://images.unsplash.com/photo-1580489944761-15a19d654956"),
        LiveUserInfo(3, "개발자 모루", "#TIL", null),
        LiveUserInfo(101, "요가마스터", "#요가", "https://images.unsplash.com/photo-1552058544-f2b08422138a"),
        LiveUserInfo(102, "개발왕", "#코딩", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d"),
        LiveUserInfo(4, "요리왕 준", "#집밥", "https://images.unsplash.com/photo-1541533267753-bab141444692"),
    )

    MoruLiveSection(
        liveUsers = sampleUsers,
        onUserClick = {},
        onTitleClick = {}
    )
}

@Preview(showBackground = true, name = "라이브 없는 경우")
@Composable
private fun MoruLiveSectionEmptyPreview() {
    MoruLiveSection(
        liveUsers = emptyList(),
        onUserClick = {},
        onTitleClick = {}
    )
}