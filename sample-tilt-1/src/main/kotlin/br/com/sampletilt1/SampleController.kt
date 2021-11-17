package br.com.sampletilt1

import br.com.sampletilt1.client.SampleClient
import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SampleController(private val sampleClient: SampleClient) {

    val logger = KotlinLogging.logger {  }

    @GetMapping("/hello")
    fun hello(): Sample {
        val sample2 = sampleClient.getSample2()

        return Sample("hello, ${sample2.response}").also {
            logger.info { "hello: finish request sample" }
        }
    }
}

data class Sample(val response: String? = "sample2 DOWN")