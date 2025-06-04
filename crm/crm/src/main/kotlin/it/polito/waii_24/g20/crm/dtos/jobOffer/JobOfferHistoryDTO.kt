package it.polito.waii_24.g20.crm.dtos.jobOffer

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import it.polito.waii_24.g20.crm.common.JobOfferStateLabel
import it.polito.waii_24.g20.crm.common.JobOfferStateLabelDeserializer
import it.polito.waii_24.g20.crm.entities.jobOffer.JobOfferHistory
import java.time.LocalDateTime

data class JobOfferHistoryDTO(
    @JsonDeserialize(using = JobOfferStateLabelDeserializer::class)
    val currentState: JobOfferStateLabel,
    @JsonDeserialize(using = JobOfferStateLabelDeserializer::class)
    val previousState: JobOfferStateLabel?,
    val dateTime: LocalDateTime
)

fun JobOfferHistory.toDTO() = JobOfferHistoryDTO(
    currentState = this.currentState,
    previousState = this.previousState,
    dateTime = this.dateTime
)