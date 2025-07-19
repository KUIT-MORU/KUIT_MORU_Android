package com.konkuk.moru.presentation.routinefeed.component.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.core.component.chip.MoruChip
import com.konkuk.moru.ui.theme.MORUTheme

// --- Data Transfer Object for Tags ---
data class TagDto(
    val id: Int,
    val name: String,
    val isSelected: Boolean = false
)

fun generateDummyTags(): List<TagDto> {
    val sampleTagWords = listOf(
        "자바", "코틀린", "파이썬", "웹", "앱", "프론트", "백엔드", "데이터",
        "AI", "알고", "디자인", "UX", "UI", "서버", "DB", "리액트", "뷰", "안드로",
        "IOS", "기획", "보안", "게임", "클라우드", "머신", "딥러닝", "마케팅",
        "영상", "블록", "핀테크", "스타트"
    )
    val tags = sampleTagWords.mapIndexed { index, word -> TagDto(id = index, name = "#$word") }
    val selected = tags.shuffled().take(8)
    return tags.map { tag -> selected.find { it.id == tag.id }?.copy(isSelected = true) ?: tag }
}

// --- Common UI Components ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RoutineSearchTopAppBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onNavigateBack: () -> Unit,
    placeholderText: String
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        title = {
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MORUTheme.typography.body_SB_16.copy(color = MORUTheme.colors.black),
                singleLine = true,
                cursorBrush = SolidColor(MORUTheme.colors.black),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            text = placeholderText,
                            style = MORUTheme.typography.desc_M_16,
                            color = MORUTheme.colors.lightGray
                        )
                    }
                    innerTextField()
                }
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "뒤로가기",
                    modifier = Modifier.size(32.dp),
                    tint = MORUTheme.colors.black
                )
            }
        },
        actions = {
            IconButton(onClick = onSearch) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_magnifying),
                    contentDescription = "검색",
                    modifier = Modifier.size(24.dp),
                    tint = MORUTheme.colors.black
                )
            }
        }
    )
}

@Composable
internal fun TagSearchBar(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(Color(0xFFF5F6F8))
            .border(1.dp, MORUTheme.colors.lightGray, RoundedCornerShape(100.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_magnifying),
            contentDescription = "태그 검색",
            tint = MORUTheme.colors.darkGray,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = text,
            style = MORUTheme.typography.time_R_14,
            color = MORUTheme.colors.mediumGray
        )
    }
}

@Composable
internal fun RecentSearchesHeader(onDeleteAll: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "최근 검색어", style = MORUTheme.typography.body_SB_14)
        Text(
            text = "전체 삭제",
            style = MORUTheme.typography.desc_M_12,
            color = MORUTheme.colors.mediumGray,
            modifier = Modifier.clickable { onDeleteAll() }
        )
    }
}

@Composable
internal fun SortButtons(selectedOption: String, onOptionSelected: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SortButton("최신순", selectedOption == "최신순") { onOptionSelected("최신순") }
        Spacer(modifier = Modifier.size(8.dp))
        SortButton("인기순", selectedOption == "인기순") { onOptionSelected("인기순") }
    }
}

@Composable
private fun SortButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color.Black else MORUTheme.colors.veryLightGray
    val textColor = if (isSelected) Color.White else MORUTheme.colors.mediumGray
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, color = textColor, style = MORUTheme.typography.body_SB_14)
    }
}

@Composable
internal fun BackTitle(title: String, onNavigateBack: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, style = MORUTheme.typography.title_B_20)
    }
}

@Composable
internal fun HashTagSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, MORUTheme.colors.lightGray, shape = RoundedCornerShape(24.dp))
            .background(Color(0xFFF5F6F8))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_magnifying),
            contentDescription = "Search Icon",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            textStyle = MORUTheme.typography.time_R_14.copy(color = Color.Black),
            modifier = Modifier.weight(1f),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = "태그를 검색해 보세요.",
                        style = MORUTheme.typography.time_R_14,
                        color = MORUTheme.colors.mediumGray
                    )
                }
                innerTextField()
            }
        )
    }
}

@Composable
internal fun TagSectionHeader(title: String) {
    Text(text = title, style = MORUTheme.typography.body_SB_14)
}

