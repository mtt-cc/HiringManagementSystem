package it.polito.waii_24.g20.api_gateway.controllers

import it.polito.waii_24.g20.api_gateway.dtos.MeDTO
import it.polito.waii_24.g20.api_gateway.dtos.generate
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class PrincipalController {
    private val logger = LoggerFactory.getLogger(PrincipalController::class.java)

    @GetMapping("/user/me")
    fun me(
//        @CookieValue("XSRF-TOKEN", required = false) xsrfToken: String?,
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ): MeDTO {

        val principal : OidcUser? = authentication?.principal as? OidcUser

        val csrfToken = request.getAttribute(CsrfToken::class.java.name) as? CsrfToken
        if (csrfToken != null) {
            val cookie = Cookie("X-XSRF-TOKEN", csrfToken.token)
            cookie.path = "/"
            cookie.isHttpOnly = false
            response.addCookie(cookie)
        }

        logger.info("User information requested: ${csrfToken?.token}")
        return principal.generate(csrfToken?.token)
    }
}