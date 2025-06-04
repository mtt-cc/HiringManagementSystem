package it.polito.waii_24.g20.crm.entities.jobOffer

import it.polito.waii_24.g20.crm.common.JobOfferStateLabel
import it.polito.waii_24.g20.crm.entities.BaseEntity
import it.polito.waii_24.g20.crm.entities.contact.CustomerDetails
import it.polito.waii_24.g20.crm.entities.contact.ProfessionalDetails
import it.polito.waii_24.g20.crm.entities.contact.Skill
import jakarta.persistence.*

@Entity
class JobOffer : BaseEntity<Long>() {
    @ManyToOne
    @JoinColumn(name = "customer_details_id", nullable = false)
    lateinit var customer : CustomerDetails

    @ManyToOne
    @JoinColumn(name = "professional_details_id", nullable = true)
    var professional : ProfessionalDetails? = null

    @ManyToMany
    @JoinTable(
        name = "job_offer_skills",
        joinColumns = [JoinColumn(name = "job_offer_id")],
        inverseJoinColumns = [JoinColumn(name = "skill_id")]
    )
    lateinit var skills : MutableList<Skill>

    @OneToMany
    @JoinColumn(name = "job_offer_id")
    lateinit var history : MutableList<JobOfferHistory>

    @OneToMany
    @JoinColumn(name = "job_offer_id")
    lateinit var candidates : MutableList<Candidate>

    lateinit var title: String
    @Column(length = 3005)
    lateinit var description: String
    lateinit var status: JobOfferStateLabel
    var duration: Int = 0
    var value: Double = 0.0
    var budget: Double = 0.0
    lateinit var notes: String
}