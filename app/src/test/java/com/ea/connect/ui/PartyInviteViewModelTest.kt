package com.ea.connect.ui

import com.ea.connect.data.BackendError
import com.ea.connect.data.BackendException
import com.ea.connect.data.Friend
import com.ea.connect.data.Network
import com.ea.connect.data.Presence
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.net.UnknownHostException

class PartyInviteViewModelTest {

    private val xboxFriend = Friend(
        id = "vector-zero",
        gamertag = "VectorZero",
        handle = "VZ",
        network = Network.XBL,
        presence = Presence.ONLINE,
        status = "Squad up",
        game = "Battlefield 6",
    )

    private val brokerOutage = BackendException(
        BackendError(
            statusCode = 500,
            error = "PartyBrokerUnavailable",
            message = "Cross-platform party broker is unavailable: cluster eac-party-xbl for network XBL is not registered",
            code = "PARTY_BROKER_UNAVAILABLE",
            requestId = "req-7f3a",
        ),
        url = "http://10.0.2.2:3000/api/eaconnect/party/invite",
    )

    @Test
    fun `a broker outage becomes a friendly temporarily-unavailable message`() {
        val failure = PartyInviteViewModel.describeFailure(brokerOutage, xboxFriend)

        assertEquals("Party invites are taking a breather", failure.title)
        assertTrue(failure.body.contains("Xbox Network"))
        assertFalse(failure.title.contains("Exception"))
        assertFalse(failure.body.contains("PartyBrokerUnavailable"))
    }

    @Test
    fun `backend error, message and requestId are kept as support details`() {
        val details = PartyInviteViewModel.describeFailure(brokerOutage, xboxFriend).supportDetails.toMap()

        assertEquals("PartyBrokerUnavailable", details["Error"])
        assertEquals(
            "Cross-platform party broker is unavailable: cluster eac-party-xbl for network XBL is not registered",
            details["Message"],
        )
        assertEquals("req-7f3a", details["Request ID"])
        assertEquals("500", details["Status"])
    }

    @Test
    fun `support details omit fields the backend did not send`() {
        val sparse = BackendException(BackendError(502, null, null, null, null), url = "http://10.0.2.2:3000/x")

        val details = PartyInviteViewModel.describeFailure(sparse, xboxFriend).supportDetails.toMap()

        assertEquals(setOf("Status"), details.keys)
        assertEquals("502", details["Status"])
    }

    @Test
    fun `a 4xx reads as a problem with the invite rather than an outage`() {
        val rejected = BackendException(
            BackendError(409, "FriendUnavailable", "VectorZero is busy and cannot be invited right now.", "FRIEND_UNAVAILABLE", "req-1"),
            url = "http://10.0.2.2:3000/x",
        )

        val failure = PartyInviteViewModel.describeFailure(rejected, xboxFriend)

        assertEquals("Couldn't send that invite", failure.title)
        assertEquals("FriendUnavailable", failure.supportDetails.toMap()["Error"])
    }

    @Test
    fun `a network failure reads as offline`() {
        val failure = PartyInviteViewModel.describeFailure(UnknownHostException("10.0.2.2"), xboxFriend)

        assertEquals("You're offline", failure.title)
        assertEquals("10.0.2.2", failure.supportDetails.toMap()["Message"])
    }

    @Test
    fun `an unexpected exception never leaks its stack trace into the copy`() {
        val failure = PartyInviteViewModel.describeFailure(IllegalStateException("boom"), xboxFriend)

        assertEquals("Something went wrong", failure.title)
        assertFalse(failure.body.contains("IllegalStateException"))
        assertEquals("IllegalStateException", failure.supportDetails.toMap()["Error"])
    }

    @Test
    fun `an IOException with no message yields no support details`() {
        val failure = PartyInviteViewModel.describeFailure(IOException(), xboxFriend)

        assertTrue(failure.supportDetails.isEmpty())
    }
}
