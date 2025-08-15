package com.konkuk.moru.presentation.myroutines.component

/**
 * '내 루틴' 화면의 TopAppBar와 요일 선택 탭
 * (상태 호이스팅이 적용되어 재사용 및 관리가 용이한 버전)
 *
 * @param onInfoClick 정보 아이콘 클릭 시 동작
 * @param onTrashClick 휴지통 아이콘 클릭 시 동작
 * @param onDaySelected 요일 탭 선택 시 동작 (선택된 DayOfWeek 전달, 해제 시 null 전달)
 * @param selectedDay 외부에서 전달받는 현재 선택된 요일 (State)
 */
// [추가]
// [추가]
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.R
import com.konkuk.moru.ui.theme.MORUTheme
import com.konkuk.moru.ui.theme.moruFontSemiBold
import java.time.DayOfWeek
import java.util.Locale
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRoutineTopAppBar(
    onInfoClick: () -> Unit,
    onTrashClick: () -> Unit,
    onDaySelected: (DayOfWeek?) -> Unit,
    selectedDay: DayOfWeek?,
    modifier: Modifier = Modifier
) {
    val days = DayOfWeek.values() // MONDAY ~ SUNDAY

    // [추가] 시스템 글자 크기(접근성) 반영해서 탭 최소 높이를 살짝 가변 처리
    val fontScale = LocalDensity.current.fontScale
    val minTabHeight: Dp = max(40f, 32f * fontScale).dp  // 기본 40dp, fontScale이 크면 더 키움

    Column(modifier = modifier.background(MORUTheme.colors.charcoalBlack)) {
        TopAppBar(
            title = {
                Text(
                    text = "내 루틴",
                    color = MORUTheme.colors.limeGreen,
                    fontFamily = moruFontSemiBold,
                    fontWeight = FontWeight(600)
                )
            },
            actions = {
                IconButton(onClick = onInfoClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_info),
                        contentDescription = "정보",
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = onTrashClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_trash),
                        contentDescription = "삭제",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                actionIconContentColor = Color.Gray
            )
        )

        // [추가] 탭 영역 하단 모서리만 둥글게 만들 shape
        val tabShape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 4.dp,   // [추가] 좌하단 라운드
            bottomEnd = 4.dp      // [추가] 우하단 라운드
        )



        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(tabShape),     // [추가] 내부 컨텐츠도 라운드에 맞춰 클리핑
            shape = tabShape,        // [추가]
            color = MORUTheme.colors.charcoalBlack, // [추가] 탭 박스 전용 배경색 (배경과 구분 원하면 조절)
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            // [기존] TabRow는 그대로 사용
            TabRow(
                selectedTabIndex = selectedDay?.value?.minus(1) ?: 0,
                // (만약 기존에 뒤집기 트릭을 유지 중이라면 아래 한 줄 남겨두세요)
                // modifier = Modifier.graphicsLayer(scaleY = -1f), // [유지/선택]
                containerColor = Color.Transparent,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    selectedDay?.let {
                        val index = it.value - 1
                        if (index in tabPositions.indices) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                                color = Color.White
                            )
                        }
                    }
                }
            ) {
                days.forEach { day ->
                    val isSelected = selectedDay == day
                    MoruTab( // [변경] Tab → MoruTab
                        label = day.name.take(3).lowercase()
                            .replaceFirstChar { it.titlecase(Locale.getDefault()) },
                        selected = isSelected,
                        onClick = {
                            val newSelection = if (isSelected) null else day
                            onDaySelected(newSelection)
                        },
                        modifier = Modifier.weight(1f) // [유지]
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun MyRoutineTopAppBarPreview() {
    // 위 '어떻게 사용하나요?' 에서 설명한 방식의 실제 사용 예시입니다.
    var selectedDay by remember { mutableStateOf<DayOfWeek?>(DayOfWeek.MONDAY) }

    MaterialTheme {
        MyRoutineTopAppBar(
            onInfoClick = {},
            onTrashClick = {},
            selectedDay = selectedDay,
            onDaySelected = { day ->
                selectedDay = day
            }
        )
    }
}