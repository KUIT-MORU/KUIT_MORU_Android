package com.konkuk.moru.presentation.myroutines.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.core.component.chip.MoruChip
import com.konkuk.moru.presentation.home.component.RoutineTag
import com.konkuk.moru.ui.theme.MORUTheme


@Composable
fun MyRoutineTag(
    tags: List<String>,
    isEditMode: Boolean,
    onAddTag: () -> Unit,
    onDeleteTag: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 17.dp, end = 17.dp, top = 25.dp, bottom = 30.dp),
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(tags) { tag ->
                MoruChip(
                    text = "#$tag",
                    isSelected = true,
                    onClick = {
                        if (isEditMode) {
                            onDeleteTag(tag)
                        }
                    },
                    selectedBackgroundColor = Color.Black,
                    selectedContentColor = MORUTheme.colors.limeGreen,
                    unselectedBackgroundColor = Color.White,
                    unselectedContentColor = Color.Black,
                    endIconContent = if (isEditMode) {
                        {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove Tag",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    } else null
                )
            }

            if (isEditMode) {
                item {
                    IconButton(
                        onClick = onAddTag,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(
                                    color = MORUTheme.colors.lightGray,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "태그 추가",
                                tint = Color.Black,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}


@Preview(showBackground = true, name = "태그 정보 - 보기 모드")
@Composable
private fun RoutineInfoPreview_ViewMode() {
    MORUTheme {
        MyRoutineTag(
            tags = listOf("아침루틴", "운동", "건강", "자기계발"),
            isEditMode = false, // 보기 모드
            onAddTag = {},
            onDeleteTag = {}
        )
    }
}

@Preview(showBackground = true, name = "태그 정보 - 보기 모드")
@Composable
private fun RoutineInfoPreview_ViewMode2() {
    MORUTheme {
        MyRoutineTag(
            tags = listOf("아침루틴", "운동", "건강", "자기계발"),
            isEditMode = true, // 보기 모드
            onAddTag = {},
            onDeleteTag = {}
        )
    }
}