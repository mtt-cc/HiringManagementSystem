package it.polito.waii_24.g20.crm.dtos.contact

import it.polito.waii_24.g20.crm.dtos.DTO
import it.polito.waii_24.g20.crm.entities.contact.Address

data class AddressDTO(
    override val id: Long,
    val street: String,
    val number: String,
    val city: String,
    val postalCode: String,
    val country: String
) : DTO<Long>

fun Address.toDTO(): AddressDTO = AddressDTO(
    id = this.id!!,
    street = this.street,
    number = this.number,
    city = this.city,
    postalCode = this.postalCode,
    country = this.country
)
