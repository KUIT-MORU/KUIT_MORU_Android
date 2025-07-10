import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailTopAppBar(
    likeCount: Int,
    isLiked: Boolean,
    isBookmarked: Boolean,
    onLikeClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = { /* 제목 없음 */ },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
            }
        },
        actions = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onLikeClick) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "좋아요",
                        tint = if (isLiked) Color.Red else Color.Gray
                    )
                }
                Text(text = "$likeCount", style = MaterialTheme.typography.bodyLarge)
            }
            IconButton(onClick = onBookmarkClick) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = "북마크",
                    // 3번 요청 반영: 북마크 활성화 색상 변경
                    tint = if (isBookmarked) Color.Black else Color.Gray
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}