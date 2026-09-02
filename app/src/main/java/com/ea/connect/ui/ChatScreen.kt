package com.ea.connect.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ea.connect.data.DemoData
import com.ea.connect.data.Friend
import com.ea.connect.data.Message

@Composable
fun InboxScreen(onOpen: (Friend) -> Unit) {
    LazyColumn(Modifier.fillMaxWidth()) {
        item {
            EaScreenHeader(
                "Chat",
                Modifier.padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 8.dp),
            )
        }
        items(DemoData.onlineFriends + DemoData.offlineFriends.take(2)) { friend ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onOpen(friend) }
                    .padding(horizontal = 20.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PresenceAvatar(friend, 44)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        friend.gamertag,
                        style = MaterialTheme.typography.titleMedium,
                        color = EaColors.White,
                    )
                    Text(
                        DemoData.conversation.last().text,
                        color = EaColors.Muted,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
fun ChatScreen(friend: Friend, onBack: () -> Unit, onPartyUp: () -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                "Back",
                tint = EaColors.White,
                modifier = Modifier.size(22.dp).clickable(onClick = onBack),
            )
            Spacer(Modifier.width(12.dp))
            PresenceAvatar(friend, 38)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    friend.gamertag,
                    style = MaterialTheme.typography.titleMedium,
                    color = EaColors.White,
                )
                Text(
                    friend.game ?: friend.status,
                    color = EaColors.Muted,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Box(
                Modifier
                    .size(36.dp)
                    .background(EaColors.Blue, CircleShape)
                    .clickable(onClick = onPartyUp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Groups, "Party Up", tint = EaColors.White, modifier = Modifier.size(18.dp))
            }
        }
        LazyColumn(Modifier.weight(1f)) {
            items(DemoData.conversation) { message -> Bubble(message) }
        }
        Row(
            Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            DemoData.quickReplies.forEach { reply -> Chip(reply) }
        }
    }
}

@Composable
private fun Bubble(message: Message) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = if (message.fromMe) Arrangement.End else Arrangement.Start,
    ) {
        Box(
            Modifier
                .background(
                    if (message.fromMe) EaColors.Blue else EaColors.Surface,
                    RoundedCornerShape(16.dp),
                )
                .then(
                    if (message.fromMe) Modifier
                    else Modifier.border(1.dp, EaColors.Outline, RoundedCornerShape(16.dp)),
                )
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Text(message.text, color = EaColors.White, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
