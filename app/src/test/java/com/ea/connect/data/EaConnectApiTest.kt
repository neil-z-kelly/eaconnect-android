package com.ea.connect.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EaConnectApiTest {
    @Test
    fun parseBackendError_mapsFullEnvelope() {
        val error = parseBackendError(
            500,
            """{"success":false,"error":"PartyBrokerUnavailable","message":"Cross-platform party broker is unavailable: cluster eac-party-xbl for network XBL is not registered","code":"PARTY_BROKER_UNAVAILABLE","requestId":"req-123"}""",
        )

        assertEquals(500, error.statusCode)
        assertEquals("PartyBrokerUnavailable", error.error)
        assertEquals(
            "Cross-platform party broker is unavailable: cluster eac-party-xbl for network XBL is not registered",
            error.message,
        )
        assertEquals("PARTY_BROKER_UNAVAILABLE", error.code)
        assertEquals("req-123", error.requestId)
    }

    @Test
    fun parseBackendError_handlesNonJsonBody() {
        val error = parseBackendError(502, "<html>502 Bad Gateway</html>")

        assertEquals(502, error.statusCode)
        assertEquals("HttpError", error.error)
        assertEquals("HTTP_502", error.code)
        assertNull(error.requestId)
        assertTrue(error.message.contains("502"))
    }

    @Test
    fun parseBackendError_handlesMissingRequestId() {
        val error = parseBackendError(
            500,
            """{"success":false,"error":"PartyBrokerUnavailable","message":"Unavailable","code":"PARTY_BROKER_UNAVAILABLE"}""",
        )

        assertNull(error.requestId)
    }
}
