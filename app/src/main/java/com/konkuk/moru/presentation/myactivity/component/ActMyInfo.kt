package com.konkuk.moru.presentation.myactivity.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.konkuk.moru.R
import com.konkuk.moru.core.util.modifier.noRippleClickable
import com.konkuk.moru.presentation.navigation.Route
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun ActMyInfo(
    routineCount: Int = 0,
    followerCount: Int = 0,
    followingCount: Int = 0,
    userName: String = "사용자명",
    selfInfo: String = "자기소개",
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .background(Color(0xFFFFFFFF))
            .height(144.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(color = colors.lightGray)
            ) {}

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(end = 36.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "루틴", style = typography.body_SB_16)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = routineCount.toString(), style = typography.time_R_12)
                }
                Spacer(modifier = Modifier.width(25.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "팔로워", style = typography.body_SB_16)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = followerCount.toString(), style = typography.time_R_12)
                }
                Spacer(modifier = Modifier.width(25.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "팔로잉", style = typography.body_SB_16)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = followingCount.toString(), style = typography.time_R_12)
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 35.dp)
        ) {
            Text(
                text = userName,
                style = typography.desc_M_12,
                modifier = Modifier
                    .padding(top = 6.dp, bottom = 4.dp, end = 2.dp)
            )

            Icon(
                painterResource(R.drawable.ic_pencil),
                contentDescription = "Settings Icon",
                tint = colors.mediumGray,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(24.dp)
                    .noRippleClickable { navController.navigate(Route.ActProfile.route) }
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = colors.veryLightGray, shape = RoundedCornerShape(4.dp))
                .height(34.dp)
        ) {
            val trimmedSelfInfo = if (selfInfo.length > 40) selfInfo.take(40) + "…" else selfInfo
            Text(text = trimmedSelfInfo,
                style = typography.time_R_10,
                modifier = Modifier.padding(start = 16.dp))
        }
    }
}