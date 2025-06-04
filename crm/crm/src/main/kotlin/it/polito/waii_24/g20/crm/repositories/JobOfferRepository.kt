package it.polito.waii_24.g20.crm.repositories

import it.polito.waii_24.g20.crm.entities.jobOffer.JobOffer
import it.polito.waii_24.g20.crm.repositories.customImplementations.JobOfferRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JobOfferRepository : JpaRepository<JobOffer, Long>, JobOfferRepositoryCustom