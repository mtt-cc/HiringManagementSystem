package it.polito.waii_24.g20.crm.dtos.contact

import it.polito.waii_24.g20.crm.entities.contact.Contact

data class ContactHeaderDTO(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val category: String,
)

fun Contact.toContactHeaderDTO() = ContactHeaderDTO(
    id = this.id!!,
    firstName = this.firstName,
    lastName = this.lastName,
    category = this.category.toString(),
)