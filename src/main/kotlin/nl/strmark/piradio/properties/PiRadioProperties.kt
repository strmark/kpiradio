package nl.strmark.piradio.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "piradio")
data class PiRadioProperties(var volume: Int?)
