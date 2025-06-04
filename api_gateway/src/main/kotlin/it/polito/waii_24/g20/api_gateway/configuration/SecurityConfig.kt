package it.polito.waii_24.g20.api_gateway.configuration

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.stereotype.Component
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@EnableWebSecurity
@Configuration
class SecurityConfig(
    private val crr: ClientRegistrationRepository,
    private val customAccessDeniedHandler: CustomAccessDeniedHandler,
    ) {
    //handle RP-initiated logout
    fun oidcLogoutSuccessHandler() = OidcClientInitiatedLogoutSuccessHandler(crr)
        .also {
            it.setPostLogoutRedirectUri("http://localhost:8080/")
        }

    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        return httpSecurity
            .authorizeHttpRequests {
                it.requestMatchers( "/user/me", "/").permitAll() //anybody can access the me endpoint
                it.anyRequest().authenticated() //only authenticated users can access
            } //any other resource
            .oauth2Login {}
            .csrf {
                it.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            }
            .cors {it.configurationSource(corsConfigurationSource())}
            .exceptionHandling {
                it.accessDeniedHandler(customAccessDeniedHandler)
            }
            .logout {
                it.logoutSuccessHandler(oidcLogoutSuccessHandler())
            }
            .build()
    }

    // Define CORS configuration
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val corsConfiguration = CorsConfiguration()
        corsConfiguration.allowedOrigins = listOf("http://localhost:3000") // Frontend URL
        corsConfiguration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        corsConfiguration.allowedHeaders = listOf("Authorization", "Content-Type", "X-XSRF-TOKEN")
        corsConfiguration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfiguration)
        return source
    }

}

@Component
class CustomAccessDeniedHandler : AccessDeniedHandler {
    override fun handle(request: HttpServletRequest?, response: HttpServletResponse?, accessDeniedException: AccessDeniedException?) {
        response?.status = HttpServletResponse.SC_FORBIDDEN
        response?.contentType = "application/json"
        response?.writer?.write("{\"error\": \"Forbidden by my handler\", \"message\": ${accessDeniedException?.message}}")
    }
}