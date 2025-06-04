package it.polito.waii_24.g20.crm.dtos.contact

import it.polito.waii_24.g20.crm.dtos.DTO
import it.polito.waii_24.g20.crm.entities.contact.Email

data class EmailDTO(
    override val id: Long,
    val email: String
) : DTO<Long>

fun Email.toDTO(): EmailDTO = EmailDTO(
    id = this.id!!,
    email = this.email
)