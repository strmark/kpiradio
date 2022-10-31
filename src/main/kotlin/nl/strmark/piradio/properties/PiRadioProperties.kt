package nl.strmark.piradio.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "piradio")
data class PiRadioProperties(
    var volume: Float = 100f,
    var device: String = "Master",
    var vlc: Vlc = Vlc()
) {
    data class Vlc(var player: Player = Player()) {
        data class Player(
            var path: String = "/usr/bin/cvlc"
        )
    }
}
