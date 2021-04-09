package nl.strmark.piradio

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PiradioApplication

fun main(args: Array<String>) {
	runApplication<PiradioApplication>(*args)
}
