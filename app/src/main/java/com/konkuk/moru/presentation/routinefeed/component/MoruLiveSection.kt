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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.R
import com.konkuk.moru.core.component.ProfileCard
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
                contentPadding = PaddingValues(horizontal = 12.dp), // Card 내부 패딩 고려
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(liveUsers) { user ->
                    ProfileCard(
                        painter = painterResource(id = user.profileImageRes),
                        username = user.name,
                        tag = user.tag,
                        modifier = Modifier.clickable { onUserClick(user.userId) }
                    )
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

@Composable
@Preview
fun MoruLiveSection() {
    MoruLiveSection()
}