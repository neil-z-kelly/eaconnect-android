package com.ea.connect.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ea.connect.R
import com.ea.connect.data.Friend
import com.ea.connect.data.Network
import com.ea.connect.data.Presence

private val AvatarPalettes = listOf(
    listOf(Color(0xFF4A5BA6), Color(0xFF232C57)),
    listOf(Color(0xFF7A4BC6), Color(0xFF2B1F55)),
    listOf(Color(0xFFC64B8A), Color(0xFF55203C)),
    listOf(Color(0xFF4BA678), Color(0xFF1F5540)),
    listOf(Color(0xFFC6884B), Color(0xFF553B1F)),
    listOf(Color(0xFF4B8FC6), Color(0xFF1F3E55)),
)

/** Screen header: the EA mark followed by the screen title. */
@Composable
fun EaScreenHeader(title: String, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(R.drawable.ic_ea_mark),
            contentDescription = "EA",
            modifier = Modifier.size(32.dp),
        )
        Text(
            title,
            style = MaterialTheme.typography.headlineMedium,
            color = EaColors.White,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

fun presenceColor(presence: Presence): Color = when (presence) {
    Presence.ONLINE -> EaColors.Online
    Presence.BUSY -> EaColors.Busy
    Presence.OFFLINE -> EaColors.Muted
}

@Composable
fun Avatar(seed: String, size: Int, modifier: Modifier = Modifier) {
    val palette = AvatarPalettes[Math.floorMod(seed.hashCode(), AvatarPalettes.size)]
    Box(
        modifier
            .size(size.dp)
            .background(Brush.linearGradient(palette), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            seed.take(2).uppercase(),
            color = EaColors.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size / 2.6).sp,
        )
    }
}

@Composable
fun PresenceAvatar(friend: Friend, size: Int, modifier: Modifier = Modifier) {
    Box(modifier) {
        Avatar(friend.gamertag, size)
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .size((size / 4).dp)
                .background(EaColors.Midnight, CircleShape)
                .padding(2.dp)
                .background(presenceColor(friend.presence), CircleShape),
        )
    }
}

@Composable
fun NetworkBadge(network: Network) {
    Box(
        Modifier
            .size(14.dp)
            .background(EaColors.SurfaceHigh, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(network.short, color = EaColors.Muted, fontSize = 7.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun Chip(label: String, selected: Boolean = false, modifier: Modifier = Modifier) {
    Box(
        modifier
            .background(
                if (selected) EaColors.Blue else EaColors.SurfaceHigh,
                RoundedCornerShape(18.dp),
            )
            .border(1.dp, EaColors.Outline, RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(label, color = EaColors.White, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun SectionHeader(title: String, count: Int) {
    Row(
        Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = EaColors.White)
        Box(
            Modifier
                .background(EaColors.SurfaceHigh, RoundedCornerShape(6.dp))
                .padding(horizontal = 7.dp, vertical = 2.dp),
        ) {
            Text("$count", color = EaColors.Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}
