package com.ea.connect.ui

import com.ea.connect.data.BackendException
import org.json.JSONObject
import java.io.IOException

/** What the player sees when an invite fails, plus the backend fields kept for support. */
data class PartyInviteFailure(
    val title: String,
    val body: String,
    val statusCode: Int?,
    val error: String?,
    val message: String?,
    val requestId: String?,
) {
    /** Label/value pairs for the subdued support block; only fields the backend actually returned. */
    val supportDetails: List<Pair<String, String>>
        get() = listOfNotNull(
            error?.let { "Error" to it },
            message?.let { "Message" to it },
            requestId?.let { "Request ID" to it },
            statusCode?.let { "Status" to "HTTP $it" },
        )

    companion object {
        const val UNAVAILABLE_TITLE = "Party invites are taking a breather"
        const val CONNECTION_TITLE = "Couldn't reach EA Connect"

        fun from(throwable: Throwable, gamertag: String): PartyInviteFailure = when (throwable) {
            is BackendException -> {
                val fields = parseBody(throwable.rawBody)
                PartyInviteFailure(
                    title = UNAVAILABLE_TITLE,
                    body = "Party invites are temporarily unavailable, so we couldn't send yours to $gamertag. " +
                        "Nothing's wrong on your end — give it another go in a moment.",
                    statusCode = throwable.statusCode,
                    error = fields["error"],
                    message = fields["message"],
                    requestId = fields["requestId"],
                )
            }
            is IOException -> PartyInviteFailure(
                title = CONNECTION_TITLE,
                body = "We couldn't send your invite to $gamertag. Check your connection and try again.",
                statusCode = null, error = null, message = null, requestId = null,
            )
            else -> PartyInviteFailure(
                title = UNAVAILABLE_TITLE,
                body = "Something went wrong sending your invite to $gamertag. Give it another go in a moment.",
                statusCode = null, error = null, message = null, requestId = null,
            )
        }

        private fun parseBody(rawBody: String): Map<String, String> = try {
            val obj = JSONObject(rawBody)
            listOf("error", "message", "requestId")
                .mapNotNull { key -> obj.optString(key, "").takeIf { it.isNotBlank() }?.let { key to it } }
                .toMap()
        } catch (_: Exception) {
            emptyMap()
        }
    }
}
