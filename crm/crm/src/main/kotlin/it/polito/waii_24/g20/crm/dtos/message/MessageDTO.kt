package it.polito.waii_24.g20.crm.dtos.message

import io.swagger.v3.oas.annotations.media.Schema
import it.polito.waii_24.g20.crm.common.Channel
import it.polito.waii_24.g20.crm.common.MessageState
import it.polito.waii_24.g20.crm.common.MessageStateValue
import it.polito.waii_24.g20.crm.entities.message.Message
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

@Schema(description = "Data Transfer Object representing a message")
data class MessageDTO(
    @Schema(description = "The id of the message", example = "1")
    @NotBlank @Min(1)
    val id: Long,

    @Schema(description = "The sender of the message", example = "e@email.com")
    @NotBlank
    val sender: String,

    @Schema(description = "The date of the message", example = "2021-09-01")
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

    @Schema(description = "The channel of the message", example = "email", implementation = Channel::class)
    val channel: Channel,

    @Schema(description = "The actual state of the message", example = "pending", implementation = MessageStateValue::class)
    val actualMessageState: MessageState,

    @Schema(description = "The priority of the message", example = "1", allowableValues = ["1", "2", "3"])
    val priority: Byte
)


fun Message.toMessageDTO(): MessageDTO {
    return MessageDTO(
        id!!,
        sender,
        date,
        subject,
        attachments.map{ it.attachmentId },
        body,
        channel,
        MessageStateValue.createMessageState(actualState),
        priority
    )
}