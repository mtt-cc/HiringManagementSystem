package it.polito.waii_24.g20.crm.dtos.jobOffer

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import it.polito.waii_24.g20.crm.common.JobOfferStateLabel
import it.polito.waii_24.g20.crm.common.JobOfferStateLabelDeserializer

data class JobOfferDescriptionHelperDTO(
    val description: String
)

data class JobOfferNotesHelperDTO(
    val notes: String
)

data class JobOfferStatusHelperDTO(
    @JsonDeserialize(using = JobOfferStateLabelDeserializer::class)
    val status: JobOfferStateLabel,
    val professionalId: Long?
)

data class CandidateHelperDTO(
    val notes: String,
    val verified: Boolean
)