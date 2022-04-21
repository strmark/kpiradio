package nl.strmark.piradio.controller

import mu.KotlinLogging
import nl.strmark.piradio.payload.VolumeRequest
import nl.strmark.piradio.properties.PiRadioProperties
import nl.strmark.piradio.util.Audio
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import kotlin.math.ceil

@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
@RestController
class VolumeController {

    @Autowired
    private lateinit var piRadioProperties: PiRadioProperties

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @GetMapping(path = ["/volume"], produces = ["application/json"])
    fun getVolume(): String? {
        var volume = ceil(((Audio.getOutputVolume(piRadioProperties.device) ?: piRadioProperties.volume).times(100f))).toInt()
        volume = volume.coerceAtLeast(0).coerceAtMost(100)
        return "{\"volume\":$volume}"
    }

    @PostMapping(path = ["/volume"], produces = ["application/json"])
    fun updateVolume(@RequestBody volume: VolumeRequest): String? {
        val volValue: String = volume.volume
        val vol = volValue.toFloat() / 100
        logger.info("Volume $vol")
        Audio.setOutputVolume(piRadioProperties.device, vol)
        return "{\"volume\":$volValue}"
    }
}
