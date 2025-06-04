package it.polito.waii_24.g20.crm.entities.contact

import it.polito.waii_24.g20.crm.entities.BaseEntity
import it.polito.waii_24.g20.crm.entities.jobOffer.JobOffer
import jakarta.persistence.Entity
import jakarta.persistence.ManyToMany

@Entity
class Skill : BaseEntity<Long>() {
    lateinit var skill : String

    @ManyToMany(mappedBy = "skills")
    lateinit var jobOffers : List<JobOffer>

    @ManyToMany(mappedBy = "skills")
    lateinit var professionals : List<ProfessionalDetails>
}

fun Skill.export() = this.skill