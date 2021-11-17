package br.com.sampletilt2

import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SampleController {

    val logger = KotlinLogging.logger {  }

    @GetMapping("/hello")
    fun hello() = Sample("world").also {
        logger.info { "hello: received call to sample" }
    }
}

data class Sample(val response: String)