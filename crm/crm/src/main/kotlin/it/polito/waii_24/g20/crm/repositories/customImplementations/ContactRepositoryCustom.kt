package it.polito.waii_24.g20.crm.repositories.customImplementations

import it.polito.waii_24.g20.crm.common.ContactCategory
import it.polito.waii_24.g20.crm.entities.contact.Contact
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ContactRepositoryCustom {
    fun getFilteredContacts(firstName: String?, lastName: String?, category: ContactCategory?, ssn: String?, country: String?, city: String?, pageable: Pageable): Page<Contact>
}