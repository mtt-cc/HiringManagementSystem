package it.polito.waii_24.g20.crm.dtos.message

import io.swagger.v3.oas.annotations.media.Schema
import it.polito.waii_24.g20.crm.common.MessageStateValue
import jakarta.validation.constraints.NotBlank

@Schema(description = "Data Transfer Object representing the request to change the state of a target")
data class TargetStateRequest(
    @Schema(description = "The target state to which the change is requested", example = "completed", implementation = MessageStateValue::class)
    @NotBlank
    val targetState: String,
    @Schema(description = "The comment of the request", example = "The order has been completed")
    val comment: String?
)