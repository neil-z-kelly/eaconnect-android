package com.ea.connect.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ea.connect.data.DemoData

@Composable
fun NotificationsScreen() {
    LazyColumn(Modifier.fillMaxWidth()) {
        item {
            Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 8.dp)) {
                Text(
                    "EA CONNECT",
                    color = EaColors.Blue,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text("Activity", style = MaterialTheme.typography.headlineMedium, color = EaColors.White)
            }
        }
        items(DemoData.notifications) { notification ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .background(EaColors.Surface, RoundedCornerShape(14.dp))
                    .border(1.dp, EaColors.Outline, RoundedCornerShape(14.dp))
                    .padding(14.dp),
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        notification.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = EaColors.White,
                    )
                    Text(
                        notification.detail,
                        color = EaColors.Muted,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(notification.ago, color = EaColors.Muted, style = MaterialTheme.typography.bodyMedium)
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}
