package it.polito.waii_24.g20.crm.dtos.contact

import it.polito.waii_24.g20.crm.dtos.DTO
import it.polito.waii_24.g20.crm.entities.contact.PhoneNumber

data class PhoneNumberDTO (
    override var id: Long,
    var telephone: String
) : DTO<Long>

fun PhoneNumber.toDTO(): PhoneNumberDTO = PhoneNumberDTO(
    id = this.id!!,
    telephone = this.phoneNumber
)