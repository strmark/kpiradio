package nl.strmark.piradio.util

import mu.KotlinLogging
import nl.strmark.piradio.properties.PiRadioProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class VlcPlayer {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @Autowired
    private lateinit var piRadioProperties: PiRadioProperties

    private var vlcPlayerProcess: Process? = null

    fun open(url: String, autoStopMinutes: Long) {
        when (vlcPlayerProcess != null) {
            true -> {
                stopVlcPlayer()
                open(url, autoStopMinutes)
            }
            else -> {
                // start VlcPlayer as an external process
                val command = "${piRadioProperties.vlc.player.path} $url"
                logger.info { "Starting VlcPlayer process: $command" }
                vlcPlayerProcess = Runtime.getRuntime().exec(command)
                when {
                    autoStopMinutes > 0 && vlcPlayerProcess?.waitFor(
                        autoStopMinutes,
                        TimeUnit.MINUTES
                    ) == false -> stopVlcPlayer()
                }
            }
        }
        logger.info { "Started playing $url" }
    }

    fun stopVlcPlayer() {
        vlcPlayerProcess?.destroy()
        vlcPlayerProcess = null
    }
}
