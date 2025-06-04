package it.polito.waii_24.g20.crm.entities.contact

import it.polito.waii_24.g20.crm.entities.BaseEntity
import it.polito.waii_24.g20.crm.entities.jobOffer.JobOffer
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne

@Entity
class CustomerDetails : BaseEntity<Long>() {
    @OneToOne(mappedBy = "customerDetails", cascade = [CascadeType.ALL])
    lateinit var contact : Contact

    @OneToMany(mappedBy = "customer", cascade = [CascadeType.ALL])
    lateinit var jobOffers : MutableList<JobOffer>

    @Column(length = 3005)
    lateinit var notes : String
    @Column(length = 3005)
    lateinit var preferences : String

    var deleted : Boolean = false
}

fun CustomerDetails.clean() {
    this.notes = "**********"
    this.preferences = "**********"

    this.deleted = true
}