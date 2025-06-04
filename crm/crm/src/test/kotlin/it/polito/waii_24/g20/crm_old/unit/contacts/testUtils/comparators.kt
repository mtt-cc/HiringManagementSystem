package it.polito.waii_24.g20.crm_old.unit.contacts.testUtils

import it.polito.waii_24.g20.crm_old.dtos.AddressDTO
import it.polito.waii_24.g20.crm_old.dtos.ContactDTO
import it.polito.waii_24.g20.crm_old.dtos.EmailDTO
import it.polito.waii_24.g20.crm_old.dtos.TelephoneDTO

fun EmailDTO.equalsTo(emailDTO: EmailDTO): Boolean {
    if (this.id != emailDTO.id) return false
    if (this.email != emailDTO.email) return false
    return true
}

fun TelephoneDTO.equalsTo(telephoneDTO: TelephoneDTO): Boolean {
    if (this.id != telephoneDTO.id) return false
    if (this.telephone != telephoneDTO.telephone) return false
    return true
}

fun AddressDTO.equalsTo(addressDTO: AddressDTO): Boolean {
    if (this.id != addressDTO.id) return false
    if (this.street != addressDTO.street) return false
    if (this.number != addressDTO.number) return false
    if (this.city != addressDTO.city) return false
    if (this.postalCode != addressDTO.postalCode) return false
    return true
}

fun ContactDTO.equalsTo(contactDTO: ContactDTO): Boolean {
    if (this.id != contactDTO.id) return false
    if (this.firstName != contactDTO.firstName) return false
    if (this.lastName != contactDTO.lastName) return false
    if (this.emails.size != contactDTO.emails.size) return false
    if (this.phoneNumbers.size != contactDTO.phoneNumbers.size) return false
    if (this.addresses.size != contactDTO.addresses.size) return false
    val emailPairs = this.emails.toTypedArray().zip(contactDTO.emails.toTypedArray())
    val telephonePairs = this.phoneNumbers.toTypedArray().zip(contactDTO.phoneNumbers.toTypedArray())
    val addressPairs = this.addresses.toTypedArray().zip(contactDTO.addresses.toTypedArray())
    return emailPairs.all { it.first.equalsTo(it.second) } &&
            telephonePairs.all { it.first.equalsTo(it.second) } &&
            addressPairs.all { it.first.equalsTo(it.second) }
}