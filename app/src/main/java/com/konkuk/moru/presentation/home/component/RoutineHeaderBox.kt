package com.konkuk.moru.presentation.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.google.accompanist.flowlayout.FlowRow
import com.konkuk.moru.R
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

//집중,간편 타입 박스 그리는 함수
@Composable
fun RoutineHeaderBox(
    routineTitle: String,
    tags: List<String>,
    category: String,
    imageUrl: String? = null,
) {
    // 디버깅용 로그
    android.util.Log.d("RoutineHeaderBox", "🖼️ RoutineHeaderBox 렌더링:")
    android.util.Log.d("RoutineHeaderBox", "   - 제목: $routineTitle")
    android.util.Log.d("RoutineHeaderBox", "   - 이미지 URL: $imageUrl")
    android.util.Log.d("RoutineHeaderBox", "   - 이미지 URL이 null이거나 빈 문자열인가?: ${imageUrl.isNullOrBlank()}")
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (imageUrl.isNullOrBlank()) {
                // 서버에서 이미지가 없을 때 기본 투명 박스 표시
                Image(
                    painter = painterResource(R.drawable.transparentbox),
                    contentDescription = "투명 박스",
                    modifier = Modifier
                        .width(53.dp)
                        .height(52.dp)
                )
            } else {
                // 서버에서 받은 이미지 URL 사용
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "루틴 이미지",
                    modifier = Modifier
                        .width(53.dp)
                        .height(52.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.transparentbox),
                    error = painterResource(R.drawable.transparentbox)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
            ){
                Text(
                    text = routineTitle,
                    style = typography.head_EB_24,
                    color = colors.black,
                    maxLines = 2,
                    softWrap = true
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    mainAxisSpacing = 6.dp,
                    crossAxisSpacing = 4.dp
                ) {
                    tags.forEach { tag ->
                        Text(
                            text = "#$tag",
                            style = typography.body_SB_16,
                            color = colors.darkGray,
                            maxLines = 1
                        )
                    }
                }
            }
            // 집중 or 간편
            FocusTypeChip(category = category)
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun RoutineHeaderBoxPreview() {
    RoutineHeaderBox(
        routineTitle = "주말 아침 루틴",
        tags = listOf("화이팅", "루틴"),
        category = "집중",
        imageUrl = null
    )
}