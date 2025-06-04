package it.polito.waii_24.g20.crm.repositories

import it.polito.waii_24.g20.crm.entities.contact.Skill
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SkillRepository : JpaRepository<Skill, Long> {
    fun findSkillBySkill(skill: String): Skill?
}