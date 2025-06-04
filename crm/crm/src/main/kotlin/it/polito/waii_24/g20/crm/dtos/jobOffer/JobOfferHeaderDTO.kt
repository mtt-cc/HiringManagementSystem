package it.polito.waii_24.g20.crm.dtos.jobOffer

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import it.polito.waii_24.g20.crm.common.JobOfferStateLabel
import it.polito.waii_24.g20.crm.common.JobOfferStateLabelDeserializer
import it.polito.waii_24.g20.crm.dtos.contact.ContactHeaderDTO
import it.polito.waii_24.g20.crm.dtos.contact.toContactHeaderDTO
import it.polito.waii_24.g20.crm.entities.jobOffer.JobOffer

data class JobOfferHeaderDTO(
    val id: Long,
    val title: String,
    val description: String,
    @JsonDeserialize(using = JobOfferStateLabelDeserializer::class)
    val status: JobOfferStateLabel,
    val customer: ContactHeaderDTO
)

fun JobOffer.toHeaderDTO() = JobOfferHeaderDTO(
    id = this.id!!,
    title = this.title,
    description = this.description,
    status = this.status,
    customer = this.customer.contact.toContactHeaderDTO()
)