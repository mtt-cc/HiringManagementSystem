package it.polito.waii_24.g20.crm.dtos.contact

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import it.polito.waii_24.g20.crm.common.ContactCategory
import it.polito.waii_24.g20.crm.common.ContactCategoryDeserializer
import it.polito.waii_24.g20.crm.entities.contact.Contact

data class ContactDTO(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val ssn: String,
    val emails: List<EmailDTO>,
    val addresses: List<AddressDTO>,
    val phoneNumbers: List<PhoneNumberDTO>,
    @JsonDeserialize(using = ContactCategoryDeserializer::class)
    val category: ContactCategory,
    val relatedProfessional: ProfessionalDetailsDTO?,
    val relatedCustomer: CustomerDetailsDTO?
)

fun Contact.toContactOnlyDTO(): ContactDTO = ContactDTO(
    id = this.id!!,
    firstName = this.firstName,
    lastName = this.lastName,
    ssn = this.ssn,
    emails = this.emails.map { it.toDTO() },
    addresses = this.addresses.map { it.toDTO() },
    phoneNumbers = this.phoneNumbers.map { it.toDTO() },
    category = this.category,
    relatedProfessional = null,
    relatedCustomer = null
)

fun Contact.toDTO(): ContactDTO = ContactDTO(
    id = this.id!!,
    firstName = this.firstName,
    lastName = this.lastName,
    ssn = this.ssn,
    emails = this.emails.map { it.toDTO() },
    addresses = this.addresses.map { it.toDTO() },
    phoneNumbers = this.phoneNumbers.map { it.toDTO() },
    category = this.category,
    relatedProfessional = this.professionalDetails?.toDTO(),
    relatedCustomer = this.customerDetails?.toDTO()
)