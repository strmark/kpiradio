package nl.strmark.piradio

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PiRadioApplication

fun main(args: Array<String>) {
    runApplication<PiRadioApplication>(*args)
}
