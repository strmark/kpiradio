package nl.strmark.piradio

import nl.strmark.piradio.properties.PiRadioProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(PiRadioProperties::class)
class PiRadioApplication

fun main(args: Array<String>) {
    runApplication<PiRadioApplication>(*args)
}
