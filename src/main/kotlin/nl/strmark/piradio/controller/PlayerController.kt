package nl.strmark.piradio.controller

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import mu.KotlinLogging
import nl.strmark.piradio.entity.Webradio
import nl.strmark.piradio.payload.PlayerRequest
import nl.strmark.piradio.repository.WebradioRepository
import nl.strmark.piradio.util.VlcPlayer
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.Objects
import java.util.stream.Collectors

@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
@RestController
class PlayerController(private val webradioRepository: WebradioRepository, private val vlcplayer: VlcPlayer) {


    @GetMapping(path = ["/player"], produces = ["application/json"])
    fun get(): JsonElement = Json.parseToJsonElement("""{"status":"on"}""")

    @PostMapping(path = ["/player"], produces = ["application/json"])
    fun updatePlayer(@RequestBody player: PlayerRequest): JsonElement {
        logger.info("Webradio: ${player.webradio}")
        logger.info("Status: ${player.status}")
        return when (player.status) {
            "on" -> startPlayer(player.webradio, player.autoStopMinutes)
            else -> stopPlayer()
        }
    }

    private fun startPlayer(webradioId: Int?, autoStopMinutes: Int?): JsonElement {
        logger.info { "Webradio: id = $webradioId" }
        logger.info { "Webradio: autostop = $autoStopMinutes" }
        val url: String? = when (webradioId) {
            0 -> getWebradioUrl(webradioRepository.findAll().requireNoNulls())
            else -> {
                webradioRepository
                    .findAll()
                    .stream()
                    .map<String?> { webradio: Webradio? ->
                        when {
                            webradio != null -> webradioId?.let { setDefaultAndSave(it, webradio) }
                            else -> null
                        }
                    }
                    .filter { obj: String? -> Objects.nonNull(obj) }
                    .collect(Collectors.joining())
            }
        }
        return startPlayer(url, autoStopMinutes)
    }

    private fun setDefaultAndSave(webradioId: Int, webradio: Webradio): String? {
        when {
            webradio.isDefault -> {
                when (webradio.id) {
                    webradioId -> {
                        webradio.isDefault = true
                        webradioRepository.save(webradio)
                        return webradio.url
                    }
                    else -> {
                        webradio.isDefault = false
                        webradioRepository.save(webradio)
                    }
                }
            }
            webradio.id == webradioId -> {
                webradio.isDefault = true
                webradioRepository.save(webradio)
                return webradio.url
            }
        }
        return null
    }

    private fun getWebradioUrl(webradioList: List<Webradio>): String? {
        return webradioList
            .stream()
            .filter(Webradio::isDefault)
            .map<String?>(Webradio::url)
            .findAny()
            .orElse(null)
    }

    fun startPlayer(url: String?, autoStopMinutes: Int?): JsonElement {
        try {
            // no timer so minutes 0l
            if (url != null && autoStopMinutes != null) vlcplayer.open(url, autoStopMinutes)
        } catch (exception: Exception) {
            logger.error { "$exception.message, $exception" }
        }
        return Json.parseToJsonElement("""{"status":"on"}""")
    }

    fun stopPlayer(): JsonElement {
        // stop playing and return status off
        try {
            vlcplayer.close()
        } catch (exception: Exception) {
            logger.error { "$exception.message, $exception" }
        }
        return Json.parseToJsonElement("""{"status":"off"}""")
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}