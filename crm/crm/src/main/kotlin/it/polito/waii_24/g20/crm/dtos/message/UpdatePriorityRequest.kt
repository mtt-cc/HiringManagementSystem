package it.polito.waii_24.g20.crm.dtos.message

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Data Transfer Object representing the request to update the priority of a message")
data class UpdatePriorityRequest(
    @Schema(description = "The new priority of the message", example = "1", allowableValues = ["1", "2", "3"])
    val newPriority: Byte
)  // choose Byte because only valid priority values are by design 1, 2 and 3