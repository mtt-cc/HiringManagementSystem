package it.polito.waii_24.g20.crm.entities.contact

import it.polito.waii_24.g20.crm.common.ProfessionalEmploymentState
import it.polito.waii_24.g20.crm.entities.BaseEntity
import it.polito.waii_24.g20.crm.entities.jobOffer.Candidate
import it.polito.waii_24.g20.crm.entities.jobOffer.JobOffer
import jakarta.persistence.*

@Entity
class ProfessionalDetails : BaseEntity<Long>() {
    @OneToOne(mappedBy = "professionalDetails", cascade = [CascadeType.ALL])
    lateinit var contact: Contact

    @OneToMany(mappedBy = "candidate")
    lateinit var candidations: MutableList<Candidate>

    @OneToMany(mappedBy = "professional")
    lateinit var jobOffers: MutableList<JobOffer>

    @ManyToMany
    @JoinTable(
        name = "professional_details_skills",
        joinColumns = [JoinColumn(name = "professional_details_id")],
        inverseJoinColumns = [JoinColumn(name = "skill_id")]
    )
    lateinit var skills: List<Skill>

    lateinit var location: String
    @Column(length = 3005)
    lateinit var notes: String
    var dailyRate: Double = 0.0
    var employmentState: ProfessionalEmploymentState = ProfessionalEmploymentState.NOT_AVAILABLE


    var deleted : Boolean = false
}

fun ProfessionalDetails.clean() {
    this.contact.clean()
    this.skills = emptyList()

    this.location = "**********"
    this.notes = "**********"
    this.dailyRate = 0.0
    this.employmentState = ProfessionalEmploymentState.NOT_AVAILABLE

    this.deleted = true
}