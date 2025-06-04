package it.polito.waii_24.g20.crm.repositories

import it.polito.waii_24.g20.crm.entities.jobOffer.JobOfferHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JobOfferHistoryRepository : JpaRepository<JobOfferHistory, Long>