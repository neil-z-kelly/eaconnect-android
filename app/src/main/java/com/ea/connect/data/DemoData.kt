package com.ea.connect.data

enum class Presence { ONLINE, BUSY, OFFLINE }

enum class Network(val label: String, val short: String) {
    EA("EA Account", "EA"),
    PSN("PlayStation Network", "PS"),
    XBL("Xbox Network", "XB"),
    PC("PC", "PC"),
    SWITCH("Nintendo Switch Online", "NS"),
}

data class Friend(
    val id: String,
    val gamertag: String,
    val handle: String,
    val network: Network,
    val presence: Presence,
    val status: String,
    val game: String?,
    val favorite: Boolean = false,
    val isNew: Boolean = false,
)

data class Message(
    val fromMe: Boolean,
    val text: String,
    val quick: Boolean,
)

data class Notification(
    val title: String,
    val detail: String,
    val ago: String,
)

data class Player(
    val gamertag: String,
    val accountId: String,
    val handle: String,
)

object DemoData {
    val player = Player(gamertag = "Neil_K", accountId = "EA-4471203", handle = "NeilKelly")

    val statusNotes = listOf("Add a Note", "\uD83D\uDC7E Invite me", "\uD83E\uDD1D Party Up", "Need a Squad", "Passing through")

    val friends = listOf(
        Friend(
            id = "shadow-ranger",
            gamertag = "ShadowRanger",
            handle = "Shadow_R",
            network = Network.PSN,
            presence = Presence.ONLINE,
            status = "Ultimate Team",
            game = "EA SPORTS FC 27",
            favorite = true,
        ),
        Friend(
            id = "vector-zero",
            gamertag = "VectorZero",
            handle = "Space_vector",
            network = Network.XBL,
            presence = Presence.ONLINE,
            status = "Breakthrough",
            game = "Battlefield 6",
            isNew = true,
        ),
        Friend(
            id = "ratio-line",
            gamertag = "RatioLine",
            handle = "GoldenRatio",
            network = Network.EA,
            presence = Presence.ONLINE,
            status = "Online",
            game = null,
        ),
        Friend(
            id = "echo-hollow",
            gamertag = "EchoHollow",
            handle = "HollowScope",
            network = Network.EA,
            presence = Presence.BUSY,
            status = "Busy",
            game = null,
        ),
        Friend(
            id = "silent-m",
            gamertag = "Silent_m",
            handle = "Silent_em",
            network = Network.EA,
            presence = Presence.OFFLINE,
            status = "Last online 2h ago",
            game = null,
        ),
        Friend(
            id = "quiet-dawn",
            gamertag = "QuietDawn",
            handle = "Dawn_q",
            network = Network.PSN,
            presence = Presence.OFFLINE,
            status = "Last online yesterday",
            game = null,
        ),
        Friend(
            id = "pixel-drift",
            gamertag = "PixelDrift",
            handle = "Drift_px",
            network = Network.PC,
            presence = Presence.OFFLINE,
            status = "Last online 3d ago",
            game = null,
        ),
        Friend(
            id = "north-gate",
            gamertag = "NorthGate",
            handle = "Gate_n",
            network = Network.SWITCH,
            presence = Presence.OFFLINE,
            status = "Last online 5d ago",
            game = null,
        ),
    )

    val onlineFriends = friends.filter { it.presence != Presence.OFFLINE }
    val offlineFriends = friends.filter { it.presence == Presence.OFFLINE }

    val conversation = listOf(
        Message(fromMe = false, text = "Play another?", quick = true),
        Message(fromMe = true, text = "GG", quick = true),
        Message(fromMe = true, text = "Sure, Let's try a different game mode this time? World of Chel?", quick = false),
        Message(fromMe = true, text = "What do you think?", quick = false),
        Message(fromMe = false, text = "Ready?", quick = true),
    )

    val quickReplies = listOf("I'm ready", "Be right there", "Let's go!")

    val searchResults = mapOf(
        "gr8one" to Friend(
            id = "gr8-one",
            gamertag = "Gr8One",
            handle = "Great1one",
            network = Network.XBL,
            presence = Presence.ONLINE,
            status = "2 mutual friends",
            game = null,
        ),
    )

    val notifications = listOf(
        Notification("VectorZero sent you a party invite", "Battlefield 6 — Breakthrough", "2m"),
        Notification("ShadowRanger is now online", "EA SPORTS FC 27 — Ultimate Team", "14m"),
        Notification("Gr8One accepted your friend request", "You now have 2 mutual friends", "1h"),
        Notification("RatioLine linked a PlayStation Network account", "Cross-platform friends are now visible", "yesterday"),
    )
}
