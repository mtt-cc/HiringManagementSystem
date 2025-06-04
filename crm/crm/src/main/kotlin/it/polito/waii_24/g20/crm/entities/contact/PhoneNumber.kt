package it.polito.waii_24.g20.crm.entities.contact

import it.polito.waii_24.g20.crm.entities.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class PhoneNumber : BaseEntity<Long>() {
    @ManyToOne
    @JoinColumn(name = "contact_id", nullable = false)
    lateinit var contact : Contact

    lateinit var phoneNumber : String

    var deleted : Boolean = false
}

fun PhoneNumber.clean() {
    this.phoneNumber = "**********"
    this.deleted = true
}