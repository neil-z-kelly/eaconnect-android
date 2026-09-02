package com.ea.connect.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ea.connect.data.BackendException
import com.ea.connect.data.DemoData
import com.ea.connect.data.EaConnectApi
import com.ea.connect.data.Friend
import com.ea.connect.data.PartyInviteResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Player-facing description of a failed invite: friendly copy up top, and the
 * backend's identifiers kept aside for a support conversation.
 */
data class InviteFailure(
    val title: String,
    val body: String,
    val supportDetails: List<Pair<String, String>>,
)

sealed interface PartyInviteState {
    data object Idle : PartyInviteState
    data object Loading : PartyInviteState
    data class Sent(val result: PartyInviteResult) : PartyInviteState
    data class Failure(val failure: InviteFailure) : PartyInviteState
}

class PartyInviteViewModel(private val api: EaConnectApi = EaConnectApi()) : ViewModel() {

    private val _state = MutableStateFlow<PartyInviteState>(PartyInviteState.Idle)
    val state: StateFlow<PartyInviteState> = _state

    fun invite(friend: Friend, game: String) {
        _state.value = PartyInviteState.Loading
        viewModelScope.launch {
            _state.value = try {
                val result = withContext(Dispatchers.IO) {
                    api.sendPartyInvite(DemoData.player.accountId, friend, game)
                }
                PartyInviteState.Sent(result)
            } catch (t: Throwable) {
                PartyInviteState.Failure(describeFailure(t, friend))
            }
        }
    }

    fun reset() {
        _state.value = PartyInviteState.Idle
    }

    companion object {
        fun describeFailure(error: Throwable, friend: Friend): InviteFailure = when (error) {
            is BackendException -> {
                val details = error.details
                val serverSide = details.statusCode >= 500
                InviteFailure(
                    title = if (serverSide) "Party invites are taking a breather" else "Couldn't send that invite",
                    body = if (serverSide) {
                        "We can't reach the party service for ${friend.network.label} right now. " +
                            "Your squad is safe — give it another go in a moment, or head back to your friends list."
                    } else {
                        "Something about this invite didn't check out. Try again, or pick another friend from your list."
                    },
                    supportDetails = listOfNotNull(
                        details.error?.let { "Error" to it },
                        details.message?.let { "Message" to it },
                        details.requestId?.let { "Request ID" to it },
                        "Status" to details.statusCode.toString(),
                    ),
                )
            }

            is IOException -> InviteFailure(
                title = "You're offline",
                body = "We couldn't reach EA Connect. Check your connection and try again.",
                supportDetails = listOfNotNull(error.message?.let { "Message" to it }),
            )

            else -> InviteFailure(
                title = "Something went wrong",
                body = "That didn't work. Give it another go, or head back to your friends list.",
                supportDetails = listOfNotNull(
                    "Error" to error.javaClass.simpleName,
                    error.message?.let { "Message" to it },
                ),
            )
        }
    }
}
