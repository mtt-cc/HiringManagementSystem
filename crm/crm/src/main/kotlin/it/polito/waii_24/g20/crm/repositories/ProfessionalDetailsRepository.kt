package it.polito.waii_24.g20.crm.repositories

import it.polito.waii_24.g20.crm.common.ProfessionalEmploymentState
import it.polito.waii_24.g20.crm.entities.contact.ProfessionalDetails
import it.polito.waii_24.g20.crm.entities.contact.Skill
import it.polito.waii_24.g20.crm.entities.jobOffer.JobOffer
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProfessionalDetailsRepository : JpaRepository<ProfessionalDetails, Long> {

    @Query("""
        SELECT pd
        FROM ProfessionalDetails pd
        LEFT JOIN pd.skills s
        WHERE (:availableOnly = FALSE OR pd.employmentState = :state)
        AND pd.id NOT IN (
            SELECT c.candidate.id
            FROM JobOffer j
            JOIN j.candidates c
            WHERE j.id = :jobOfferId
        )
        AND s.skill IN :skills
        GROUP BY pd
        HAVING COUNT(s) = :skillsCount
    """)
    fun findBySkillsContainingAll(
        @Param("skills") skills: List<String>,
        @Param("skillsCount") skillsCount: Int,
        @Param("jobOfferId") jobOfferId: Long,
        @Param("availableOnly") availableOnly: Boolean,
        pageable: Pageable,
        @Param("state") state: ProfessionalEmploymentState = ProfessionalEmploymentState.UNEMPLOYED
    ): Page<ProfessionalDetails>

    @Query("""
        SELECT pd
        FROM ProfessionalDetails pd
        WHERE (:availableOnly = FALSE OR pd.employmentState = :state)
        AND pd.id NOT IN (
            SELECT c.candidate.id
            FROM JobOffer j
            JOIN j.candidates c
            WHERE j.id = :jobOfferId
        )
    """)
    fun findBySkillsContainingAllEmpty(
        @Param("jobOfferId") jobOfferId: Long,
        @Param("availableOnly") availableOnly: Boolean,
        pageable: Pageable,
        @Param("state") state: ProfessionalEmploymentState = ProfessionalEmploymentState.UNEMPLOYED
    ): Page<ProfessionalDetails>
}