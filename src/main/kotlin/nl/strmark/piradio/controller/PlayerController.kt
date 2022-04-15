package nl.strmark.piradio.controller

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import mu.KotlinLogging
import nl.strmark.piradio.entity.WebRadio
import nl.strmark.piradio.payload.PlayerRequest
import nl.strmark.piradio.repository.WebRadioRepository
import nl.strmark.piradio.util.VlcPlayer
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
@RestController
class PlayerController(private val webRadioRepository: WebRadioRepository, private val vlcPlayer: VlcPlayer) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @GetMapping(path = ["/player"], produces = ["application/json"])
    fun get(): JsonElement = Json.parseToJsonElement("""{"status":"on"}""")

    @PostMapping(path = ["/player"], produces = ["application/json"])
    fun updatePlayer(@RequestBody player: PlayerRequest): JsonElement {
        logger.info("WebRadio: ${player.webRadio}")
        logger.info("Status: ${player.status}")
        return when (player.status) {
            "on" -> startPlayer(player.webRadio, player.autoStopMinutes)
            else -> stopPlayer()
        }
    }

    fun startPlayer(url: String?, autoStopMinutes: Int?): JsonElement {
        try {
            // no timer so minutes 0
            if (url != null && autoStopMinutes != null) vlcPlayer.open(url, autoStopMinutes.toLong())
        } catch (exception: Exception) {
            logger.error { "$exception.message, $exception" }
        }
        return Json.parseToJsonElement("""{"status":"on"}""")
    }

    fun stopPlayer(): JsonElement {
        // stop playing and return status off
        try {
            vlcPlayer.stopVlcPlayer()
        } catch (exception: Exception) {
            logger.error { "$exception.message, $exception" }
        }
        return Json.parseToJsonElement("""{"status":"off"}""")
    }

    private fun startPlayer(webRadioId: Int?, autoStopMinutes: Int?): JsonElement {
        logger.info { "WebRadio: id = $webRadioId" }
        logger.info { "WebRadio: autoStop = $autoStopMinutes" }
        val url: String? = when (webRadioId) {
            0 -> getWebRadioUrl(webRadioRepository.findAll().requireNoNulls())
            else -> {
                webRadioRepository
                    .findAll()
                    .map { webRadio: WebRadio? ->
                        when {
                            webRadio != null -> webRadioId?.let { setDefaultAndSave(it, webRadio) }
                            else -> null
                        }
                    }
                    .joinToString()
            }
        }
        return startPlayer(url, autoStopMinutes)
    }

    private fun setDefaultAndSave(webRadioId: Int, webRadio: WebRadio): String? {
        when {
            webRadio.isDefault -> {
                when (webRadio.id) {
                    webRadioId -> {
                        webRadio.isDefault = true
                        webRadioRepository.save(webRadio)
                        return webRadio.url
                    }
                    else -> {
                        webRadio.isDefault = false
                        webRadioRepository.save(webRadio)
                    }
                }
            }
            webRadio.id == webRadioId -> {
                webRadio.isDefault = true
                webRadioRepository.save(webRadio)
                return webRadio.url
            }
        }
        return null
    }

    private fun getWebRadioUrl(webRadioList: List<WebRadio>): String? {
        return webRadioList.firstOrNull(WebRadio::isDefault)?.url
    }
}
