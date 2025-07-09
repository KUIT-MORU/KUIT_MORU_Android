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
import com.konkuk.moru.presentation.routinefeed.data.RoutineDetail


@OptIn(ExperimentalLayoutApi::class) // FlowRow를 사용하기 위해 필요
@Composable
fun RoutineInfoSection(
    modifier: Modifier = Modifier,
    routineDetail: RoutineDetail // ✅ 초기 데이터 모델을 파라미터로 받습니다.
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
                model = routineDetail.authorProfileUrl, // ✅ routineDetail에서 데이터 사용
                contentDescription = "작성자 프로필 이미지",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Text(
                text = routineDetail.authorName, // ✅ routineDetail에서 데이터 사용
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        // 2. 루틴 제목
        Text(
            text = routineDetail.routineTitle, // ✅ routineDetail에서 데이터 사용
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // 3. 루틴 설명
        Text(
            text = routineDetail.routineDescription, // ✅ routineDetail에서 데이터 사용
            style = MaterialTheme.typography.bodyLarge,
            color = Color.DarkGray
        )

        // 4. 태그
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TagChip(
                text = routineDetail.routineCategory, // ✅ routineDetail에서 데이터 사용
                backgroundColor = Color(0xFFD9F7A2)
            )
            routineDetail.tags.forEach { tag -> // ✅ routineDetail에서 데이터 사용
                TagChip(text = "#$tag", backgroundColor = Color(0xFFF1F3F5))
            }
        }
    }
}

/**
 * 태그 표시용 재사용 컴포저블
 */
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