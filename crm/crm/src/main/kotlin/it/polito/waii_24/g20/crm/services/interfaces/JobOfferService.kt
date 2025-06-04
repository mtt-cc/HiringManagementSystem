package it.polito.waii_24.g20.crm.services.interfaces

import it.polito.waii_24.g20.crm.common.JobOfferStateLabel
import it.polito.waii_24.g20.crm.dtos.contact.ContactDTO
import it.polito.waii_24.g20.crm.dtos.jobOffer.CandidateDTO
import it.polito.waii_24.g20.crm.dtos.jobOffer.JobOfferCreationDTO
import it.polito.waii_24.g20.crm.dtos.jobOffer.JobOfferDTO
import it.polito.waii_24.g20.crm.dtos.jobOffer.JobOfferHeaderDTO
import org.springframework.data.domain.Page

interface JobOfferService {
    /**
     * This method is used to get a page of job offers, filtered by the given parameters.
     *
     * @param page The page number
     * @param size The size of the page
     * @param states The states of the job offers
     * @param customerId The id of the customer related to the job offers
     * @param professionalId The id of the professional related to the job offers
     * @param searchKey The search key
     * @return A page of job offers
     */
    fun getAllJobOffers(
        page: Int,
        size: Int,
        states: List<JobOfferStateLabel>,
        customerId: Long?,
        professionalId: Long?,
        searchKey: String?
    ) : Page<JobOfferHeaderDTO>

    /**
     * This method is used to create a new job offer.
     *
     * @param jobOfferCreationDTO The job offer to be created
     * @return The created job offer
     */
    fun createJobOffer(
        jobOffer: JobOfferCreationDTO
    ) : JobOfferDTO

    /**
     * This method is used to get a job offer by its id.
     *
     * @param id The id of the job offer to be retrieved
     * @return The job offer with the given id
     */
    fun getJobOffer(
        id: Long
    ) : JobOfferDTO

    /**
     * This method is used to update the list of skills of a job offer.
     *
     * @param id The id of the job offer to be updated
     * @param skills The new list of skills
     * @return The updated job offer
     */
    fun updateJobOfferSkills(
        id: Long,
        skills: List<String>
    ) : JobOfferDTO

    /**
     * This method is used to update the state of a job offer.
     *
     * @param id The id of the job offer to be updated
     * @param state The new state
     * @return The updated job offer
     */
    fun updateJobOfferState(
        id: Long,
        state: JobOfferStateLabel,
        professionalId: Long?
    ) : JobOfferDTO

    /**
     * This method is used to update the notes of a job offer.
     *
     * @param id The id of the job offer to be updated
     * @param notes The new notes
     * @return The updated job offer
     */
    fun updateJobOfferNotes(
        id: Long,
        notes: String
    ) : JobOfferDTO

    /**
     * This method is used to update the description of a job offer.
     *
     * @param id The id of the job offer to be updated
     * @param description The new description
     * @return The updated job offer
     */
    fun updateJobOfferDescription(
        id: Long,
        description: String
    ) : JobOfferDTO

    /**
     * This method is used to candidate a professional to a job offer.
     *
     * @param jobOfferId The id of the job offer
     * @param professionalId The id of the professional to be candidate
     * @return The candidate entry
     */
    fun candidateProfessional(
        jobOfferId: Long,
        professionalId: Long
    ) : CandidateDTO

    /**
     * This method is used to update the notes of a candidate.
     *
     * @param candidateId The id of the candidate
     * @param notes The new notes
     * @return The updated candidate
     */
    fun updateCandidateNotes(
        candidateId: Long,
        notes: String
    ) : CandidateDTO

    /**
     * This method is used to update the verification of a candidate.
     *
     * @param candidateId The id of the candidate
     * @return The updated candidate
     */
    fun updateCandidateVerification(
        candidateId: Long,
    )

    fun sendKafkaMessage(msg: String)
}