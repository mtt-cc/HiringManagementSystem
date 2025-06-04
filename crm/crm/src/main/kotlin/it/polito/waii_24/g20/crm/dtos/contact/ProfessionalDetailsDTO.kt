package it.polito.waii_24.g20.crm.dtos.contact

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import it.polito.waii_24.g20.crm.common.ProfessionalEmploymentState
import it.polito.waii_24.g20.crm.common.ProfessionalEmploymentStateDeserializer
import it.polito.waii_24.g20.crm.dtos.jobOffer.JobOfferHeaderDTO
import it.polito.waii_24.g20.crm.dtos.jobOffer.toHeaderDTO
import it.polito.waii_24.g20.crm.entities.contact.ProfessionalDetails
import it.polito.waii_24.g20.crm.entities.contact.export

data class ProfessionalDetailsDTO(
    val skills: List<String>,
    val location: String,
    val dailyRate: Double,
    val notes: String,
    @JsonDeserialize(using = ProfessionalEmploymentStateDeserializer::class)
    val employmentState: ProfessionalEmploymentState,
    val jobOffers: List<JobOfferHeaderDTO>,
)

fun ProfessionalDetails.toDTO() = ProfessionalDetailsDTO(
    skills = this.skills.map { it.export() },
    location = this.location,
    dailyRate = this.dailyRate,
    notes = this.notes,
    employmentState = this.employmentState,
    jobOffers = this.jobOffers.map { it.toHeaderDTO() },
)