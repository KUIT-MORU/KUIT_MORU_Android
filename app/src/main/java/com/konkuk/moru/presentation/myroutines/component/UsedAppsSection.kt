package com.konkuk.moru.presentation.myroutines.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.konkuk.moru.R
import com.konkuk.moru.data.model.AppInfo
import com.konkuk.moru.ui.theme.MORUTheme


@Composable
fun UsedAppsSection(
    apps: List<AppInfo>,
    isEditMode: Boolean,
    onAddApp: () -> Unit,
    onDeleteApp: (AppInfo) -> Unit
) {
    Column(
        Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text("사용 앱", style = MORUTheme.typography.title_B_20, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(apps) { app ->
                Box {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(
                            model = app.iconUrl,
                            contentDescription = app.name,
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    MORUTheme.colors.veryLightGray,
                                    shape = RoundedCornerShape(size = 6.dp)
                                )
                                .padding(8.dp),
                            placeholder = painterResource(id = R.drawable.ic_reset),
                            error = painterResource(id = R.drawable.ic_info)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(app.name, style = MORUTheme.typography.time_R_12)
                    }
                    if (isEditMode) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Delete App",
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.TopStart)
                                .clip(CircleShape)
                                .background(MORUTheme.colors.lightGray)
                                .clickable { onDeleteApp(app) },
                            tint = MORUTheme.colors.darkGray
                        )
                    }
                }
            }

            if (isEditMode) {
                item {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MORUTheme.colors.veryLightGray,
                                shape = RoundedCornerShape(size = 6.dp)
                            )
                            .clickable(onClick = onAddApp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add App",
                            tint = MORUTheme.colors.darkGray
                        )
                    }
                }
            }
        }
    }
}
