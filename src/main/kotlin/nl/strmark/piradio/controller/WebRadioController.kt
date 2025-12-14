package nl.strmark.piradio.controller

import nl.strmark.piradio.entity.WebRadio
import nl.strmark.piradio.exception.ResourceNotFoundException
import nl.strmark.piradio.repository.DefaultWebradioRepository
import nl.strmark.piradio.repository.WebRadioRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
@RestController
@RequestMapping("/webRadio")
class WebRadioController(private val webRadioRepository: WebRadioRepository, private val defaultWebradioRepository: DefaultWebradioRepository) {

    @GetMapping
    fun findAll(): MutableList<WebRadio?> = webRadioRepository.findAll()

    @GetMapping(path = ["default"])
    fun getDefault(): String =
        defaultWebradioRepository.findAll().first().webRadioId?.let { id ->
            webRadioRepository.findById(id).get().name
        }.toString()

    @PostMapping
    fun saveWebRadio(@RequestBody webRadioRequest: WebRadio): WebRadio = webRadioRepository.save(webRadioRequest)

    @GetMapping("{id}")
    fun findById(@PathVariable(value = "id") webRadioId: Int): WebRadio? =
        webRadioRepository.findById(webRadioId)
            .orElseThrow { ResourceNotFoundException(WEBRADIO, "id", webRadioId) }

    @PutMapping("{id}")
    fun saveWebRadio(
        @PathVariable(value = "id") webRadioId: Int,
        @RequestBody webRadioRequest: WebRadio
    ): WebRadio? =
        webRadioRepository.findById(webRadioId)
            .orElseThrow { ResourceNotFoundException(WEBRADIO, "id", webRadioId) }
            ?.let { webRadio ->
                webRadio.name = webRadioRequest.name
                webRadio.url = webRadioRequest.url
                webRadioRepository.save(webRadio)
            }

    @DeleteMapping("{id}")
    fun deleteWebRadio(@PathVariable(value = "id") webRadioId: Int): ResponseEntity<Long> =
        webRadioRepository.findById(webRadioId)
            .orElseThrow { ResourceNotFoundException(WEBRADIO, "id", webRadioId) }
            ?.let { webRadio ->
                webRadioRepository.delete(webRadio)
                ResponseEntity.ok().build()
            } ?: ResponseEntity.notFound().build()

    companion object {
        private const val WEBRADIO = "WebRadio"
    }
}
