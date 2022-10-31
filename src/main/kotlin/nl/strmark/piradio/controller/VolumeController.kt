package nl.strmark.piradio.controller

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import nl.strmark.piradio.payload.VolumeRequest
import nl.strmark.piradio.payload.VolumeValue
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
class VolumeController(private val objectMapper: ObjectMapper) {

    @Autowired
    private lateinit var piRadioProperties: PiRadioProperties

    companion object {
        private val logger = KotlinLogging.logger {}
        private var volume = VolumeValue(50)
    }

    @GetMapping(path = ["/volume"], produces = ["application/json"])
    fun getVolume(): String {
        val vol = getDeviceVolume()
        volume = VolumeValue(roundValue(vol))
        return objectMapper.writeValueAsString(volume)
    }

    @PostMapping(path = ["/volume"], produces = ["application/json"])
    fun updateVolume(@RequestBody request: VolumeRequest): String {
        val vol = request.volume.toFloat() / 100
        volume = VolumeValue(roundValue(vol.toInt()))
        logger.info("Volume $vol")
        Audio.setOutputVolume(piRadioProperties.device, vol)
        return objectMapper.writeValueAsString(volume)
    }

    private fun getDeviceVolume() =
        ceil(((Audio.getOutputVolume(piRadioProperties.device) ?: piRadioProperties.volume).times(100f))).toInt()

    private fun roundValue(volume: Int) = volume.coerceAtLeast(0).coerceAtMost(100)
}
