package com.ea.connect.ui

import com.ea.connect.data.BackendException
import java.io.IOException

data class SupportDetail(val label: String, val value: String)

data class PartyInviteFailure(
    val title: String,
    val body: String,
    val supportDetails: List<SupportDetail>,
) {
    companion object {
        fun from(error: Throwable, networkLabel: String): PartyInviteFailure = when {
            error is BackendException && error.statusCode >= 500 -> PartyInviteFailure(
                title = "Party invites are temporarily unavailable",
                body = "We couldn't reach the party service for $networkLabel right now. Your friend hasn't been notified — give it a moment and try again.",
                supportDetails = backendDetails(error),
            )
            error is BackendException -> PartyInviteFailure(
                title = "We couldn't send that invite",
                body = error.backendMessage ?: "Please try again.",
                supportDetails = backendDetails(error),
            )
            error is IOException -> PartyInviteFailure(
                title = "Can't reach EA Connect",
                body = "Check your connection and try again.",
                supportDetails = genericDetails(error),
            )
            else -> PartyInviteFailure(
                title = "Something went wrong",
                body = "Give it a moment and try again.",
                supportDetails = genericDetails(error),
            )
        }

        private fun backendDetails(e: BackendException) = listOfNotNull(
            e.error?.let { SupportDetail("Error", it) },
            e.backendMessage?.let { SupportDetail("Message", it) },
            e.requestId?.let { SupportDetail("Request ID", it) },
            SupportDetail("Status", "HTTP ${e.statusCode}"),
        )

        private fun genericDetails(e: Throwable) = listOfNotNull(
            SupportDetail("Error", e.javaClass.simpleName),
            e.message?.let { SupportDetail("Message", it) },
        )
    }
}
