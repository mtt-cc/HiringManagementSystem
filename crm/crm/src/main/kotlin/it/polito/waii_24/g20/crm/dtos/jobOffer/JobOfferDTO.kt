package it.polito.waii_24.g20.crm.dtos.jobOffer

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import it.polito.waii_24.g20.crm.common.JobOfferStateLabel
import it.polito.waii_24.g20.crm.common.JobOfferStateLabelDeserializer
import it.polito.waii_24.g20.crm.dtos.contact.ContactHeaderDTO
import it.polito.waii_24.g20.crm.dtos.contact.toContactHeaderDTO
import it.polito.waii_24.g20.crm.entities.jobOffer.JobOffer

data class JobOfferDTO(
    val id: Long,
    val title: String,
    val description: String,
    @JsonDeserialize(using = JobOfferStateLabelDeserializer::class)
    val status: JobOfferStateLabel,
    val customer: ContactHeaderDTO,
    val duration: Int,
    val value: Double,
    val budget: Double,
    val notes: String,
    val history: List<JobOfferHistoryDTO>,
    val skills: List<String>,
    val professional: ContactHeaderDTO?,
    val candidates: List<CandidateDTO>
)

fun JobOffer.toDTO() = JobOfferDTO(
    id = this.id!!,
    title = this.title,
    description = this.description,
    status = this.status,
    customer = this.customer.contact.toContactHeaderDTO(),
    duration = this.duration,
    value = this.value,
    budget = this.budget,
    notes = this.notes,
    history = this.history.map { it.toDTO() },
    skills = this.skills.map { it.skill },
    professional = this.professional?.contact?.toContactHeaderDTO(),
    candidates = this.candidates.map { it.toDTO() }
)
