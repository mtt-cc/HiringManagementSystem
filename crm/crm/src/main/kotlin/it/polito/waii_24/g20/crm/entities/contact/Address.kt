package it.polito.waii_24.g20.crm.entities.contact

import it.polito.waii_24.g20.crm.entities.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class Address : BaseEntity<Long>() {
    @ManyToOne
    @JoinColumn(name = "contact_id", nullable = false)
    lateinit var contact : Contact

    lateinit var street : String
    lateinit var number : String
    lateinit var city : String
    lateinit var postalCode : String
    lateinit var country : String

    var deleted : Boolean = false
}

fun Address.clean() {
    this.street = "**********"
    this.number = "**********"
    this.city = "**********"
    this.postalCode = "**********"
    this.country = "**********"

    this.deleted = true
}