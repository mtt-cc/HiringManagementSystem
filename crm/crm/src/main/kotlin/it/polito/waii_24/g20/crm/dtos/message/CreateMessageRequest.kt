package it.polito.waii_24.g20.crm.dtos.message

import io.swagger.v3.oas.annotations.media.Schema
import it.polito.waii_24.g20.crm.common.Channel
import jakarta.validation.constraints.NotBlank

@Schema(description = "Data Transfer Object representing the creation of a message")
data class CreateMessageRequest(
    @Schema(description = "The sender of the message", example = "e@email.com")
    @NotBlank
    val sender: String,
    @Schema(description = "The receiver of the message", example = "14-12-2021")
    val date: String?,
    @Schema(description = "The subject of the message", example = "Subject")
    val subject: String?,
    @Schema(description = "The attachments of the message", example = "[\"1\", \"2\"]")
    val attachments: List<Long>?,
    @Schema(description = "The body of the message", example = "Body")
    val body: String?,
    @Schema(description = "The channel of the message", example = "email", implementation = Channel::class)
    val channel: String?
)