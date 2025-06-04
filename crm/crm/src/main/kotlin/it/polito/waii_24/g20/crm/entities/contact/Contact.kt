package it.polito.waii_24.g20.crm.entities.contact

import it.polito.waii_24.g20.crm.common.ContactCategory
import it.polito.waii_24.g20.crm.entities.BaseEntity
import jakarta.persistence.*

@Entity
class Contact : BaseEntity<Long>() {

    @OneToMany(mappedBy = "contact", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    lateinit var emails : MutableList<Email>

    @OneToMany(mappedBy = "contact", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    lateinit var phoneNumbers : MutableList<PhoneNumber>

    @OneToMany(mappedBy = "contact", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    lateinit var addresses : MutableList<Address>

    @OneToOne(optional = true, fetch = FetchType.LAZY)
    var customerDetails : CustomerDetails? = null

    @OneToOne(optional = true, fetch = FetchType.LAZY)
    var professionalDetails : ProfessionalDetails? = null

    lateinit var firstName: String
    lateinit var lastName: String
    lateinit var ssn: String
    lateinit var category: ContactCategory

    var deleted : Boolean = false
}

fun Contact.clean() {
    this.emails.map { it.clean() }
    this.phoneNumbers.map { it.clean() }
    this.addresses.map { it.clean() }
    this.customerDetails?.clean()
    this.professionalDetails?.clean()

    this.firstName = "**********"
    this.lastName = "**********"
    this.ssn = "**********"
    this.category = ContactCategory.UNKNOWN

    this.deleted = true
}