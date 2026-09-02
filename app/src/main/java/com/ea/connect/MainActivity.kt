package com.ea.connect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.PersonSearch
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ea.connect.data.Friend
import com.ea.connect.ui.ChatScreen
import com.ea.connect.ui.EaBackdrop
import com.ea.connect.ui.EaColors
import com.ea.connect.ui.EaConnectTheme
import com.ea.connect.ui.FindFriendsScreen
import com.ea.connect.ui.FriendsScreen
import com.ea.connect.ui.InboxScreen
import com.ea.connect.ui.NotificationsScreen
import com.ea.connect.ui.PartyInviteScreen

private enum class Tab(val label: String, val icon: ImageVector) {
    FRIENDS("Friends", Icons.Filled.PeopleAlt),
    CHAT("Chat", Icons.Filled.ChatBubbleOutline),
    FIND("Find", Icons.Filled.PersonSearch),
    ACTIVITY("Activity", Icons.Filled.NotificationsNone),
}

private sealed interface Route {
    data object Tabs : Route
    data class Chat(val friend: Friend) : Route
    data class Party(val friend: Friend) : Route
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EaConnectTheme { EaConnectApp() }
        }
    }
}

@Composable
private fun EaConnectApp() {
    var tab by remember { mutableStateOf(Tab.FRIENDS) }
    var route by remember { mutableStateOf<Route>(Route.Tabs) }

    Box(
        Modifier
            .fillMaxSize()
            .background(EaBackdrop),
    ) {
        Column(Modifier.fillMaxSize().systemBarsPadding()) {
            Box(Modifier.weight(1f)) {
                when (val current = route) {
                    is Route.Chat -> ChatScreen(
                        friend = current.friend,
                        onBack = { route = Route.Tabs },
                        onPartyUp = { route = Route.Party(current.friend) },
                    )

                    is Route.Party -> PartyInviteScreen(
                        friend = current.friend,
                        onBack = { route = Route.Tabs },
                    )

                    Route.Tabs -> when (tab) {
                        Tab.FRIENDS -> FriendsScreen(
                            onPartyUp = { route = Route.Party(it) },
                            onChat = { route = Route.Chat(it) },
                            onFindFriends = { tab = Tab.FIND },
                        )

                        Tab.CHAT -> InboxScreen(onOpen = { route = Route.Chat(it) })
                        Tab.FIND -> FindFriendsScreen()
                        Tab.ACTIVITY -> NotificationsScreen()
                    }
                }
            }
            if (route is Route.Tabs) {
                BottomBar(tab) { tab = it }
            }
        }
    }
}

@Composable
private fun BottomBar(selected: Tab, onSelect: (Tab) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(EaColors.Navy)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Tab.entries.forEach { tab ->
            val active = tab == selected
            Column(
                Modifier.clickable { onSelect(tab) }.padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    tab.icon,
                    tab.label,
                    tint = if (active) EaColors.Blue else EaColors.Muted,
                    modifier = Modifier.size(22.dp),
                )
                Text(
                    tab.label,
                    color = if (active) EaColors.Blue else EaColors.Muted,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}
