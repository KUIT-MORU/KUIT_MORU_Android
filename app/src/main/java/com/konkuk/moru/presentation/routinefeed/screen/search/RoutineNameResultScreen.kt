package com.konkuk.moru.presentation.routinefeed.screen.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.core.component.routine.RoutineListItem
import com.konkuk.moru.data.model.DummyData
import com.konkuk.moru.presentation.routinefeed.component.search.RoutineSearchTopAppBar
import com.konkuk.moru.presentation.routinefeed.component.search.SelectedTagChip
import com.konkuk.moru.presentation.routinefeed.component.search.SortButtons
import com.konkuk.moru.ui.theme.MORUTheme
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.konkuk.moru.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RoutineNameResultScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    onPerformSearch: () -> Unit, // TopAppBar에서 검색 실행을 위한 콜백
    selectedTags: List<String>,
    onNavigateBack: () -> Unit,
    onRoutineClick: (Int) -> Unit,
    onNavigateToTagSearch: () -> Unit,
    onDeleteTag: (String) -> Unit
) {
    var sortOption by remember { mutableStateOf("최신순") }

    val searchResults by remember(query, selectedTags, sortOption) {
        mutableStateOf(
            DummyData.feedRoutines.filter { routine ->
                val titleMatches =
                    query.isBlank() || routine.title.contains(query, ignoreCase = true)
                val tagsMatch = selectedTags.all { selectedTag ->
                    val cleanSelectedTag = selectedTag.removePrefix("#")
                    routine.tags.any { it.equals(cleanSelectedTag, ignoreCase = true) }
                }
                titleMatches && tagsMatch
            }.let { filteredList ->
                when (sortOption) {
                    "최신순" -> filteredList.sortedByDescending { it.routineId }
                    "인기순" -> filteredList.sortedByDescending { it.likes }
                    else -> filteredList
                }
            }
        )
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            // 1️⃣. TopAppBar를 루틴명 검색 전용 컴포넌트로 교체
            RoutineSearchTopAppBar(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onPerformSearch,
                onNavigateBack = onNavigateBack,
                placeholderText = "루틴명을 검색해보세요"
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // 2️⃣. 태그 검색 및 표시 영역: 스크린샷과 동일한 UI로 교체
            item {
                SelectedTagsSection(
                    modifier = Modifier.padding(16.dp),
                    selectedTags = selectedTags,
                    onDeleteTag = onDeleteTag,
                    onNavigateToTagSearch = onNavigateToTagSearch
                )
            }

            // 3️⃣. 정렬 버튼
            item {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    SortButtons(
                        selectedOption = sortOption,
                        onOptionSelected = { sortOption = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // 4️⃣. 검색 결과 목록
            items(searchResults) { routine ->
                RoutineListItem(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    isRunning = routine.isRunning,
                    routineName = routine.title,
                    tags = routine.tags,
                    likeCount = routine.likes,
                    isLiked = routine.isLiked,
                    onLikeClick = { /* 좋아요 로직 */ },
                    onItemClick = { onRoutineClick(routine.routineId) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

/**
 * 스크린샷의 UI와 동일하게, 선택된 태그와 '+' 버튼을 함께 보여주는 컴포넌트
 */
@Composable
private fun SelectedTagsSection(
    modifier: Modifier = Modifier,
    selectedTags: List<String>,
    onDeleteTag: (String) -> Unit,
    onNavigateToTagSearch: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp) // 내부 컨텐츠 크기에 맞게 높이 조절
            .clip(RoundedCornerShape(100.dp))
            .background(MORUTheme.colors.veryLightGray)
            .border(1.dp, MORUTheme.colors.lightGray, RoundedCornerShape(100.dp))
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 선택된 태그 칩들을 보여주는 LazyRow
        LazyRow(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(selectedTags) { tag ->
                SelectedTagChip(text = tag) { onDeleteTag(tag) }
            }
        }

        // '+' 아이콘 버튼
        IconButton(
            onClick = onNavigateToTagSearch,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_plus_search), // ◀ 이미지 리소스로 교체
                contentDescription = "태그 추가 검색",
                tint = Color.Unspecified
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RoutineNameResultScreenPreview() {
    MORUTheme {
        RoutineNameResultScreen(
            query = "아침 운동",
            onQueryChange = {},
            onPerformSearch = {},
            selectedTags = listOf("#태그그그태", "#태그2", "#tag"),
            onNavigateBack = {},
            onRoutineClick = { },
            onNavigateToTagSearch = {},
            onDeleteTag = {}
        )
    }
}