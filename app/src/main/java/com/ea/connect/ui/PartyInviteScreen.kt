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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ea.connect.data.Friend

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

            is PartyInviteState.Failure -> InviteUnavailableCard(
                failure = current.failure,
                onRetry = { viewModel.invite(friend, game) },
                onBackToFriends = {
                    viewModel.reset()
                    onBack()
                },
            )

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

@Composable
private fun SecondaryButton(label: String, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .border(1.dp, EaColors.Outline, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = EaColors.White, style = MaterialTheme.typography.labelLarge)
    }
}

/**
 * Graceful failure state: on-brand copy, a retry, a way back to the friends list, and the
 * backend's identifiers tucked away as support details.
 */
@Composable
private fun InviteUnavailableCard(
    failure: InviteFailure,
    onRetry: () -> Unit,
    onBackToFriends: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .background(EaColors.Surface, RoundedCornerShape(18.dp))
            .border(1.dp, EaColors.Outline, RoundedCornerShape(18.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .size(56.dp)
                .background(EaColors.SurfaceHigh, RoundedCornerShape(28.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.CloudOff, null, tint = EaColors.Away, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.height(14.dp))
        Text(
            failure.title,
            style = MaterialTheme.typography.titleLarge,
            color = EaColors.White,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            failure.body,
            style = MaterialTheme.typography.bodyMedium,
            color = EaColors.Muted,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(18.dp))
        PrimaryButton(label = "Retry", enabled = true, onClick = onRetry)
        Spacer(Modifier.height(10.dp))
        SecondaryButton(label = "Back to friends", onClick = onBackToFriends)

        if (failure.supportDetails.isNotEmpty()) {
            Spacer(Modifier.height(18.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(EaColors.Navy, RoundedCornerShape(10.dp))
                    .padding(12.dp),
            ) {
                Text(
                    "SUPPORT DETAILS",
                    color = EaColors.Muted,
                    style = MaterialTheme.typography.labelSmall,
                )
                Spacer(Modifier.height(6.dp))
                failure.supportDetails.forEach { (label, value) ->
                    Text(
                        "$label: $value",
                        color = EaColors.Muted.copy(alpha = 0.75f),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                    )
                }
            }
        }
    }
}
