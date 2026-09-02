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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ea.connect.data.Friend
import java.io.PrintWriter
import java.io.StringWriter

@Composable
fun PartyInviteScreen(friend: Friend, onBack: () -> Unit) {
    val viewModel: PartyInviteViewModel = viewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val game = remember(friend) { friend.game ?: "Battlefield 6" }

    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                "Back",
                tint = EaColors.White,
                modifier = Modifier.size(22.dp).clickable(onClick = onBack),
            )
            Spacer(Modifier.width(12.dp))
            Text("Party Up", style = MaterialTheme.typography.titleLarge, color = EaColors.White)
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .background(EaColors.Surface, RoundedCornerShape(18.dp))
                .border(1.dp, EaColors.Outline, RoundedCornerShape(18.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PresenceAvatar(friend, 72)
            Spacer(Modifier.height(12.dp))
            Text(friend.gamertag, style = MaterialTheme.typography.titleLarge, color = EaColors.White)
            Text(
                "${friend.network.label} • $game",
                color = EaColors.Muted,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(18.dp))
            PrimaryButton(
                label = if (state is PartyInviteState.Loading) "Sending invite…" else "Send party invite",
                enabled = state !is PartyInviteState.Loading,
            ) { viewModel.invite(friend, game) }
        }

        Spacer(Modifier.height(16.dp))

        when (val current = state) {
            is PartyInviteState.Loading -> Row(
                Modifier.fillMaxWidth().padding(20.dp),
                horizontalArrangement = Arrangement.Center,
            ) { CircularProgressIndicator(color = EaColors.Blue) }

            is PartyInviteState.Sent -> Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .background(EaColors.Surface, RoundedCornerShape(16.dp))
                    .padding(18.dp),
            ) {
                Text("Invite sent", style = MaterialTheme.typography.titleMedium, color = EaColors.Online)
                Text(
                    "Party ${current.result.partyId} • expires in ${current.result.expiresInSeconds}s",
                    color = EaColors.Muted,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            is PartyInviteState.Failure -> RawErrorDump(current.error)

            PartyInviteState.Idle -> Unit
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun PrimaryButton(label: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(if (enabled) EaColors.Blue else EaColors.SurfaceHigh, RoundedCornerShape(24.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = EaColors.White, style = MaterialTheme.typography.labelLarge)
    }
}

/**
 * Deliberately raw failure surface: exception class, message and stack trace straight on screen.
 */
@Composable
private fun RawErrorDump(error: Throwable) {
    val stackTrace = remember(error) {
        StringWriter().also { writer -> error.printStackTrace(PrintWriter(writer)) }.toString()
    }
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .background(Color(0xFF2A0E0C), RoundedCornerShape(6.dp))
            .border(1.dp, Color(0xFF7A1F19), RoundedCornerShape(6.dp))
            .padding(10.dp),
    ) {
        Text(
            "Unhandled exception in PartyInviteViewModel.invite()",
            color = Color(0xFFFF7A6B),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            error.javaClass.name,
            color = Color(0xFFFFD3CC),
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            error.message ?: "null",
            color = Color(0xFFFFD3CC),
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
        )
        Spacer(Modifier.height(8.dp))
        Box(Modifier.horizontalScroll(rememberScrollState())) {
            Text(
                stackTrace,
                color = Color(0xFFC9A9A4),
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
            )
        }
    }
}
