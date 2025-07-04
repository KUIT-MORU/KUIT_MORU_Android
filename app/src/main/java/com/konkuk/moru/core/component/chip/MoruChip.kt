package com.konkuk.moru.core.component.chip

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.ui.theme.MORUTheme

/**
 * Moru 디자인 시스템의 칩 (태그, 필터)
 *
 * @param text 칩 텍스트
 * @param onClick 클릭 이벤트
 * @param isSelected 선택 상태 여부
 * @param selectedBackgroundColor 선택 시 배경색
 * @param selectedContentColor 선택 시 내용 색상
 * @param unselectedBackgroundColor 미선택 시 배경색
 * @param unselectedContentColor 미선택 시 내용 색상
 * @param modifier Modifier
 * @param shape 칩 모양 (기본값: 알약 모양)
 * @param trailingContent 텍스트 뒤에 붙는 콘텐츠 (예: 닫기 아이콘)
 */

@Composable
fun MoruChip(
    fontSize: TextUnit = 14.sp,
    text: String,
    onClick: () -> Unit,
    isSelected: Boolean,
    border: BorderStroke? = null,
    selectedBackgroundColor: Color,
    selectedContentColor: Color,
    unselectedBackgroundColor: Color,
    unselectedContentColor: Color,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    startIconContent: (@Composable () -> Unit)? = null, // ◀ 시작 아이콘용 파라미터
    endIconContent: (@Composable () -> Unit)? = null     // ◀ 끝 아이콘용 파라미터
) {
    val backgroundColor = if (isSelected) selectedBackgroundColor else unselectedBackgroundColor
    val contentColor = if (isSelected) selectedContentColor else unselectedContentColor


    Surface(
        modifier = modifier
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        color = backgroundColor,
        contentColor = contentColor,
        border = border
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 시작 아이콘
            if (startIconContent != null) {
                startIconContent()
                Spacer(modifier = Modifier.width(4.dp))
            }

            Text(text = text,fontSize=fontSize)

            // 끝 아이콘
            if (endIconContent != null) {
                Spacer(modifier = Modifier.width(4.dp))
                endIconContent()
            }
        }
    }
}

@Preview(showBackground = true, name = "MoruChip 아이콘 배치 예시")
@Composable
private fun MoruChipWithIconsPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        //시작 아이콘만 있는 칩
        MoruChip(
            text = "#태그",
            isSelected = true,
            onClick = { },
            selectedBackgroundColor = Color.Black,
            selectedContentColor = MORUTheme.colors.limeGreen,
            unselectedBackgroundColor = Color.White,
            unselectedContentColor = Color.Black,

        )
        //끝에 아이콘 있는경우
        MoruChip(
            text = "#태그",
            isSelected = true,
            onClick = { },
            selectedBackgroundColor = Color.Black,
            selectedContentColor = MORUTheme.colors.limeGreen,
            unselectedBackgroundColor = Color.White,
            unselectedContentColor = Color.Black,
            endIconContent = {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    modifier = Modifier.size(14.dp)
                )
            }
            )


        // 양쪽 아이콘이 모두 있는 칩
        MoruChip(
            text = "Status",
            isSelected = false,
            onClick = { },
            selectedBackgroundColor = Color.Black,
            selectedContentColor = Color.White,
            unselectedBackgroundColor = Color.White,
            unselectedContentColor = Color.Black,
            startIconContent = {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Status",
                    modifier = Modifier.size(18.dp)
                )
            },
            endIconContent = {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier.size(18.dp)
                )
            }
        )

        // 1. 아이콘이 없는 기본 칩
        MoruChip(
            text = "#태그",
            isSelected = true,
            onClick = { },
            selectedBackgroundColor = MORUTheme.colors.paleLime,
            selectedContentColor = MORUTheme.colors.oliveGreen,
            unselectedBackgroundColor = MORUTheme.colors.veryLightGray,
            unselectedContentColor = MORUTheme.colors.darkGray
        )

        // 2. 양쪽 아이콘이 모두 있는 칩
        MoruChip(
            text = "Status",
            isSelected = true,
            onClick = { },
            selectedBackgroundColor = Color.Black,
            selectedContentColor = Color.White,
            unselectedBackgroundColor = Color.White,
            unselectedContentColor = Color.Black,
            startIconContent = {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Status",
                    modifier = Modifier.size(18.dp)
                )
            },
            endIconContent = {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier.size(18.dp)
                )
            }
        )

        Text("--- 테두리 예시 ---", fontWeight = FontWeight.Bold)

        // 3. 테두리가 있는 칩 (미완료 상태)
        MoruChip(
            text = "미완료",
            isSelected = false,
            onClick = { },
            selectedBackgroundColor = MORUTheme.colors.oliveGreen,
            selectedContentColor = Color.White,
            unselectedBackgroundColor = Color.White,
            unselectedContentColor = MORUTheme.colors.oliveGreen,
            // ◀ border 파라미터에 BorderStroke 객체를 전달
            border = BorderStroke(width = 1.dp, color = MORUTheme.colors.limeGreen)
        )

        // 4. 테두리가 없는 칩 (완료 상태)
        MoruChip(
            text = "완료",
            isSelected = true,
            onClick = { },
            selectedBackgroundColor = MORUTheme.colors.oliveGreen,
            selectedContentColor = Color.White,
            unselectedBackgroundColor = Color.White,
            unselectedContentColor = MORUTheme.colors.oliveGreen,
            border = null // ◀ border를 null로 설정
        )
    }
}