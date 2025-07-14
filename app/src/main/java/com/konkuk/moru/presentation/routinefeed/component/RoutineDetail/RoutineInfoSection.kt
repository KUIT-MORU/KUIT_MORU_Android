
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.konkuk.moru.data.model.Routine // [수정] 통합 Routine 모델 임포트

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoutineInfoSection(
    modifier: Modifier = Modifier,
    routine: Routine // [수정] 파라미터 타입을 통합 Routine으로 변경
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. 작성자 정보
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = routine.authorProfileUrl, // [수정] 필드명 변경
                contentDescription = "작성자 프로필 이미지",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Text(
                text = routine.authorName, // [수정] 필드명 변경
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        // 2. 루틴 제목
        Text(
            text = routine.title, // [수정] 필드명 변경 (routineTitle -> title)
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // 3. 루틴 설명
        Text(
            text = routine.description, // [수정] 필드명 변경 (routineDescription -> description)
            style = MaterialTheme.typography.bodyLarge,
            color = Color.DarkGray
        )

        // 4. 태그
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TagChip(
                text = routine.category, // [수정] 필드명 변경 (routineCategory -> category)
                backgroundColor = Color(0xFFD9F7A2)
            )
            routine.tags.forEach { tag -> // [수정] 필드명 변경
                TagChip(text = "#$tag", backgroundColor = Color(0xFFF1F3F5))
            }
        }
    }
}

@Composable
private fun TagChip(text: String, backgroundColor: Color) {
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}