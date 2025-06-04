package it.polito.waii_24.g20.crm.repositories.customImplementations

import it.polito.waii_24.g20.crm.common.JobOfferStateLabel
import it.polito.waii_24.g20.crm.entities.jobOffer.JobOffer
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface JobOfferRepositoryCustom {
    fun getFilteredJobOffers(
        states: List<JobOfferStateLabel>,
        customerId: Long?,
        professionalId: Long?,
        searchKey: String?,
        pageable: Pageable
    ): Page<JobOffer>
}