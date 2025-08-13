package com.konkuk.moru.presentation.routinefeed.screen.search

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.konkuk.moru.ui.theme.MORUTheme

/**
 * 검색 화면의 상태를 정의하는 Enum Class
 */
internal enum class SearchMode {
    INITIAL,            // 최초 진입 (루틴명 검색)
    ROUTINE_NAME_RESULT,// 루틴명 검색 결과
    TAG_SEARCH          // 태그 검색/관리
}

@Composable
fun RoutineSearchHost(
    navController: NavHostController
) {
    var searchMode by remember { mutableStateOf(SearchMode.INITIAL) }
    var searchQuery by remember { mutableStateOf("") }

    var recentSearches by remember {
        mutableStateOf(listOf("아침 요가", "산책", "TIL 작성하기", "명상", "헬스", "독서"))
    }
    val selectedTags = remember { mutableStateListOf<String>() }

    // 뒤로가기 로직 (시스템/툴바 모두 공통)
    val handleNavigateBack: () -> Unit = {
        Log.d("RoutineSearchHost", "뒤로가기 실행 - 현재 모드: $searchMode, 선택된 태그: $selectedTags")

        when (searchMode) {
            SearchMode.ROUTINE_NAME_RESULT, SearchMode.TAG_SEARCH -> {
                if (selectedTags.isNotEmpty()) {
                    Log.d("RoutineSearchHost", "태그 선택됨 → 이전 화면으로 결과 전달 후 popBackStack()")
                    // 선택된 태그를 호출자에게 전달
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selectedTagsResult", ArrayList(selectedTags))

                    val popped = navController.popBackStack() // ← 이전 화면으로
                    Log.d("RoutineSearchHost", "popBackStack 결과: $popped")
                    if (!popped) {
                        Log.w("RoutineSearchHost", "백스택 없음 → startDestination 이동")
                        navController.graph.startDestinationRoute?.let { start ->
                            navController.navigate(start)
                        }
                    }
                } else {
                    Log.d("RoutineSearchHost", "태그 선택 안됨 → 검색 초기 화면으로 전환")
                    searchMode = SearchMode.INITIAL
                    searchQuery = ""
                    selectedTags.clear()
                }
            }

            SearchMode.INITIAL -> {
                Log.d("RoutineSearchHost", "INITIAL 모드 → 이전 화면으로 popBackStack()")
                val popped = navController.popBackStack()
                Log.d("RoutineSearchHost", "popBackStack 결과: $popped")
                if (!popped) {
                    Log.w("RoutineSearchHost", "백스택 없음 → startDestination 이동")
                    navController.graph.startDestinationRoute?.let { start ->
                        navController.navigate(start)
                    }
                }
            }
        }
    }

    // 시스템 뒤로가기 처리
    BackHandler {
        Log.d("RoutineSearchHost", "BackHandler 호출됨")
        handleNavigateBack()
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
                onDeleteAllRecentSearches = { recentSearches = emptyList() },
                onNavigateToTagSearch = { searchMode = SearchMode.TAG_SEARCH },
                onNavigateBack = handleNavigateBack
            )
        }

        SearchMode.ROUTINE_NAME_RESULT -> {
            RoutineNameResultScreen(
                query = searchQuery,
                onQueryChange = { newQuery -> searchQuery = newQuery },
                onPerformSearch = {
                    // 현재 검색창(query)에 있는 내용으로 검색을 재실행합니다.
                    // 이 부분은 실제 검색 로직(예: API 호출)이 있다면 추가 구현이 필요할 수 있습니다.
                    // 지금은 상태 기반 필터링이므로 별도 작업은 필요 없습니다.
                    println("검색 재실행: $searchQuery")
                },
                selectedTags = selectedTags,
                onNavigateBack = handleNavigateBack,
                onRoutineClick = { routineId ->
                    // 다른 화면으로 진입하는 동작은 그대로 유지
                    navController.navigate(
                        com.konkuk.moru.presentation.navigation.Route
                            .RoutineFeedDetail.createRoute(routineId)
                    )
                },
                onNavigateToTagSearch = { searchMode = SearchMode.TAG_SEARCH },
                onDeleteTag = { tag -> selectedTags.remove(tag) }
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