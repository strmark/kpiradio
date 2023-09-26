package nl.strmark.piradio.util

import mu.KotlinLogging
import nl.strmark.piradio.properties.PiRadioProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit.MINUTES

@Component
class VlcPlayer {

    @Autowired
    private lateinit var piRadioProperties: PiRadioProperties

    private var vlcPlayerProcess: Process? = null

    fun open(url: String, autoStopMinutes: Long) {
        logger.info("url; $url")
        when {
            vlcPlayerProcess != null -> {
                stopVlcPlayer()
                open(url, autoStopMinutes)
            }
            else -> {
                // start VlcPlayer as an external process
                val command = arrayOf(piRadioProperties.vlc.player.path, url)
                logger.info { "Starting VlcPlayer process: ${command.joinToString { " " }}" }
                vlcPlayerProcess = Runtime.getRuntime().exec(command)
                when {
                    autoStopMinutes > 0 && vlcPlayerProcess?.waitFor(autoStopMinutes, MINUTES) == false -> stopVlcPlayer()
                }
            }
        }
        logger.info { "Started playing $url" }
    }

    fun stopVlcPlayer() {
        vlcPlayerProcess?.destroy()
        vlcPlayerProcess = null
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
