package it.polito.waii_24.g20.crm.entities.jobOffer

import it.polito.waii_24.g20.crm.common.JobOfferStateLabel
import it.polito.waii_24.g20.crm.entities.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity
class JobOfferHistory : BaseEntity<Long>() {
    @ManyToOne
    @JoinColumn(name = "job_offer_id", nullable = false)
    lateinit var jobOffer: JobOffer

    lateinit var currentState: JobOfferStateLabel
    var previousState: JobOfferStateLabel? = null
    lateinit var dateTime: LocalDateTime
}