@Composable
internal fun TagChip(
    text: String,
    selected: Boolean,
    showCloseIcon: Boolean = false,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) MORUTheme.colors.paleLime else Color(0xFFF5F6F8)
    val textColor = if (selected) MORUTheme.colors.oliveGreen else MORUTheme.colors.mediumGray
    val borderModifier = if (selected) Modifier.border(
        1.dp,
        MORUTheme.colors.oliveGreen,
        RoundedCornerShape(140.dp)
    ) else Modifier

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(140.dp))
            .then(borderModifier)
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 13.dp, vertical = 8.dp)
    ) {
        Text(text = text, style = MORUTheme.typography.time_R_14, color = textColor)
        if (showCloseIcon) {
            Spacer(modifier = Modifier.width(4.dp))
            /*Icon(
                //painter = painterResource(id = R.drawable.ic_info),
                contentDescription = "Remove Tag",
                modifier = Modifier.size(12.dp),
                tint = MORUTheme.colors.mediumGray
            )*/
        }
    }
}


/**
 * 검색 결과 화면에서 선택된 태그를 표시하는 칩
 * @param text 표시될 태그 텍스트
 * @param onDeleteClick 'X' 버튼 클릭 시 동작
 */
@Composable
internal fun SelectedTagChip(
    text: String,
    onDeleteClick: () -> Unit
) {
    MoruChip(
        text = text,
        onClick = {}, // 칩 자체에는 클릭 효과 없음
        isSelected = true,
        selectedBackgroundColor = MORUTheme.colors.charcoalBlack,
        selectedContentColor = MORUTheme.colors.limeGreen,
        unselectedBackgroundColor = Color.Transparent, // 사용되지 않음
        unselectedContentColor = Color.Transparent, // 사용되지 않음
        textStyle = MORUTheme.typography.time_R_14,
        contentPadding = PaddingValues(start = 12.dp, top = 6.dp, bottom = 6.dp, end = 4.dp),
        endIconContent = {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "태그 삭제",
                modifier = Modifier
                    .size(16.dp)
                    .clickable { onDeleteClick() } // 아이콘을 클릭해서 삭제
            )
        }
    )
}

@Composable
internal fun TagInputSearchBar(
    modifier: Modifier = Modifier,
    selectedTags: List<String>,
    onDeleteTag: (String) -> Unit,
    onAddTag: (String) -> Unit,
    onNavigateToTagSearch: () -> Unit
) {
    var tagInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    fun submitTag() {
        if (tagInput.isNotBlank()) {
            val tag = if (tagInput.startsWith("#")) tagInput else "#$tagInput"
            onAddTag(tag)
            tagInput = ""
            focusManager.clearFocus()
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(100.dp))
            .background(MORUTheme.colors.veryLightGray)
            .border(1.dp, MORUTheme.colors.lightGray, RoundedCornerShape(100.dp))
            .padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        /* ---------- ① 태그 + 입력창 : 남는 공간 전부 ---------- */
        LazyRow(
            modifier = Modifier
                .weight(1f)              // ★ 핵심: 아이콘을 제외한 모든 공간 차지
                .padding(end = 8.dp),    // 아이콘과 살짝 간격
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            /* 선택된 태그 칩 */
            items(selectedTags) { tag ->
                SelectedTagChip(text = tag) { onDeleteTag(tag) }
            }

            /* 입력창 */
            item {
                BasicTextField(
                    value = tagInput,
                    onValueChange = { tagInput = it },
                    singleLine = true,
                    textStyle = MORUTheme.typography.time_R_14
                        .copy(color = MORUTheme.colors.black),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions { submitTag() },
                    decorationBox = { inner ->
                        if (tagInput.isEmpty() && selectedTags.isEmpty()) {
                            Text(
                                text = "#태그 검색",
                                style = MORUTheme.typography.time_R_14,
                                color = MORUTheme.colors.mediumGray
                            )
                        }
                        inner()
                    }
                )
            }
        }

        /* ---------- ② “+” 아이콘 : 항상 오른쪽 ---------- */
        IconButton(
            onClick = onNavigateToTagSearch,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "태그 추가",
                tint = MORUTheme.colors.mediumGray
            )
        }
    }
}


@Preview
@Composable
fun TagInputSearchBarPreview() {
    TagInputSearchBar(
        selectedTags = listOf(),
        onNavigateToTagSearch = {},
        onAddTag = {},
        onDeleteTag = {})
}

@Preview
@Composable
fun SelectedTagChipPreview() {
    SelectedTagChip(text = "", onDeleteClick = {})
}