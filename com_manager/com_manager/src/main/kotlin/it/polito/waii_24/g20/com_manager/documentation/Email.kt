package it.polito.waii_24.g20.com_manager.documentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses

@Operation(
    summary = "Send an Email using the Gmail API",
    description = "This method uses the Gmail API to send an email, it receives the receiver, the subject, the body and eventually a list of attachments.",
    tags = ["Email"]
)
@ApiResponses(
    value = [
        ApiResponse(
            responseCode = "202",
            description = "Accepted",
            content = [Content()]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Invalid receiver address | Invalid request body",
            content = [Content()]
        ),
        ApiResponse(
            responseCode = "500",
            description = "Internal server error | Impossible to send the email",
            content = [Content()]
        )
    ]
)
annotation class SendEmail