package com.konkuk.moru.presentation.routinefeed.screen.search

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.konkuk.moru.presentation.navigation.Route
import com.konkuk.moru.ui.theme.MORUTheme

/**
 * 검색 화면의 상태를 정의하는 Enum Class
 */
internal enum class SearchMode {
    INITIAL, // 최초 진입 (루틴명 검색)
    ROUTINE_NAME_RESULT, // 루틴명 검색 결과
    TAG_SEARCH // 태그 검색/관리
}

@Composable
fun RoutineSearchHost(
    navController: NavHostController
) {
    var searchMode by remember { mutableStateOf(SearchMode.INITIAL) }
    var searchQuery by remember { mutableStateOf("") }

    var recentSearches by remember {
        mutableStateOf(
            listOf("아침 요가", "산책", "TIL 작성하기", "명상", "헬스", "독서")
        )
    }
    val selectedTags = remember { mutableStateListOf<String>() }

    val handleNavigateBack: () -> Unit = {
        when (searchMode) {
            // 결과 화면이나 태그 검색 화면에서는 -> 초기 화면으로 이동
            SearchMode.ROUTINE_NAME_RESULT, SearchMode.TAG_SEARCH -> {
                searchMode = SearchMode.INITIAL
                searchQuery = ""
                selectedTags.clear() // ▼▼▼ 상태 초기화 시 선택된 태그도 비워줍니다.
            }
            // 초기 화면에서만 -> 이전 스크린(RoutineFeedScreen)으로 이동
            SearchMode.INITIAL -> {
                navController.popBackStack()
            }
        }
    }

    when (searchMode) {
        SearchMode.INITIAL -> {
            RoutineSearchInitialScreen(
                recentSearches = recentSearches,
                onPerformSearch = { query ->
                    searchQuery = query
                    searchMode = SearchMode.ROUTINE_NAME_RESULT
                },
                onDeleteRecentSearch = { item ->
                    recentSearches = recentSearches - item
                },
                onDeleteAllRecentSearches = {
                    recentSearches = emptyList()
                },
                onNavigateToTagSearch = {
                    searchMode = SearchMode.TAG_SEARCH
                },
                onNavigateBack = handleNavigateBack
            )
        }

        SearchMode.ROUTINE_NAME_RESULT -> {
            RoutineNameResultScreen(
                query = searchQuery,
                // ▼▼▼ `searchQuery`를 수정할 수 있는 람다 함수를 전달합니다.
                onQueryChange = { newQuery ->
                    searchQuery = newQuery
                },
                onPerformSearch = {
                    // 현재 검색창(query)에 있는 내용으로 검색을 재실행합니다.
                    // 이 부분은 실제 검색 로직(예: API 호출)이 있다면 추가 구현이 필요할 수 있습니다.
                    // 지금은 상태 기반 필터링이므로 별도 작업은 필요 없습니다.
                    println("검색 재실행: $searchQuery")
                },
                selectedTags = selectedTags,
                onNavigateBack = handleNavigateBack,
                onRoutineClick = { routineId ->
                    navController.navigate(Route.RoutineFeedDetail.createRoute(routineId))
                },
                onNavigateToTagSearch = { searchMode = SearchMode.TAG_SEARCH },
                onDeleteTag = { tag ->
                    selectedTags.remove(tag)
                }
            )
        }

        SearchMode.TAG_SEARCH -> {
            TagSearchScreen(
                originalQuery = searchQuery,
                onNavigateBack = handleNavigateBack,
                onTagSelected = { selectedTag ->
                    if (!selectedTags.contains(selectedTag)) {
                        selectedTags.add(selectedTag)
                    }
                    searchMode = SearchMode.ROUTINE_NAME_RESULT
                }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun RoutineSearchHostPreview() {
    MORUTheme {
        RoutineSearchHost(navController = rememberNavController())
    }
}