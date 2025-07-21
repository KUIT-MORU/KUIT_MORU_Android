package com.konkuk.moru.presentation.myactivity.component

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.konkuk.moru.R
import com.konkuk.moru.core.util.modifier.noRippleClickable
import com.konkuk.moru.presentation.myactivity.screen.RoutineDetail
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@Composable
fun RoutineDetailContent(
    detail: RoutineDetail,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(455.dp)
                .background(colors.veryLightGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.0f to Color.Transparent,
                                0.55f to Color(0xFFF5F5F5),
                                0.75f to Color(0xFFD9D9D9),
                                0.90f to Color(0xFFBBBBBB),
                                1.0f to Color(0xFF999999)
                            )
                        )
                    )
            )

            Spacer(modifier = Modifier.height(16.dp))
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_d),
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = detail.title,
                        style = typography.title_B_24,
                        color = colors.darkGray
                    )
                    Spacer(modifier = Modifier.width(17.dp))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(47.dp)
                            .height(28.dp)
                            .background(colors.paleLime, RoundedCornerShape(12.dp))
                    ) {
                        Text("집중", color = colors.oliveGreen, style = typography.body_SB_16)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Row {
                    detail.tags.forEach {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .height(18.dp)
                                .background(
                                    color = colors.darkGray,
                                    shape = RoundedCornerShape(140.dp)
                                ),
                        ) {
                            Text(
                                text = it,
                                color = colors.paleLime,
                                style = typography.time_R_14,
                                modifier = Modifier.padding(horizontal = 7.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(11.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.width(130.dp)
                ) {
                    Text("TOTAL", style = typography.time_R_14, color = Color.White)
                    Text(
                        text = String.format(
                            "%d분 %02d초",
                            detail.totalDuration.toMinutes(),
                            detail.totalDuration.seconds % 60
                        ),
                        style = typography.title_B_14,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.width(109.dp)
                ) {
                    Text("RESULT", style = typography.time_R_14, color = Color.White)
                    CompleteCheck(false)
                }

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.width(266.dp)
                ) {
                    Text("DATE", style = typography.time_R_14, color = Color.White)
                    Text(
                        text = detail.dateRange.start.format(DateTimeFormatter.ofPattern("yyyy. MM. dd a hh:mm")) +
                                " ~ " + detail.dateRange.end.format(DateTimeFormatter.ofPattern("hh:mm")),
                        style = typography.title_B_14, color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(colors.lightGray, RoundedCornerShape(5.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(11.dp))
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        val expandedStepSet = remember { mutableStateMapOf<Int, Boolean>() }
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = "STEP", style = typography.title_B_20, color = colors.black)
            Spacer(modifier = Modifier.height(8.dp))

            detail.steps.forEach { step ->
                val isExpanded = expandedStepSet[step.order] == true

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(colors.mediumGray)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(57.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.noRippleClickable {
                                expandedStepSet[step.order] = !isExpanded
                            }
                        ) {
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                "${step.order}",
                                style = typography.body_SB_14,
                                color = colors.black
                            )
                            Spacer(modifier = Modifier.width(41.dp))
                            Text(step.name, style = typography.body_SB_14, color = colors.black)
                            Spacer(modifier = Modifier.width(12.5.dp))
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = step.startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                                style = typography.body_SB_14,
                                color = colors.black
                            )
                            step.endTime?.let {
                                Text(
                                    text = it.format(DateTimeFormatter.ofPattern("HH:mm")),
                                    style = typography.caption_L_12.copy(fontSize = 10.sp),
                                    color = colors.mediumGray
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(colors.mediumGray)
                    )

                    if (isExpanded) {
                        val clipboardManager = LocalClipboardManager.current
                        val context = LocalContext.current
                        val memoText = step.memo ?: "아주아주아주아주아주아주아주아주아주아주 기이이이인 무우우운 자아아아앙"

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(69.dp)
                                .background(colors.veryLightGray)
                                .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = step.memo ?: "아주아주아주아주아주아주아주아주아주아주 기이이이인 무우우운 자아아아앙",
                                style = typography.desc_M_12,
                                color = colors.darkGray,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(1f, fill = false)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Image(
                                painter = painterResource(id = R.drawable.ic_copy_button),
                                contentDescription = "Step Image",
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            clipboardManager.setText(AnnotatedString(memoText))
                                            Toast.makeText(context, "복사되었습니다", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            )
                        }
                    }
                    if(!isExpanded){
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                }
            }
        }
    }
}
