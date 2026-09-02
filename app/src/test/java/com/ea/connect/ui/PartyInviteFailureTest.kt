package com.ea.connect.ui

import com.ea.connect.data.BackendException
import java.net.ConnectException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PartyInviteFailureTest {

    @Test
    fun backend500ExposesSupportFieldsAndFriendlyCopy() {
        val failure = PartyInviteFailure.from(
            BackendException(
                500,
                """{"success":false,"error":"PartyBrokerUnavailable","message":"Cross-platform party broker is unavailable: cluster eac-party-xbl for network XBL is not registered","code":"PARTY_BROKER_UNAVAILABLE","requestId":"82fe7aa1-7eea-49f3-91e3-8342f3ba16aa"}""",
                "http://x/api/eaconnect/party/invite",
            ),
            "VectorZero",
        )

        assertEquals(PartyInviteFailure.UNAVAILABLE_TITLE, failure.title)
        assertTrue(failure.body.contains("VectorZero"))
        assertTrue(failure.body.contains("temporarily unavailable"))
        assertEquals("PartyBrokerUnavailable", failure.error)
        assertEquals(
            "Cross-platform party broker is unavailable: cluster eac-party-xbl for network XBL is not registered",
            failure.message,
        )
        assertEquals("82fe7aa1-7eea-49f3-91e3-8342f3ba16aa", failure.requestId)
        assertEquals(500, failure.statusCode)
        assertEquals(4, failure.supportDetails.size)
        assertFalse(failure.title.contains("Exception"))
        assertFalse(failure.body.contains("Exception"))
        assertFalse(failure.title.contains("HTTP"))
        assertFalse(failure.body.contains("HTTP"))
        assertFalse(failure.title.contains("at com."))
        assertFalse(failure.body.contains("at com."))
    }

    @Test
    fun nonJsonBodyStillProducesFriendlyState() {
        val failure = PartyInviteFailure.from(
            BackendException(502, "<html>Bad Gateway</html>", "http://x/api/eaconnect/party/invite"),
            "VectorZero",
        )

        assertNull(failure.error)
        assertNull(failure.message)
        assertNull(failure.requestId)
        assertEquals(502, failure.statusCode)
        assertEquals(listOf("Status" to "HTTP 502"), failure.supportDetails)
        assertEquals(PartyInviteFailure.UNAVAILABLE_TITLE, failure.title)
    }

    @Test
    fun emptyBodyHandled() {
        val failure = PartyInviteFailure.from(
            BackendException(500, "", "http://x/api/eaconnect/party/invite"),
            "VectorZero",
        )

        assertNull(failure.error)
        assertNull(failure.message)
        assertNull(failure.requestId)
        assertEquals(500, failure.statusCode)
    }

    @Test
    fun networkFailureAsksToCheckConnection() {
        val failure = PartyInviteFailure.from(ConnectException("Failed to connect"), "VectorZero")

        assertEquals(PartyInviteFailure.CONNECTION_TITLE, failure.title)
        assertTrue(failure.body.contains("connection"))
        assertTrue(failure.supportDetails.isEmpty())
    }

    @Test
    fun unexpectedThrowableFallsBackToGenericCopy() {
        val failure = PartyInviteFailure.from(IllegalStateException("boom"), "VectorZero")

        assertEquals(PartyInviteFailure.UNAVAILABLE_TITLE, failure.title)
        assertTrue(failure.supportDetails.isEmpty())
        assertFalse(failure.body.contains("boom"))
    }
}
