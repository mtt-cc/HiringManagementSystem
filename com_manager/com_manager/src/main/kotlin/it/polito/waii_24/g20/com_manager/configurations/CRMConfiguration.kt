package it.polito.waii_24.g20.com_manager.configurations

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
/**
 * Configuration class for the CRM microservice.
 * This data class maps the properties in the application.yml file.
 *
 * @property url[String] The Base URL of the CRM microservice.
 * @property addMessageRoute[String] The route to add a message {POST}.
 */
@Configuration
@ConfigurationProperties(prefix = "microservices.crm")
class CRMConfig {
    lateinit var url: String
    lateinit var addMessageRoute : String
}