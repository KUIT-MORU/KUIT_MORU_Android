package com.konkuk.moru.presentation.routinefeed.component.follow

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import com.konkuk.moru.R // app/src/main/res/drawable 에 ic_profile_placeholder.xml 같은 플레이스홀더 아이콘 필요
import com.konkuk.moru.core.component.button.MoruButton
import com.konkuk.moru.presentation.routinefeed.data.FollowUser
import com.konkuk.moru.ui.theme.MORUTheme

@Composable
fun UserItem(
    user: FollowUser,
    onFollowClick: (FollowUser) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 프로필 이미지 (임시 플레이스홀더)
        Image(
            painter = painterResource(id = R.drawable.ic_profile_with_background),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 사용자 정보 (이름, 자기소개)
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = user.username,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = user.bio,
                fontSize = 10.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.width(36.dp))

        // 팔로우/팔로잉 버튼(하드 코딩)
        val buttonText = if (user.isFollowing) "팔로잉" else "팔로우"
        val backgroundColor = if (user.isFollowing) Color(0xFFF1F3F5) else Color.Black
        val contentColor = if (user.isFollowing) MORUTheme.colors.mediumGray else MORUTheme.colors.limeGreen

        MoruButton(
            text = buttonText,
            onClick = { onFollowClick(user) },
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            shape = RoundedCornerShape(140.dp),
            fontSize = 14.sp,
            modifier = Modifier
                .wrapContentWidth() // 텍스트 크기에 따라 폭 조정
                .defaultMinSize(minWidth = 64.dp, minHeight = 32.dp)


        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UserItemPreview() {
    MORUTheme {
        Column {
            UserItem(
                user = FollowUser(1, "", "사용자명1", "자기소개입니다. 잘 부탁드립니다.", false),
                onFollowClick = {}
            )
            UserItem(
                user = FollowUser(2, "", "사용자명2", "안녕하세요!", true),
                onFollowClick = {}
            )
        }
    }
}