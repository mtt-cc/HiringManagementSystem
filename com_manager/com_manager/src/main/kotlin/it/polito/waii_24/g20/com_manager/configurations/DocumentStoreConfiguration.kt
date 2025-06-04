package it.polito.waii_24.g20.com_manager.configurations

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * Configuration class for the Document Store microservice.
 * This data class maps the properties in the application.yml file.
 *
 * @property url[String] The Base URL of the Document Store microservice.
 * @property addRoute[String] The route to add a document {POST}.
 * @property getContentRoute[String] The route to get the content of a document {GET - Application/Octet-Stream}
 * @property getMetadataRoute[String] The route to get the metadata of a document {GET - Application/Json}
 */
@Configuration
@ConfigurationProperties(prefix = "microservices.document-store")
class DSConfig {
    lateinit var url: String
    lateinit var addRoute: String
    lateinit var getContentRoute: String
    lateinit var getMetadataRoute: String
    lateinit var embeddedAttachmentRoute: String
}