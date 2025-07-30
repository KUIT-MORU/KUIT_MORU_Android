package com.konkuk.moru.presentation.myroutines.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.konkuk.moru.R
import com.konkuk.moru.core.component.Switch.CustomToggleSwitch
import com.konkuk.moru.core.component.chip.MoruChip
import com.konkuk.moru.presentation.routinefeed.component.modale.CenteredInfoDialog
import com.konkuk.moru.presentation.routinefeed.component.modale.CustomDialog
import com.konkuk.moru.ui.theme.MORUTheme
import com.konkuk.moru.ui.theme.moruFontBold
import kotlinx.coroutines.delay

@Composable
fun RoutineItemCard(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    title: String,
    isEditMode: Boolean,
    onDelete: () -> Unit,
    description: String,
    category: String,
    onDescriptionChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
) {
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showDeleteCompleteDialog by remember { mutableStateOf(false) }
    var isUserChecked by remember { mutableStateOf(false) }

    if (showDeleteConfirmDialog) {
        CustomDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            onConfirmation = {
                showDeleteConfirmDialog = false
                showDeleteCompleteDialog = true
                onDelete()
            },
            content = {
                Text(
                    text = "루틴을 삭제하시겠습니까?",
                    style = MORUTheme.typography.title_B_20,
                    textAlign = TextAlign.Center
                )
            }
        )
    }
    if (showDeleteCompleteDialog) {
        CenteredInfoDialog(
            onDismissRequest = { showDeleteCompleteDialog = false },
            content = {
                Text(
                    text = "삭제되었습니다!",
                    color = Color.LightGray,
                    style = MORUTheme.typography.desc_M_14
                )
            }
        )
        LaunchedEffect(Unit) {
            delay(1500)
            showDeleteCompleteDialog = false
        }
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "루틴 대표 이미지",
            modifier = Modifier
                .width(105.dp)
                .height(140.dp)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_routine_card_basic),
            error = painterResource(id = R.drawable.ic_routine_card_basic)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontFamily = moruFontBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!isEditMode) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { showDeleteConfirmDialog = true },
                        tint = MORUTheme.colors.mediumGray,
                        painter = painterResource(R.drawable.ic_trash),
                        contentDescription = " 쓰레기통"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically

            ) {
                Row(
                    modifier = Modifier.clickable { isUserChecked = !isUserChecked },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(if (isUserChecked) R.drawable.ic_checkbox_uncheck else R.drawable.ic_checkbox_gray),
                        contentDescription = "체크",
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "사용자 표시",
                        color = MORUTheme.colors.darkGray,
                        style = MORUTheme.typography.time_R_12
                    )
                }
                if (!isEditMode) {
                    MoruChip(
                        modifier = Modifier.height(28.dp),
                        text = category,
                        onClick = {},
                        isSelected = true,
                        selectedBackgroundColor = Color(0xFFEBFFC0),
                        selectedContentColor = Color(0xFF8CCD00),
                        unselectedBackgroundColor = Color.Transparent,
                        unselectedContentColor = Color.Transparent
                    )

                } else {
                    CustomToggleSwitch(
                        checked = category == "집중",
                        onCheckedChange = { isChecked ->
                            onCategoryChange(if (isChecked) "집중" else "간편")
                        },
                        leftText = "간편",
                        rightText = "집중",
                        containerColor = Color(0xFFE8E8E8),
                        thumbColor = Color(0xFFEBFFC0),
                        checkedTextColor = Color(0xFF8CCD00),
                        uncheckedTextColor = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .width(95.dp)
                            .height(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((74.dp))
                    .background(
                        color = MORUTheme.colors.veryLightGray,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(10.dp)
            ) {
                if (isEditMode) {
                    BasicTextField(
                        value = description,
                        onValueChange = onDescriptionChange,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MORUTheme.typography.time_R_14.copy(color = Color.Black)
                    )
                } else {
                    Text(
                        text = if (description.isNotBlank()) description else "설명을 입력해주세요.",
                        modifier = Modifier.fillMaxWidth(),
                        style = MORUTheme.typography.time_R_14.copy(
                            color = if (description.isNotBlank()) Color.Black else Color.Gray
                        ),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}