package it.polito.waii_24.g20.crm.dtos.message

import io.swagger.v3.oas.annotations.media.Schema
import it.polito.waii_24.g20.crm.common.MessageStateValue
import jakarta.validation.constraints.NotBlank

@Schema(description = "Data Transfer Object representing the creation of a message")
data class CreateMessageDTO(
    @Schema(description = "The sender of the message", example = "mail@mail.com")
    @NotBlank
    val sender: String,

    @Schema(description = "The receiver of the message", example = "14-12-2021")
    @NotBlank
    val date: String,

    @Schema(description = "The subject of the message", example = "Subject")
    @NotBlank
    val subject: String,

    @Schema(description = "The attachments of the message", example = "[\"attachment1\", \"attachment2\"]")
    val attachments: List<Long>,

    @Schema(description = "The body of the message", example = "Body")
    @NotBlank
    val body: String,

    @Schema(description = "The channel of the message", example = "Email")
    val channel: String,

    @Schema(description = "The actual state of the message", implementation = MessageStateValue::class, example = "received")
    val actualState: String
)