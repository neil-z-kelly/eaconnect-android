package com.ea.connect.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ea.connect.data.DemoData
import com.ea.connect.data.Friend

@Composable
fun FindFriendsScreen() {
    var query by remember { mutableStateOf("") }
    var added by remember { mutableStateOf(setOf<String>()) }
    val normalized = query.trim().lowercase()
    val results: List<Friend> = when {
        normalized.isEmpty() -> emptyList()
        else -> (DemoData.searchResults.filterKeys { it.startsWith(normalized) }.values +
            DemoData.friends.filter { it.gamertag.lowercase().startsWith(normalized) }).toList()
    }

    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        EaScreenHeader(
            "Find Friends",
            Modifier.padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 8.dp),
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .background(EaColors.Surface, RoundedCornerShape(12.dp))
                .border(1.dp, EaColors.Outline, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Filled.Search, null, tint = EaColors.Muted, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Box(Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        "Search by EA ID or gamertag",
                        color = EaColors.Muted,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = EaColors.White),
                    cursorBrush = SolidColor(EaColors.Blue),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Text(
            "Suggested",
            style = MaterialTheme.typography.titleMedium,
            color = EaColors.White,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        )
        val shown = results.ifEmpty { listOf(DemoData.searchResults.values.first()) }
        shown.forEach { friend ->
            SearchRow(friend, added.contains(friend.id)) { added = added + friend.id }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SearchRow(friend: Friend, isAdded: Boolean, onAdd: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Avatar(friend.gamertag, 44)
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    friend.gamertag,
                    style = MaterialTheme.typography.titleMedium,
                    color = EaColors.White,
                )
                Spacer(Modifier.width(6.dp))
                NetworkBadge(friend.network)
            }
            Text(friend.status, color = EaColors.Muted, style = MaterialTheme.typography.bodyMedium)
        }
        Box(
            Modifier
                .background(if (isAdded) EaColors.SurfaceHigh else EaColors.Blue, CircleShape)
                .size(36.dp)
                .clickable(enabled = !isAdded, onClick = onAdd),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                if (isAdded) Icons.Filled.Check else Icons.Filled.PersonAdd,
                if (isAdded) "Added" else "Add friend",
                tint = EaColors.White,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
