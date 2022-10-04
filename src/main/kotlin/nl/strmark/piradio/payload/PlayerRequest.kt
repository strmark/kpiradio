package nl.strmark.piradio.payload

data class PlayerRequest(
        var status: String,
        var webRadio: Int,
        var autoStopMinutes: Int
)
