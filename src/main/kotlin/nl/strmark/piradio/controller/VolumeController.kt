package nl.strmark.piradio.controller

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import mu.KotlinLogging
import nl.strmark.piradio.payload.VolumeRequest
import nl.strmark.piradio.util.Audio
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import kotlin.math.ceil

@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
@RestController
class VolumeController {

    @GetMapping(path = ["/volume"], produces = ["application/json"])
    fun getVolume(): JsonElement {
        var volume = Audio.speakerOutputVolume?.times(100)?.toDouble()?.let { ceil(it).toInt() } ?: 0
        volume = volume.coerceAtLeast(0)
        volume = volume.coerceAtMost(100)
        logger.info { "Get Volume $volume" }
        return Json.parseToJsonElement("""{"volume":"$volume"}""")
    }

    @PostMapping(path = ["/volume"], produces = ["application/json"])
    fun setVolume(@RequestBody volume: VolumeRequest): JsonElement {
        val vol = volume.volume.toFloat() / 100
        logger.info { "Set Volume $vol" }
        Audio.setSpeakerOutputVolume(vol)
        return Json.parseToJsonElement("""{"volume":"${volume.volume}"}""")
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}