package it.polito.waii_24.g20.crm.repositories

import it.polito.waii_24.g20.crm.entities.contact.PhoneNumber
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PhoneNumberRepository : JpaRepository<PhoneNumber, Long>