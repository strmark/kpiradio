package nl.strmark.piradio.util

import mu.KotlinLogging
import org.springframework.stereotype.Component
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent
import java.lang.Thread.sleep

@Component
class VlcPlayer {
    private val audioPlayer: AudioPlayerComponent = object : AudioPlayerComponent() {
        override fun finished(mediaPlayer: MediaPlayer) {
            logger.info { "Finished" }
            mediaPlayer.release()
        }

        override fun error(mediaPlayer: MediaPlayer) {
            logger.error("Failed to play media")
            throw RuntimeException()
        }
    }

    fun open(url: String, autoStopMinutes: Int) {
        // Play the MRL specified by the first command-line argument
        audioPlayer.mediaPlayer().media().play(url)
        logger.info { "Started playing file $url" }
        if (autoStopMinutes > 0) {
            // Wait the autoStopMinutes
            sleep((autoStopMinutes * 60 * 1000).toLong())
            close()
            //Thread.currentThread().join((autoStopMinutes*60*1000).toLong())
        }
    }

    fun close() {
        if (audioPlayer.mediaPlayer().status().isPlaying) {
            audioPlayer.mediaPlayer().controls().stop()
            logger.info { "Stopped player" }
        }
    }

    fun getSpeakerOutputVolume(): Int {
        return audioPlayer.mediaPlayer().audio().volume()
    }

    fun setSpeakerOutputVolume(value: Int) {
        when {
            value < 0 -> audioPlayer.mediaPlayer().audio().setVolume(0)
            value > 100 -> audioPlayer.mediaPlayer().audio().setVolume(100)
            else -> audioPlayer.mediaPlayer().audio().setVolume(value)
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
