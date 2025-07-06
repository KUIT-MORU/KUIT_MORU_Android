package com.konkuk.moru.presentation.routinefeed.component.topAppBar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 인터랙티브 검색 바 컴포넌트
 * @param query 현재 검색어
 * @param onQueryChange 검색어가 변경될 때 호출되는 콜백
 * @param onSearch 검색 실행 시(키보드 검색 버튼 클릭) 호출되는 콜백
 */
/**
 * 인터랙티브 검색 바 컴포넌트
 * @param query 현재 검색어
 * @param onQueryChange 검색어가 변경될 때 호출되는 콜백
 * @param onSearch 검색 실행 시(키보드 검색 버튼 클릭) 호출되는 콜백
 */
@Composable
fun MoruSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    BasicTextField(
        value = query,
        onValueChange = onQueryChange, // ✨ 핵심: 변경 이벤트를 부모에게 전달
        modifier = modifier
            .width(201.dp)
            .height(32.dp)
            .background(
                color = Color(0xFF595959),
                shape = RoundedCornerShape(size = 100.dp)
            ),
        textStyle = TextStyle(
            color = Color.White,
            fontSize = 14.sp
        ),
        singleLine = true,
        cursorBrush = SolidColor(Color.White),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            onSearch(query)
            focusManager.clearFocus()
        }),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = "루틴을 검색해 보세요!",
                        color = Color.LightGray.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
                innerTextField()
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun MoruSearchBarPreview() {
    var text by remember { mutableStateOf("") }
    MoruSearchBar(
        query = text,
        onQueryChange = { text = it },
        onSearch = { println("Search: $it") }
    )
}