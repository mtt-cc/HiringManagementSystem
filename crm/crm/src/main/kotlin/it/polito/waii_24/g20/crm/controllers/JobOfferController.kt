package it.polito.waii_24.g20.crm.controllers

import it.polito.waii_24.g20.crm.common.JobOfferStateLabel
import it.polito.waii_24.g20.crm.common.JobOfferStateLabelEditor
import it.polito.waii_24.g20.crm.dtos.contact.ContactDTO
import it.polito.waii_24.g20.crm.dtos.jobOffer.*
import it.polito.waii_24.g20.crm.services.interfaces.JobOfferService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/jobOffers")
class JobOfferController(private val jobOfferService: JobOfferService) {
    private val logger : Logger = LoggerFactory.getLogger(JobOfferController::class.java)

    @InitBinder
    fun initBinder(binder: WebDataBinder) {
        binder.registerCustomEditor(JobOfferStateLabel::class.java, JobOfferStateLabelEditor())
    }

    @GetMapping("")
    fun getJobOffers(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false) states: List<JobOfferStateLabel>?,
        @RequestParam(required = false) customerId: Long?,
        @RequestParam(required = false) professionalId: Long?,
        @RequestParam(required = false) searchKey: String?
    ) : Page<JobOfferHeaderDTO> {
        try {
            // KAFKA
            jobOfferService.sendKafkaMessage( "GET JOB OFFERS" )

            if (page < 0 || size <= 0) {
                throw IllegalArgumentException("Page and size must be positive")
            }

            return jobOfferService.getAllJobOffers(
                page = page,
                size = size,
                states = states ?: emptyList(),
                customerId = customerId,
                professionalId = professionalId,
                searchKey = searchKey
            )
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    @GetMapping("/{id}")
    fun getJobOffer(
        @PathVariable id: Long
    ) : JobOfferDTO {
        try {
            // KAFKA
            jobOfferService.sendKafkaMessage( "GET JOB OFFER: $id" )

            return jobOfferService.getJobOffer(id)
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    @PostMapping("")
    fun createJobOffer(
        @RequestBody(required = true) jobOfferCreationDTO: JobOfferCreationDTO
    ) : Long {
        try {
            // KAFKA
            jobOfferService.sendKafkaMessage( "CREATE JOB OFFER" )

            logger.info("Creating job offer: $jobOfferCreationDTO")
            val jo = jobOfferService.createJobOffer(jobOfferCreationDTO)
            logger.info("Job offer created: $jo")
            return jo.id
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    @PutMapping("/{id}/skills")
    fun updateJobOfferSkills(
        @PathVariable id: Long,
        @RequestBody(required = true) skills: List<String>
    ) {
        try {
            // KAFKA
            jobOfferService.sendKafkaMessage( "PUT JOB OFFER" )

            logger.info("Updating job offer $id with skills: $skills")
            val jo = jobOfferService.updateJobOfferSkills(id, skills)
            logger.info("Job offer updated: $jo")
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    @PutMapping("/{id}/state")
    fun updateJobOfferState(
        @PathVariable id: Long,
        @RequestBody(required = true) helper: JobOfferStatusHelperDTO
    ) {
        // KAFKA
        jobOfferService.sendKafkaMessage( "PUT JOB OFFER STATE: $id" )

        try {
            logger.info("Updating job offer $id with state: ${helper.status}")
            val jo = jobOfferService.updateJobOfferState(id, helper.status, helper.professionalId)
            logger.info("Job offer updated: $jo")
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    @PutMapping("/{id}/notes")
    fun updateJobOfferNotes(
        @PathVariable id: Long,
        @RequestBody(required = true) helper: JobOfferNotesHelperDTO
    ) {
        try {
            // KAFKA
            jobOfferService.sendKafkaMessage( "PUT JOB OFFER NOTES" )

            logger.info("Updating job offer $id with notes: ${helper.notes}")
            val jo = jobOfferService.updateJobOfferNotes(id, helper.notes)
            logger.info("Job offer updated: $jo")
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    @PutMapping("/{id}/description")
    fun updateJobOfferDescription(
        @PathVariable id: Long,
        @RequestBody(required = true) helper: JobOfferDescriptionHelperDTO
    ) {
        try {
            // KAFKA
            jobOfferService.sendKafkaMessage( "PUT JOB OFFER DESCRIPTION: $id" )

            logger.info("Updating job offer $id with description: ${helper.description}")
            val jo = jobOfferService.updateJobOfferDescription(id, helper.description)
            logger.info("Job offer updated: $jo")
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    @PostMapping("/{id}/candidate/{professionalId}")
    fun candidateProfessionalToJobOffer(
        @PathVariable id: Long,
        @PathVariable professionalId: Long
    ) {
        try {
            // KAFKA
            jobOfferService.sendKafkaMessage( "POST JOB OFFER CANDIDATE: $professionalId - FOR JOB: $id" )

            logger.info("Candidating professional $professionalId to job offer $id")
            jobOfferService.candidateProfessional(id, professionalId)
            logger.info("Professional candidated")
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    @PutMapping("/candidate/{id}")
    fun updateCandidateNotes(
        @PathVariable id: Long,
        @RequestBody(required = true) helper: CandidateHelperDTO
    ) {
        try {
            // KAFKA
            jobOfferService.sendKafkaMessage( "PUT JOB OFFER CANDIDATE NOTES: $id" )

            logger.info("Updating notes of candidate $id with notes ${helper.notes} and ${helper.verified}")
            jobOfferService.updateCandidateNotes(id, helper.notes)
            if (helper.verified) {
                jobOfferService.updateCandidateVerification(id)
            }
            logger.info("Notes updated")
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }
}