package com.ea.connect.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BackendExceptionTest {
    private val url = "http://10.0.2.2:3000/api/eaconnect/party/invite"

    @Test
    fun parsesBackendErrorResponse() {
        val exception = BackendException.fromResponse(
            500,
            """{"success":false,"error":"PartyBrokerUnavailable","message":"Cross-platform party broker is unavailable: cluster eac-party-xbl for network XBL is not registered","code":"PARTY_BROKER_UNAVAILABLE","requestId":"req-123"}""",
            url,
        )

        assertEquals(500, exception.statusCode)
        assertEquals("PartyBrokerUnavailable", exception.error)
        assertEquals("PARTY_BROKER_UNAVAILABLE", exception.code)
        assertEquals(
            "Cross-platform party broker is unavailable: cluster eac-party-xbl for network XBL is not registered",
            exception.backendMessage,
        )
        assertEquals("req-123", exception.requestId)
    }

    @Test
    fun handlesNonJsonResponse() {
        val exception = BackendException.fromResponse(500, "<html>Bad Gateway</html>", url)

        assertEquals(500, exception.statusCode)
        assertNull(exception.error)
        assertNull(exception.code)
        assertNull(exception.backendMessage)
        assertNull(exception.requestId)
    }

    @Test
    fun handlesNullRequestId() {
        val exception = BackendException.fromResponse(
            500,
            """{"error":"PartyBrokerUnavailable","requestId":null}""",
            url,
        )

        assertEquals("PartyBrokerUnavailable", exception.error)
        assertNull(exception.requestId)
    }
}
