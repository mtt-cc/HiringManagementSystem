package it.polito.waii_24.g20.crm.dtos.jobOffer

import it.polito.waii_24.g20.crm.dtos.contact.ContactDTO
import it.polito.waii_24.g20.crm.dtos.contact.toDTO
import it.polito.waii_24.g20.crm.entities.jobOffer.Candidate


data class CandidateDTO(
    val id: Long,
    val candidate: ContactDTO,
    val notes: String,
    val verified: Boolean
)

fun Candidate.toDTO() = CandidateDTO(
    id = this.id!!,
    candidate = this.candidate.contact.toDTO(),
    notes = this.notes,
    verified = this.verified
)