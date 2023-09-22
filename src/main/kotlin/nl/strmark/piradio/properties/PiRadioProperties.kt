package nl.strmark.piradio.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "piradio")
data class PiRadioProperties(
    var vlc: Vlc = Vlc(),
    var amixer: Amixer = Amixer()
) {

    data class Vlc(var player: Player = Player()) {
        data class Player(
            var path: String = "/usr/bin/cvlc"
        )
    }

    data class Amixer(
        val steps: String = "2%",
        var device: String = "Master",
        var amixer: String = "/usr/bin/amixer"
    )
}
