package nl.strmark.piradio.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import nl.strmark.piradio.properties.PiRadioProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
@RestController
class VolumeController(private val objectMapper: ObjectMapper) {

    @Autowired
    private lateinit var piRadioProperties: PiRadioProperties

    @PostMapping(path = ["/volumeDown"], produces = ["application/json"])
    fun vVolumeDown(): String {
        setDeviceVolume(piRadioProperties.amixer.steps + "-")
        return objectMapper.writeValueAsString("Volume down")
    }

    @PostMapping(path = ["/volumeUp"], produces = ["application/json"])
    fun volumeUp(): String {
        setDeviceVolume(piRadioProperties.amixer.steps + "+")
        return objectMapper.writeValueAsString("Volume up")
    }

    private fun setDeviceVolume(volume: String) {
        val command = arrayOf(piRadioProperties.amixer.amixer, SSET, piRadioProperties.amixer.device, volume)
        logger.info { "Update volume with command: ${command.joinToString(" ")}" }
        Runtime.getRuntime().exec(command)
    }

    companion object {
        private val logger = KotlinLogging.logger {}
        const val SSET = "sset"
    }
}
