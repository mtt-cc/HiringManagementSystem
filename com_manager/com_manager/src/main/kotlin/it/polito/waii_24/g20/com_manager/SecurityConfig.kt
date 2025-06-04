package it.polito.waii_24.g20.com_manager

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customAccessDeniedHandler: CustomAccessDeniedHandler,
) {

    @Bean
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        val logger = LoggerFactory.getLogger(SecurityConfig::class.java)

        return httpSecurity
            .authorizeHttpRequests {
                logger.info("Setting up authorization rules")
                it.requestMatchers(HttpMethod.GET, "/**").hasAnyRole("guest", "operator", "manager")
                it.requestMatchers(HttpMethod.POST, "/**").hasAnyRole("operator", "manager")
                it.requestMatchers(HttpMethod.PUT, "/**").hasAnyRole("operator", "manager")
                it.requestMatchers(HttpMethod.DELETE, "/**").hasRole("manager")
                it.anyRequest().authenticated() //only authenticated users can access
            } //any other resource
            .oauth2ResourceServer {
                it.jwt { jwtConfigurer ->
                    jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter())
                    logger.info("Configuring OAuth2 Resource Server with JWT")
                }
            }
            .sessionManagement{ it.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS) }
            .csrf{ it.disable() }
            .cors{
                it.configurationSource(corsConfigurationSource())
            }
            .exceptionHandling {
                it.accessDeniedHandler(customAccessDeniedHandler)
            }
            .build()
    }

    // Define CORS configuration
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val corsConfiguration = CorsConfiguration()
        corsConfiguration.allowedOrigins = listOf("http://localhost:8080")
        corsConfiguration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        corsConfiguration.allowedHeaders = listOf("Authorization", "Content-Type", "X-XSRF-TOKEN")
        corsConfiguration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfiguration)
        return source
    }
}

fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
    val jwtAuthenticationConverter = JwtAuthenticationConverter()
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter { jwt: Jwt ->
        val logger = LoggerFactory.getLogger(SecurityConfig::class.java)
        val authorities = mutableSetOf<GrantedAuthority>()
        val resourceAccess = jwt.claims["resource_access"] as Map<String, Any>?
        logger.info("Configuring OAuth2 Resource Server with JWT")
        resourceAccess?.let {
            val clientAccess = it["apiGatewayClient"] as Map<String, Any>?
            val roles = clientAccess?.get("roles") as List<String>?

            roles?.forEach { role ->
                authorities.add(SimpleGrantedAuthority("ROLE_$role"))
            }
        }

        authorities
    }
    return jwtAuthenticationConverter
}