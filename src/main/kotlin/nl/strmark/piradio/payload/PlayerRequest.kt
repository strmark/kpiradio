package nl.strmark.piradio.payload

data class PlayerRequest (
    var status: String,
    var webradio: Int,
    var autoStopMinutes: Int
)