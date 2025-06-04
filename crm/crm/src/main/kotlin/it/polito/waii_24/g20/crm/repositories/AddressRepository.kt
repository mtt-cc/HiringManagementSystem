package it.polito.waii_24.g20.crm.repositories

import it.polito.waii_24.g20.crm.entities.contact.Address
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AddressRepository : JpaRepository<Address, Long>