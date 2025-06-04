package it.polito.waii_24.g20.crm.repositories

import it.polito.waii_24.g20.crm.entities.jobOffer.Candidate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CandidateRepository : JpaRepository<Candidate, Long>