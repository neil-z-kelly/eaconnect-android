package com.ea.connect.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ea.connect.data.BackendException
import com.ea.connect.data.DemoData
import com.ea.connect.data.EaConnectApi
import com.ea.connect.data.Friend
import com.ea.connect.data.PartyInviteResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

/** Subdued diagnostics shown under the friendly error so support can correlate the failure. */
data class SupportDetails(val error: String, val message: String, val requestId: String?) {
    companion object {
        fun from(t: Throwable): SupportDetails = when (t) {
            is BackendException -> SupportDetails(t.error.error, t.error.message, t.error.requestId)
            is IOException -> SupportDetails("NetworkError", t.message ?: "Could not reach EA Connect.", null)
            else -> SupportDetails(t.javaClass.simpleName, t.message ?: "Unexpected error.", null)
        }
    }
}

sealed interface PartyInviteState {
    data object Idle : PartyInviteState
    data object Loading : PartyInviteState
    data class Sent(val result: PartyInviteResult) : PartyInviteState
    data class Failure(val details: SupportDetails) : PartyInviteState
}

class PartyInviteViewModel(private val api: EaConnectApi = EaConnectApi()) : ViewModel() {

    private val _state = MutableStateFlow<PartyInviteState>(PartyInviteState.Idle)
    val state: StateFlow<PartyInviteState> = _state
    private var job: Job? = null

    fun invite(friend: Friend, game: String) {
        job?.cancel()
        _state.value = PartyInviteState.Loading
        job = viewModelScope.launch {
            _state.value = try {
                val result = withContext(Dispatchers.IO) {
                    api.sendPartyInvite(DemoData.player.accountId, friend, game)
                }
                PartyInviteState.Sent(result)
            } catch (t: Throwable) {
                if (t is CancellationException) throw t
                PartyInviteState.Failure(SupportDetails.from(t))
            }
        }
    }

    fun reset() {
        job?.cancel()
        job = null
        _state.value = PartyInviteState.Idle
    }
}
