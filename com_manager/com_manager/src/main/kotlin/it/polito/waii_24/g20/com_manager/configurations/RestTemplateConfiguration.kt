package it.polito.waii_24.g20.com_manager.configurations

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

/**
 * Configuration class for the RestTemplate bean.
 */
@Configuration
class RestTemplateConfiguration {
    @Bean
    fun restTemplate(builder: RestTemplateBuilder) : RestTemplate {
        return builder.build()
    }
}