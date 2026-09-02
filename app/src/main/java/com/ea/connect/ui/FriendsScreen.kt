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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ea.connect.data.DemoData
import com.ea.connect.data.Friend
import com.ea.connect.data.Presence

@Composable
fun FriendsScreen(
    onPartyUp: (Friend) -> Unit,
    onChat: (Friend) -> Unit,
    onFindFriends: () -> Unit,
) {
    LazyColumn(Modifier.fillMaxWidth()) {
        item { FriendsHeader() }
        item { StatusRow() }
        item { NotesRow() }
        item { SectionHeader("Online", DemoData.onlineFriends.size) }
        items(DemoData.onlineFriends) { friend ->
            FriendRow(friend, onPartyUp = { onPartyUp(friend) }, onChat = { onChat(friend) })
        }
        item { FindFriendsBanner(onFindFriends) }
        item { SectionHeader("Offline", DemoData.offlineFriends.size) }
        items(DemoData.offlineFriends) { friend ->
            FriendRow(friend, onPartyUp = { onPartyUp(friend) }, onChat = { onChat(friend) })
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun FriendsHeader() {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EaScreenHeader("Friends", Modifier.weight(1f))
        Icon(Icons.Filled.Settings, null, tint = EaColors.Muted, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(14.dp))
        Avatar(DemoData.player.gamertag, 36)
    }
}

@Composable
private fun StatusRow() {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(DemoData.player.gamertag, 46)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                DemoData.player.gamertag,
                style = MaterialTheme.typography.titleMedium,
                color = EaColors.White,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(7.dp).background(EaColors.Online, CircleShape))
                Spacer(Modifier.width(6.dp))
                Text("Online", color = EaColors.Muted, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun NotesRow() {
    Row(
        Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        DemoData.statusNotes.forEachIndexed { index, note ->
            Chip(note, selected = index == 2)
        }
    }
}

@Composable
private fun FriendRow(friend: Friend, onPartyUp: () -> Unit, onChat: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PresenceAvatar(friend, 44)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    friend.gamertag,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (friend.presence == Presence.OFFLINE) EaColors.Muted else EaColors.White,
                )
                Spacer(Modifier.width(6.dp))
                NetworkBadge(friend.network)
                if (friend.isNew) {
                    Spacer(Modifier.width(6.dp))
                    Box(
                        Modifier
                            .background(EaColors.Blue, RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 1.dp),
                    ) {
                        Text("NEW", color = EaColors.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Text(
                friend.game?.let { "$it • ${friend.status}" } ?: friend.status,
                color = EaColors.Muted,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        if (friend.presence != Presence.OFFLINE) {
            IconAction(Icons.Filled.Groups, "Party Up", onPartyUp)
            Spacer(Modifier.width(8.dp))
        }
        IconAction(Icons.Filled.ChatBubbleOutline, "Chat", onChat)
    }
}

@Composable
private fun IconAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    onClick: () -> Unit,
) {
    Box(
        Modifier
            .size(36.dp)
            .background(EaColors.SurfaceHigh, CircleShape)
            .border(1.dp, EaColors.Outline, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, description, tint = EaColors.White, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun FindFriendsBanner(onFindFriends: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .background(EaColors.Surface, RoundedCornerShape(14.dp))
            .border(1.dp, EaColors.Outline, RoundedCornerShape(14.dp))
            .clickable(onClick = onFindFriends)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                "Don't see all your friends?",
                style = MaterialTheme.typography.titleMedium,
                color = EaColors.White,
            )
            Text(
                "Link your platform accounts to see everyone in one place.",
                color = EaColors.Muted,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Spacer(Modifier.width(12.dp))
        Box(
            Modifier
                .size(34.dp)
                .background(EaColors.Blue, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Add, "Find friends", tint = EaColors.White, modifier = Modifier.size(18.dp))
        }
    }
}
