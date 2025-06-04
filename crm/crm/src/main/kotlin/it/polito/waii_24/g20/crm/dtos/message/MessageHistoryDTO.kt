package it.polito.waii_24.g20.crm.dtos.message

import io.swagger.v3.oas.annotations.media.Schema
import it.polito.waii_24.g20.crm.common.MessageStateValue
import it.polito.waii_24.g20.crm.entities.message.MessageHistory
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

@Schema(description = "Data Transfer Object representing a history")
data class MessageHistoryDTO(
    @Schema(description = "The id of the history", example = "1")
    @NotBlank @Min(1)
    val id: Long,

    @Schema(description = "The id of the message", example = "1")
    @NotBlank @Min(1)
    val messageId: Long,

    @Schema(description = "The date of the state change", example = "2021-09-01")
    val dateOfStateChange: String,

    @Schema(description = "The state from which the change occurred", example = "pending", implementation = MessageStateValue::class)
    val fromState: String,

    @Schema(description = "The state to which the change occurred", example = "completed", implementation = MessageStateValue::class)
    val toState: String,

    @Schema(description = "The comments of the history", example = "The order has been completed")
    val comments: String?
)
fun MessageHistory.toHistoryDTO(): MessageHistoryDTO {
    return MessageHistoryDTO(
        id!!,
        message.id!!,
        dateOfStateChange,
        fromState,
        toState,
        comments
    )
}