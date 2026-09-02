package com.ea.connect.ui

import com.ea.connect.data.BackendError
import com.ea.connect.data.BackendException
import java.net.ConnectException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SupportDetailsTest {
    @Test
    fun from_mapsBackendException() {
        val details = SupportDetails.from(
            BackendException(
                BackendError(500, "PartyBrokerUnavailable", "Unavailable", "PARTY_BROKER_UNAVAILABLE", "req-123"),
                "http://localhost",
            ),
        )

        assertEquals("PartyBrokerUnavailable", details.error)
        assertEquals("Unavailable", details.message)
        assertEquals("req-123", details.requestId)
    }

    @Test
    fun from_mapsConnectException() {
        val details = SupportDetails.from(ConnectException("Failed to connect"))

        assertEquals("NetworkError", details.error)
        assertEquals("Failed to connect", details.message)
        assertNull(details.requestId)
    }

    @Test
    fun from_mapsUnexpectedException() {
        val details = SupportDetails.from(IllegalStateException("boom"))

        assertEquals("IllegalStateException", details.error)
        assertEquals("boom", details.message)
        assertNull(details.requestId)
    }
}
