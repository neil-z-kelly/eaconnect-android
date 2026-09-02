package com.ea.connect.ui

import com.ea.connect.data.BackendException
import java.net.SocketTimeoutException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class InviteFailureTest {

    @Test
    fun backendErrorBodyIsParsedIntoSupportDetails() {
        val details = InviteFailure.from(
            BackendException(
                500,
                """{"success":false,"error":"PartyBrokerUnavailable","message":"Cross-platform party broker is unavailable: cluster eac-party-xbl for network XBL is not registered","code":"PARTY_BROKER_UNAVAILABLE","requestId":"req-123"}""",
                "http://x/api",
            ),
        )

        assertEquals(500, details.statusCode)
        assertEquals("PartyBrokerUnavailable", details.errorName)
        assertEquals(
            "Cross-platform party broker is unavailable: cluster eac-party-xbl for network XBL is not registered",
            details.message,
        )
        assertEquals("PARTY_BROKER_UNAVAILABLE", details.code)
        assertEquals("req-123", details.requestId)
    }

    @Test
    fun nonJsonBodyYieldsNullDetailsButKeepsStatus() {
        val details = InviteFailure.from(BackendException(502, "<html>Bad Gateway</html>", "http://x/api"))

        assertEquals(502, details.statusCode)
        assertNull(details.errorName)
        assertNull(details.message)
        assertNull(details.code)
        assertNull(details.requestId)
    }

    @Test
    fun emptyBodyYieldsNullDetails() {
        val details = InviteFailure.from(BackendException(500, "", "http://x/api"))

        assertEquals(500, details.statusCode)
        assertNull(details.errorName)
        assertNull(details.message)
        assertNull(details.code)
        assertNull(details.requestId)
    }

    @Test
    fun networkFailureUsesExceptionNameAndMessage() {
        val details = InviteFailure.from(SocketTimeoutException("timeout"))

        assertNull(details.statusCode)
        assertEquals("SocketTimeoutException", details.errorName)
        assertEquals("timeout", details.message)
        assertNull(details.requestId)
    }
}
