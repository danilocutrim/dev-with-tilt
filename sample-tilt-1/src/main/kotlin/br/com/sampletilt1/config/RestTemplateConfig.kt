package br.com.sampletilt1.config

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.MediaType
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate
import java.io.BufferedReader

@Configuration
class RestTemplateConfig {

    @Value("\${http.debug}")
    val httpDebug: Boolean? = null

    private val logger = KotlinLogging.logger {}

    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
        val additionalMediaTypeList = ArrayList<MediaType>()
        additionalMediaTypeList.add(MediaType.APPLICATION_JSON)

        val additionalConverter = MappingJackson2HttpMessageConverter()
        additionalConverter.supportedMediaTypes = additionalMediaTypeList

        return builder.requestFactory {
            BufferingClientHttpRequestFactory(
                HttpComponentsClientHttpRequestFactory()
            )
        }
            .additionalMessageConverters(additionalConverter)
            .interceptors(listOf(defaultRequestInterceptor()))
            .build()
    }

    private fun defaultRequestInterceptor(): ClientHttpRequestInterceptor {
        return ClientHttpRequestInterceptor { httpRequest: HttpRequest,
            bytes: ByteArray,
            requestExecution: ClientHttpRequestExecution ->
            httpRequest.headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            if (httpDebug!!) traceRequest(httpRequest, bytes)
            val response = requestExecution.execute(httpRequest, bytes)
            if (httpDebug!!) traceResponse(response)
            response
        }
    }

    private fun traceRequest(request: HttpRequest, body: ByteArray) {
        logger.info("========================= request begin ====================================")
        logger.info("URI         : {}", request.uri)
        logger.info("Method      : {}", request.method)
        logger.info("Headers     : {}", request.headers)
        logger.info("Request body: {}", String(body))
        logger.info("========================= request end ======================================")
    }

    private fun traceResponse(response: ClientHttpResponse) {
        logger.info("======================== response begin ====================================")
        logger.info("Status code  : {}", response.statusCode)
        logger.info("Status text  : {}", response.statusText)
        logger.info("Headers      : {}", response.headers)
        logger.info(
            "Response body: {}",
            response.body.bufferedReader().use(BufferedReader::readText)
        )
        logger.info("======================== response end ======================================")
    }
}
