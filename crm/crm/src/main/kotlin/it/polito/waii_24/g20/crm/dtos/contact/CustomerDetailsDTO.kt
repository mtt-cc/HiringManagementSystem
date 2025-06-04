package it.polito.waii_24.g20.crm.dtos.contact

import it.polito.waii_24.g20.crm.dtos.jobOffer.JobOfferHeaderDTO
import it.polito.waii_24.g20.crm.dtos.jobOffer.toHeaderDTO
import it.polito.waii_24.g20.crm.entities.contact.CustomerDetails

data class CustomerDetailsDTO(
    val notes: String,
    val preferences: String,
    val jobOffers: List<JobOfferHeaderDTO>,
)

fun CustomerDetails.toDTO() = CustomerDetailsDTO(
    notes = this.notes,
    preferences = this.preferences,
    jobOffers = this.jobOffers.map { it.toHeaderDTO() },
)