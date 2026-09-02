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

sealed interface PartyInviteState {
    data object Idle : PartyInviteState
    data object Loading : PartyInviteState
    data class Sent(val result: PartyInviteResult) : PartyInviteState
    data class Failure(val details: InviteFailure) : PartyInviteState
}

data class InviteFailure(
    val statusCode: Int?,
    val errorName: String?,
    val message: String?,
    val code: String?,
    val requestId: String?,
) {
    companion object {
        fun from(t: Throwable): InviteFailure = when (t) {
            is BackendException -> InviteFailure(t.statusCode, t.errorName, t.errorMessage, t.errorCode, t.requestId)
            else -> InviteFailure(null, t.javaClass.simpleName, t.message, null, null)
        }
    }
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
                PartyInviteState.Failure(InviteFailure.from(t))
            }
        }
    }

    fun reset() {
        _state.value = PartyInviteState.Idle
    }
}
