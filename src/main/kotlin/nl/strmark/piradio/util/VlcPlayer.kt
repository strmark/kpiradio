package nl.strmark.piradio.util

import mu.KotlinLogging
import nl.strmark.piradio.properties.PiRadioProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PipedInputStream
import java.util.concurrent.TimeUnit

@Component
class VlcPlayer {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @Autowired
    private lateinit var piRadioProperties: PiRadioProperties

    private var vlcPlayerProcess: Process? = null
    private var vlcPlayerOutErr: BufferedReader? = null

    @Throws(IOException::class, InterruptedException::class)
    fun open(url: String, autoStopMinutes: Long) {
        if (vlcPlayerProcess == null) {
            // start VlcPlayer as an external process
            val command = "${piRadioProperties.vlc.player.path} $url"
            logger.info("Starting VlcPlayer process:{}", command)
            vlcPlayerProcess = Runtime.getRuntime().exec(command)
            val readFrom = PipedInputStream(1024 * 1024)
            vlcPlayerOutErr = BufferedReader(InputStreamReader(readFrom))
            if (autoStopMinutes > 0 &&
                !vlcPlayerProcess?.waitFor(autoStopMinutes, TimeUnit.MINUTES)!!
            ) {
                vlcPlayerProcess?.destroy()
                vlcPlayerProcess = null
            }
        } else {
            vlcPlayerProcess?.destroy()
            vlcPlayerProcess = null
            open(url, autoStopMinutes)
        }
        // wait to start playing
        waitForAnswer()
        logger.info("Started playing $url")
    }

    fun close() {
        vlcPlayerProcess?.destroy()
    }

    private fun waitForAnswer(): String? {
        var line: String? = null
        try {
            while (vlcPlayerOutErr?.readLine().also { line = it } != null) {
                logger.info("Reading line: $line")
                if (line?.startsWith("Starting playback...") == true)
                    return line
            }
        } catch (e: IOException) {
            logger.error("Exception in Wait for answer:  ${e.message}")
        }
        return line
    }
}
