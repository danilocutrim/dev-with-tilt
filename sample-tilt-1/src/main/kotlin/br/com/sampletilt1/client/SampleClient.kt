package br.com.sampletilt1.client

import br.com.sampletilt1.Sample
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
class SampleClient(
    @Value("\${sample2.host}")
    private val host: String,
    private val restTemplate: RestTemplate
) {
    private val logger = KotlinLogging.logger {}

    fun getSample2(): Sample {
        val builder = UriComponentsBuilder.fromHttpUrl(host).path("/hello")

        try {
            return restTemplate.getForEntity(
                builder.build().toUri(),
                Sample::class.java
            ).body!!
        } catch (e: Exception) {
            logger.error("getSample2: error in sample 2")
            return Sample()
        }
    }
}