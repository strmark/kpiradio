package nl.strmark.piradio.controller

import nl.strmark.piradio.entity.Webradio
import nl.strmark.piradio.exception.ResourceNotFoundException
import nl.strmark.piradio.repository.WebradioRepository
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
@RequestMapping("/")
class WebradioController(private val webradioRepository: WebradioRepository) {

    @GetMapping("/webradio")
    fun findAll() = webradioRepository.findAll()

    @PostMapping("/webradio")
    fun saveWebradio(@RequestBody webradioRequest: Webradio): Webradio {
        return webradioRepository.save(webradioRequest)
    }

    @GetMapping("/webradio/{id}")
    fun findById(@PathVariable(value = "id") webradioId: Int): Webradio? {
        return webradioRepository.findById(webradioId)
            .orElseThrow { ResourceNotFoundException(WEBRADIO, "id", webradioId) }
    }

    @PutMapping("/webradio/{id}")
    fun saveWebradio(
        @PathVariable(value = "id") webradioId: Int,
        @RequestBody webradioRequest: Webradio
    ): Webradio? {
        val webradio = webradioRepository.findById(webradioId)
            .orElseThrow { ResourceNotFoundException(WEBRADIO, "id", webradioId) }
        return when {
            webradio != null -> {
                webradio.name = webradioRequest.name
                webradio.url = webradioRequest.url
                webradio.isDefault = webradioRequest.isDefault
                webradioRepository.save(webradio)
            }
            else -> null
        }
    }

    @DeleteMapping("/webradio/{id}")
    fun deleteWebradio(@PathVariable(value = "id") webradioId: Int): ResponseEntity<Long> {
        val webradio = webradioRepository.findById(webradioId)
            .orElseThrow { ResourceNotFoundException(WEBRADIO, "id", webradioId) }
        return when {
            webradio != null -> {
                webradioRepository.delete(webradio)
                ResponseEntity.ok().build()
            }
            else -> ResponseEntity.notFound().build()
        }
    }

    companion object {
        private const val WEBRADIO = "Webradio"
    }
}