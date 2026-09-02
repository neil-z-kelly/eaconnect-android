package com.ea.connect.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BackendErrorTest {

    @Test
    fun `parses the party broker 500 body from the invite endpoint`() {
        val body = """
            {"success":false,"error":"PartyBrokerUnavailable",
             "message":"Cross-platform party broker is unavailable: cluster eac-party-xbl for network XBL is not registered",
             "code":"PARTY_BROKER_UNAVAILABLE","requestId":"req-7f3a"}
        """.trimIndent()

        val parsed = BackendError.parse(500, body)

        assertEquals(500, parsed.statusCode)
        assertEquals("PartyBrokerUnavailable", parsed.error)
        assertEquals(
            "Cross-platform party broker is unavailable: cluster eac-party-xbl for network XBL is not registered",
            parsed.message,
        )
        assertEquals("PARTY_BROKER_UNAVAILABLE", parsed.code)
        assertEquals("req-7f3a", parsed.requestId)
    }

    @Test
    fun `tolerates a non-JSON body from a gateway`() {
        val parsed = BackendError.parse(502, "<html>Bad Gateway</html>")

        assertEquals(502, parsed.statusCode)
        assertNull(parsed.error)
        assertNull(parsed.message)
        assertNull(parsed.code)
        assertNull(parsed.requestId)
    }

    @Test
    fun `treats missing, null and blank fields as absent`() {
        val parsed = BackendError.parse(500, """{"error":"PartyBrokerUnavailable","message":null,"requestId":"  "}""")

        assertEquals("PartyBrokerUnavailable", parsed.error)
        assertNull(parsed.message)
        assertNull(parsed.code)
        assertNull(parsed.requestId)
    }

    @Test
    fun `tolerates an empty body`() {
        val parsed = BackendError.parse(500, "")

        assertEquals(500, parsed.statusCode)
        assertNull(parsed.error)
    }
}
