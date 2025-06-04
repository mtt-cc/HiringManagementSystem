package it.polito.waii_24.g20.crm.services.implementation

import it.polito.waii_24.g20.crm.common.ContactCategory
import it.polito.waii_24.g20.crm.common.JobOfferParams
import it.polito.waii_24.g20.crm.common.JobOfferStateLabel
import it.polito.waii_24.g20.crm.common.ProfessionalEmploymentState
import it.polito.waii_24.g20.crm.dtos.contact.ContactDTO
import it.polito.waii_24.g20.crm.dtos.jobOffer.*
import it.polito.waii_24.g20.crm.entities.contact.Skill
import it.polito.waii_24.g20.crm.entities.jobOffer.Candidate
import it.polito.waii_24.g20.crm.entities.jobOffer.JobOffer
import it.polito.waii_24.g20.crm.entities.jobOffer.JobOfferHistory
import it.polito.waii_24.g20.crm.exceptions.ContactServiceInternalErrorException
import it.polito.waii_24.g20.crm.repositories.*
import it.polito.waii_24.g20.crm.services.interfaces.JobOfferService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import org.springframework.kafka.core.KafkaTemplate

@Service
class JobOfferServiceImpl(
    private val jobOfferRepository: JobOfferRepository,
    private val contactRepository: ContactRepository,
    private val skillRepository: SkillRepository,
    private val jobOfferHistoryRepository: JobOfferHistoryRepository,
    private val candidateRepository: CandidateRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>
) : JobOfferService {
    private val logger : Logger = LoggerFactory.getLogger(JobOfferServiceImpl::class.java)

    override fun getAllJobOffers(page: Int, size: Int, states: List<JobOfferStateLabel>, customerId: Long?, professionalId: Long?, searchKey: String?): Page<JobOfferHeaderDTO> {
        val pageable = PageRequest.of(page, size)
        return jobOfferRepository.getFilteredJobOffers(
            states,
            customerId,
            professionalId,
            searchKey,
            pageable
        ).map { it.toHeaderDTO() }
    }

    @Transactional
    override fun createJobOffer(jobOffer: JobOfferCreationDTO): JobOfferDTO {
        try {
            logger.info("Creating job offer: $jobOffer")
            val customer = contactRepository.findContactByIdAndDeletedIsFalse(jobOffer.customerId).orElseThrow {
                IllegalArgumentException("Customer not found")
            }

            if (customer.category != ContactCategory.CUSTOMER) {
                throw IllegalArgumentException("Contact is not a customer")
            }

            val jo = JobOffer()
            jo.title = jobOffer.title
            jo.description = jobOffer.description
            jo.duration = jobOffer.duration
            jo.budget = jobOffer.budget
            jo.customer = customer.customerDetails!!
            jo.status = JobOfferStateLabel.CREATED
            jo.skills = jobOffer.skills.map { s -> findOrInsertSkill(s) }.toMutableList()
            jo.history = mutableListOf()
            jo.candidates = mutableListOf()
            jo.notes = ""

            val savedJobOffer = jobOfferRepository.save(jo)
            customer.customerDetails!!.jobOffers.add(savedJobOffer)
            contactRepository.save(customer)

            val jobOfferHistory = JobOfferHistory().apply {
                this.jobOffer = savedJobOffer
                this.currentState = JobOfferStateLabel.CREATED
                this.dateTime = LocalDateTime.now()
            }
            jobOfferHistoryRepository.save(jobOfferHistory)
            savedJobOffer.history.add(jobOfferHistory)

            return savedJobOffer.toDTO()
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    override fun getJobOffer(id: Long): JobOfferDTO {
        return jobOfferRepository.findById(id).orElseThrow {
            IllegalArgumentException("Job offer not found")
        }.toDTO()
    }

    override fun updateJobOfferSkills(id: Long, skills: List<String>): JobOfferDTO {
        try {
            logger.info("Updating job offer $id with skills: $skills")
            val jobOffer = jobOfferRepository.findById(id).orElseThrow {
                IllegalArgumentException("Job offer not found")
            }

            logger.info("Job offer found: $jobOffer")
            if (jobOffer.status != JobOfferStateLabel.CREATED && jobOffer.status != JobOfferStateLabel.SELECTION_PHASE) {
                throw IllegalArgumentException("Job offer skills can only be updated in CREATED or SELECTION_PHASE state")
            }

            logger.info("Job offer status: ${jobOffer.status}")
            jobOffer.skills = skills.map { s -> findOrInsertSkill(s) }.toMutableList()

            logger.info("Job offer updated: $jobOffer")
            return jobOfferRepository.save(jobOffer).toDTO()
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    override fun updateJobOfferState(id: Long, state: JobOfferStateLabel, professionalId: Long?): JobOfferDTO {
        try {
            val jobOffer = jobOfferRepository.findById(id).orElseThrow {
                IllegalArgumentException("Job offer not found")
            }

            // this move the current state to the next state (validating the transition)
            val newState : JobOfferStateLabel = jobOffer.status.create().next(state).getValue()

            // BACK TO SELECTION_PHASE
            if (newState == JobOfferStateLabel.SELECTION_PHASE && jobOffer.status !== JobOfferStateLabel.CREATED) {
                logger.info("Rollback of job offer status to SELECTION_PHASE. Removing the professional assigned to the job offer.")
                jobOffer.professional!!.jobOffers.remove(jobOffer)
                jobOffer.professional!!.employmentState = ProfessionalEmploymentState.UNEMPLOYED
                contactRepository.save(jobOffer.professional!!.contact)
                jobOffer.professional = null
                jobOffer.value = 0.0
            }

            // GO TO CANDIDATE_PROPOSAL (allowed only from SELECTION_PHASE)
            if (newState == JobOfferStateLabel.CANDIDATE_PROPOSAL) {
                if (professionalId == null) {
                    throw IllegalArgumentException("Professional id is required for CANDIDATE_PROPOSAL state")
                }

                val professional = contactRepository.findContactByIdAndDeletedIsFalse(professionalId).orElseThrow {
                    IllegalArgumentException("Professional not found")
                }

                if (professional.category != ContactCategory.PROFESSIONAL) {
                    throw IllegalArgumentException("Contact is not a professional")
                }

                if (professional.professionalDetails!!.employmentState != ProfessionalEmploymentState.UNEMPLOYED) {
                    throw IllegalArgumentException("Professional is not unemployed")
                }

                jobOffer.professional = professional.professionalDetails!!
                jobOffer.value = professional.professionalDetails!!.dailyRate * jobOffer.duration * JobOfferParams.PROFIT_MARGIN
                professional.professionalDetails!!.jobOffers.add(jobOffer)
                contactRepository.save(professional)
            }

            // GO TO DONE (allowed only from CONSOLIDATED)
            if (newState == JobOfferStateLabel.DONE) {
                if (jobOffer.professional == null) {
                    throw IllegalArgumentException("Professional is required for DONE state")
                }

                jobOffer.professional!!.employmentState = ProfessionalEmploymentState.UNEMPLOYED
                contactRepository.save(jobOffer.professional!!.contact)
            }

            // GO TO ABORTED (allowed from any state)
            if (newState == JobOfferStateLabel.ABORTED) {
                if (jobOffer.professional != null) {
                    jobOffer.professional!!.employmentState = ProfessionalEmploymentState.UNEMPLOYED
                    contactRepository.save(jobOffer.professional!!.contact)
                }
            }

            val newJobOfferHistory = JobOfferHistory().apply {
                this.jobOffer = jobOffer
                this.currentState = newState
                this.previousState = jobOffer.status
                this.dateTime = LocalDateTime.now()
            }
            jobOfferHistoryRepository.save(newJobOfferHistory)
            jobOffer.history.add(newJobOfferHistory)
            jobOffer.status = newState
            return jobOfferRepository.save(jobOffer).toDTO()
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    override fun updateJobOfferNotes(id: Long, notes: String): JobOfferDTO {
        try {
            logger.info("Updating job offer $id with notes: $notes")
            val jobOffer = jobOfferRepository.findById(id).orElseThrow {
                IllegalArgumentException("Job offer not found")
            }

            logger.info("Job offer found: $jobOffer")
            if (jobOffer.status == JobOfferStateLabel.ABORTED) {
                throw IllegalArgumentException("Job offer notes cannot be updated in ABORTED state")
            }

            logger.info("Job offer status: ${jobOffer.status}")
            jobOffer.notes = notes

            logger.info("Job offer updated: $jobOffer")
            return jobOfferRepository.save(jobOffer).toDTO()
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    override fun updateJobOfferDescription(id: Long, description: String): JobOfferDTO {
        try {
            logger.info("Updating job offer $id with description: $description")
            val jobOffer = jobOfferRepository.findById(id).orElseThrow {
                IllegalArgumentException("Job offer not found")
            }

            logger.info("Job offer found: $jobOffer")
            if (jobOffer.status != JobOfferStateLabel.CREATED && jobOffer.status != JobOfferStateLabel.SELECTION_PHASE) {
                throw IllegalArgumentException("Job offer skills can only be updated in CREATED or SELECTION_PHASE state")
            }

            logger.info("Job offer status: ${jobOffer.status}")
            jobOffer.description = description

            logger.info("Job offer updated: $jobOffer")
            return jobOfferRepository.save(jobOffer).toDTO()
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    override fun candidateProfessional(jobOfferId: Long, professionalId: Long): CandidateDTO {
        try {
            val jobOffer = jobOfferRepository.findById(jobOfferId).orElseThrow {
                IllegalArgumentException("Job offer not found")
            }

            if (jobOffer.status != JobOfferStateLabel.SELECTION_PHASE) {
                throw IllegalArgumentException("Job offer is not in SELECTION_PHASE state")
            }

            val professional = contactRepository.findContactByIdAndDeletedIsFalse(professionalId).orElseThrow {
                IllegalArgumentException("Professional not found")
            }

            val candidate = Candidate().apply {
                this.jobOffer = jobOffer
                this.candidate = professional.professionalDetails!!
                this.notes = ""
            }

            jobOffer.candidates.add(candidate)
            professional.professionalDetails!!.candidations.add(candidate)
            candidateRepository.save(candidate)
            contactRepository.save(professional)
            jobOfferRepository.save(jobOffer)

            return candidate.toDTO()
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    override fun updateCandidateNotes(candidateId: Long, notes: String): CandidateDTO {
        try {
            val candidate = candidateRepository.findById(candidateId).orElseThrow {
                IllegalArgumentException("Candidate not found")
            }

            candidate.notes = notes
            return candidateRepository.save(candidate).toDTO()
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    override fun updateCandidateVerification(candidateId: Long) {
        try {
            val candidate = candidateRepository.findById(candidateId).orElseThrow {
                IllegalArgumentException("Candidate not found")
            }

            if (!candidate.verified) {
                candidate.verified = true
                candidateRepository.save(candidate)
            }
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    private fun findOrInsertSkill(skill: String): Skill {
        try {
            return skillRepository.findSkillBySkill(skill)
                ?: run {
                    val newSkill = skillRepository.save(Skill().apply { this.skill = skill })
                    newSkill
                }
        } catch (e: Exception) {
            logger.debug(e.stackTraceToString())
            throw ContactServiceInternalErrorException("Failed to find or insert skill")
        }
    }

    override fun sendKafkaMessage(message: String) {
        kafkaTemplate.send("test-topic", message)
    }
}