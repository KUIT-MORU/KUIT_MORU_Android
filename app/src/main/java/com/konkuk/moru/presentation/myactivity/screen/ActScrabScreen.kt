package com.konkuk.moru.presentation.myactivity.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.konkuk.moru.presentation.myactivity.component.BackTitle
import com.konkuk.moru.presentation.myactivity.component.ScrabRoutine
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun ActScrabScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val routines = List(10) {
        "루틴 제목" to listOf("태그1", "태그2", "태그3", "완전 긴 태그 완전 긴 태그")
    }
    val selectedIndex = remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
    ) {
        Spacer(modifier = Modifier.padding(16.dp))
        Box(
            modifier = Modifier.padding(horizontal = 16.dp)
        ){
            BackTitle(title = "스크랩한 루틴", navController = navController)
        }
        Spacer(modifier = Modifier.height(38.dp))
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 98.36.dp),
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(32.86.dp),
                contentPadding = PaddingValues()
            ) {
                itemsIndexed(routines) { index, (title, tags) ->
                    ScrabRoutine(
                        title = title,
                        tags = tags,
                        isSelected = selectedIndex.value == index,
                        onLongClick = {
                            selectedIndex.value = if (selectedIndex.value == index) null else index
                        }
                    )
                }
            }
        }
        if (selectedIndex.value != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.charcoalBlack)
                    .padding(vertical = 16.dp)
                    .clickable { selectedIndex.value = null },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "추가하기",
                    color = colors.paleLime,
                    style = typography.desc_M_16
                )
            }
        }
    }
}