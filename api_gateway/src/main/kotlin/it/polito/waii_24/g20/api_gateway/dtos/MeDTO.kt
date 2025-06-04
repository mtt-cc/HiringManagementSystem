package it.polito.waii_24.g20.api_gateway.dtos

import org.springframework.security.oauth2.core.oidc.user.OidcUser

data class MeDTO(
    val loginUrl: String = "http://localhost:8080/oauth2/authorization/apiGatewayClient",
    val logoutUrl: String = "http://localhost:8080/logout",

    val status: Boolean,
    val name: String?,
    val fullname: String?,
    val email: String?,
    val emailVerified: Boolean?,
    val roles: List<String>,
//    val principal: OidcUser?,
    val xsrfToken: String?
)

fun OidcUser?.generate(xsrfToken: String?) = MeDTO(
    status = this != null,
    name = this?.preferredUsername,
    fullname = this?.fullName,
    email = this?.email,
    emailVerified = this?.emailVerified,
    roles = this?.attributes?.extractRoles() ?: emptyList(),
//    principal = this,
    xsrfToken = xsrfToken
)

fun Map<String,Any>.extractRoles(): List<String>? {
    val roles1 = this.get("resource_access") as Map<String, Any>?
    val roles2 = roles1?.get("apiGatewayClient") as Map<String, Any>?
    return roles2?.get("roles") as List<String>?
}