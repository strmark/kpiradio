package nl.strmark.piradio.util

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PipedInputStream
import java.util.concurrent.TimeUnit

@Component
class VlcPlayer {
    @Value("\${vlc.player.path}")
    private val vlcPlayerPath: String? = null
    private var vlcplayerProcess: Process? = null
    private var vlcplayerOutErr: BufferedReader? = null
    private val expected: String = "Starting playback..."

    @Throws(IOException::class, InterruptedException::class)
    fun open(url: String, autoStopMinutes: Int) {
        when (vlcplayerProcess) {
            null -> {
                // start VlcPlayer as an external process
                val command = "$vlcPlayerPath $url"
                logger.info { "Starting VlcPlayer process:$command" }
                vlcplayerProcess = Runtime.getRuntime().exec(command)
                val readFrom = PipedInputStream(1024 * 1024)
                vlcplayerOutErr = BufferedReader(InputStreamReader(readFrom))
                when {
                    autoStopMinutes > 0 && vlcplayerProcess?.waitFor(autoStopMinutes.toLong(), TimeUnit.MINUTES)
                        ?.not() == true -> close()
                }
            }
            else -> {
                close()
                open(url, autoStopMinutes)
            }
        }
        // wait to start playing
        waitForAnswer(expected)
        logger.info { "Started playing file $url" }
    }

    fun close() {
        // stop the vlcplayer
        if (vlcplayerProcess != null) {
            vlcplayerProcess?.destroy()
            vlcplayerProcess = null
        }
    }

    private fun waitForAnswer(expected: String?): String? {
        var line: String? = null
        when {
            expected != null -> {
                try {
                    while (vlcplayerOutErr?.readLine().also { line = it } != null) {
                        logger.info { "Reading line: $line" }
                        when {
                            line?.startsWith(expected) == true -> return line
                        }
                    }
                } catch (e: IOException) {
                    logger.error { "Exception in Wait for answer: $e.message" }
                }
            }
        }
        return line
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}