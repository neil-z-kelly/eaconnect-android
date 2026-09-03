package com.ea.connect.ui

import com.ea.connect.data.BackendException
import java.net.SocketTimeoutException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PartyInviteFailureTest {
    @Test
    fun mapsServerFailureToUnavailableState() {
        val failure = PartyInviteFailure.from(
            BackendException(
                statusCode = 500,
                error = "PartyBrokerUnavailable",
                code = "PARTY_BROKER_UNAVAILABLE",
                backendMessage = "Cross-platform party broker is unavailable",
                requestId = "req-123",
                url = "http://localhost/invite",
            ),
            "Xbox Network",
        )

        assertEquals("Party invites are temporarily unavailable", failure.title)
        assertTrue(failure.body.contains("Xbox Network"))
        assertEquals(
            listOf("Error", "Message", "Request ID", "Status"),
            failure.supportDetails.map { it.label },
        )
        assertTrue(failure.supportDetails.none { it.value.contains("at com.") || it.value.contains('\n') })
    }

    @Test
    fun mapsClientBackendFailureToMessage() {
        val failure = PartyInviteFailure.from(
            BackendException(
                statusCode = 409,
                error = "FriendBusy",
                code = "FRIEND_BUSY",
                backendMessage = "VectorZero is busy…",
                requestId = null,
                url = "http://localhost/invite",
            ),
            "Xbox Network",
        )

        assertEquals("We couldn't send that invite", failure.title)
        assertEquals("VectorZero is busy…", failure.body)
    }

    @Test
    fun mapsTimeoutToConnectionState() {
        val failure = PartyInviteFailure.from(SocketTimeoutException("timed out"), "Xbox Network")

        assertEquals("Can't reach EA Connect", failure.title)
    }

    @Test
    fun mapsUnexpectedFailureToGenericState() {
        val failure = PartyInviteFailure.from(IllegalStateException("boom"), "Xbox Network")

        assertEquals("Something went wrong", failure.title)
        assertEquals("IllegalStateException", failure.supportDetails[0].value)
        assertEquals("boom", failure.supportDetails[1].value)
    }

    @Test
    fun omitsEmptyBackendDetails() {
        val failure = PartyInviteFailure.from(
            BackendException(
                statusCode = 500,
                error = null,
                code = null,
                backendMessage = null,
                requestId = null,
                url = "http://localhost/invite",
            ),
            "Xbox Network",
        )

        assertEquals(listOf(SupportDetail("Status", "HTTP 500")), failure.supportDetails)
    }
}
