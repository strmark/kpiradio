package nl.strmark.piradio.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import nl.strmark.piradio.entity.DefaultWebRadio
import nl.strmark.piradio.payload.PlayerRequest
import nl.strmark.piradio.payload.PlayerStatus
import nl.strmark.piradio.repository.DefaultWebradioRepository
import nl.strmark.piradio.repository.WebRadioRepository
import nl.strmark.piradio.util.VlcPlayer
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
@RestController
class PlayerController(
    private val webRadioRepository: WebRadioRepository,
    private val defaultWebradioRepository: DefaultWebradioRepository,
    private val vlcPlayer: VlcPlayer,
    private val objectMapper: ObjectMapper
) {

    @GetMapping(path = ["/player"], produces = ["application/json"])
    fun get(): String = objectMapper.writeValueAsString(status)

    @PostMapping(path = ["/player"], produces = ["application/json"])
    fun updatePlayer(@RequestBody player: PlayerRequest): String {
        logger.info { "WebRadio: ${player.webRadio}" }
        logger.info { "Status: ${player.status}" }
        return when (player.status) {
            "on" -> startPlayer(player.webRadio, player.autoStopMinutes)
            else -> stopPlayer()
        }
    }

    fun startPlayer(url: String?, autoStopMinutes: Int?): String {
        try {
            // no timer so minutes 0
            status = PlayerStatus("on")
            if (url != null && autoStopMinutes != null) vlcPlayer.open(url, autoStopMinutes.toLong())
        } catch (exception: Exception) {
            logger.error { "$exception.message, $exception" }
        }
        return objectMapper.writeValueAsString(status)
    }

    fun stopPlayer(): String {
        // stop playing and return status off
        try {
            status = PlayerStatus("off")
            vlcPlayer.stopVlcPlayer()
        } catch (exception: Exception) {
            logger.error { "$exception.message, $exception" }
        }
        return objectMapper.writeValueAsString(status)
    }

    private fun startPlayer(webRadioId: Int?, autoStopMinutes: Int?): String {
        logger.info { "WebRadio: id = $webRadioId" }
        logger.info { "WebRadio: autoStop = $autoStopMinutes" }
        return startPlayer(getUrl(webRadioId), autoStopMinutes)
    }

    private fun getUrl(webRadioId: Int?) =
        webRadioId?.let { webradio ->
            when (webradio) {
                0 -> defaultWebradioRepository.findAll().first()?.webRadioId?.let { webRadioRepository.findById(it).get().url }
                else -> webRadioRepository.findById(webradio).get().let { setDefaultWebRadio(it.id); it.url }
            }
        }

    private fun setDefaultWebRadio(webRadioId: Int) {
        defaultWebradioRepository.deleteAll()
        defaultWebradioRepository.save(DefaultWebRadio(1, webRadioId))
    }

    companion object {
        private val logger = KotlinLogging.logger {}
        private var status = PlayerStatus("off")
    }
}
