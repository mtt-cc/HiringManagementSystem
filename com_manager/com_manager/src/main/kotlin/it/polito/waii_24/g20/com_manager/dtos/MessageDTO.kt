package it.polito.waii_24.g20.com_manager.dtos

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Data Transfer Object representing a message (incoming mail)")
data class MessageDTO(
    @Schema(description = "The sender address in the format of Name <address>", example = "Mario Rossi <mario.rossi@mail.com>")
    val sender: String,

    @Schema(description = "The date of the message", example = "Wed, 22 May 2024 17:37:46 +0200")
    val date: String,

    @Schema(description = "The subject of the message", example = "Subject")
    val subject: String,

    @Schema(description = "The body of the message", example = "Body")
    val body: String,

    @Schema(description = "The Document Store ids for the attachments of the message", example = "[\"id1\", \"id2\"]")
    val attachments: List<Long>,

    @Schema(description = "The channel of the message: by default Email (hardcoded)", example = "Email")
    val channel: String = "Email"
)