package it.polito.waii_24.g20.document_store

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class CustomAccessDeniedHandler : AccessDeniedHandler {
    override fun handle(request: HttpServletRequest?, response: HttpServletResponse?, accessDeniedException: AccessDeniedException?) {
        response?.status = HttpServletResponse.SC_FORBIDDEN
        response?.contentType = "application/json"
        response?.writer?.write("{\"error\": \"Forbidden\", \"message\": \"You do not have the necessary permissions to access this resource.\"}")
    }
}