package it.polito.waii_24.g20.com_manager.services

import it.polito.waii_24.g20.com_manager.configurations.CRMConfig
import it.polito.waii_24.g20.com_manager.dtos.MessageDTO
import org.apache.camel.CamelContext
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.ExchangeBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
interface MessageService {
    fun registerIncomingMessage(message: MessageDTO)
}

@Service
class MessageServiceImpl(
    private val rt: RestTemplate,
    crm: CRMConfig,
    private val camelContext: CamelContext,
    private val producerTemplate: ProducerTemplate
) : MessageService {
    private val addMessageUrl = crm.url + crm.addMessageRoute
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Value("\${spring.security.oauth2.client.provider.keycloak.token-uri}")
    private lateinit var tokenUri: String

    @Value("\${spring.security.oauth2.client.registration.keycloak.client-id}")
    private lateinit var clientId: String

    @Value("\${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private lateinit var clientSecret: String

    private fun getAccessToken(): String {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val body = "grant_type=client_credentials&client_id=$clientId&client_secret=$clientSecret"

        val request = HttpEntity(body, headers)
        val response = rt.postForEntity(tokenUri, request, Map::class.java)

        return response.body?.get("access_token") as? String
            ?: throw RuntimeException("Failed to obtain access token from Keycloak")
    }

    /**
     * Registers an incoming message in the CRM
     * The function send a POST request to the CRM to add the message,
     * the endpoint configuration is in the application.yml
     *
     * @param message[MessageDTO] the message to be registered
     */
    override fun registerIncomingMessage(message: MessageDTO) {
        try {
            val testRouteExchange = ExchangeBuilder.anExchange(camelContext)
                .withBody(message)
                .build()
            producerTemplate.send(
                "direct:testIncomingMessage",
                testRouteExchange
            )
            logger.info("Registering message in the CRM")

            val token = getAccessToken()
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON
            headers.setBearerAuth(token)

            val httpEntity = HttpEntity(message, headers)

            rt.postForEntity(addMessageUrl, httpEntity, String::class.java)
            logger.info("Message registered in the CRM")
        }
        catch (e: Exception) {
            logger.info("Impossible to register the message in the CRM. Error: ${e.message}")
            logger.info("Trying again...")
            throw e
        }
    }
}