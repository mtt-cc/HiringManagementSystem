package it.polito.waii_24.g20.crm.entities.jobOffer

import it.polito.waii_24.g20.crm.entities.BaseEntity
import it.polito.waii_24.g20.crm.entities.contact.ProfessionalDetails
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class Candidate : BaseEntity<Long>() {
    @ManyToOne
    @JoinColumn(name = "professional_details_id", nullable = false)
    lateinit var candidate: ProfessionalDetails

    @ManyToOne
    @JoinColumn(name = "job_offer_id", nullable = false)
    lateinit var jobOffer: JobOffer

    @Column(length = 3005)
    lateinit var notes: String
    var verified: Boolean = false
}