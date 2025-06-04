package it.polito.waii_24.g20.crm.repositories

import it.polito.waii_24.g20.crm.common.ContactCategory
import it.polito.waii_24.g20.crm.entities.contact.Contact
import it.polito.waii_24.g20.crm.repositories.customImplementations.ContactRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ContactRepository : JpaRepository<Contact, Long>, ContactRepositoryCustom {
    fun findContactByIdAndDeletedIsFalse(id: Long): Optional<Contact>
    fun findContactsByCategoryAndDeletedIsFalse(category: ContactCategory): List<Contact>
}