import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 루틴 상세화면 TopAppBar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailTopAppBar(
    likeCount: Int,
    isLiked: Boolean,
    isBookmarked: Boolean,
    onBackClick: () -> Unit,
    onLikeClick: () -> Unit,
    onBookmarkClick: () -> Unit,
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            // ❗ [수정] 아이콘별로 색상을 따로 지정할 것이므로 공통 색상 설정 제거
            // actionIconContentColor = Color.Black,
            navigationIconContentColor = Color.Black
        ),
        title = { /* 제목 없음 */ },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "뒤로가기"
                )
            }
        },
        actions = {
            // 좋아요 아이콘과 카운트 (세로 배치)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clickable(onClick = onLikeClick)
                    .padding(horizontal = 8.dp)
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "좋아요",
                    // ❗ [수정] isLiked 상태에 따라 색상 명시적 지정 (기존 코드 참고)
                    tint = if (isLiked) Color.Red else Color.Gray
                )
                Text(
                    text = "$likeCount",
                    color = Color.Black,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                )
            }

            // 북마크 아이콘
            IconButton(onClick = onBookmarkClick) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "북마크",
                    // ❗ [수정] isBookmarked 상태에 따라 색상 명시적 지정 (기존 코드 참고)
                    tint = if (isBookmarked) Color.Black else Color.Gray
                )
            }
        }
    )
}

/**
 * 미리보기
 */
@Preview(showBackground = true, backgroundColor = 0xFFE0E0E0)
@Composable
private fun RoutineDetailTopAppBarPreview() {
    Column {
        // 기본 상태
        RoutineDetailTopAppBar(
            likeCount = 16,
            isLiked = false,
            isBookmarked = false,
            onBackClick = {},
            onLikeClick = {},
            onBookmarkClick = {}
        )
        // 좋아요와 북마크가 모두 활성화된 상태
        RoutineDetailTopAppBar(
            likeCount = 17,
            isLiked = true,
            isBookmarked = true,
            onBackClick = {},
            onLikeClick = {},
            onBookmarkClick = {}
        )
    }
}