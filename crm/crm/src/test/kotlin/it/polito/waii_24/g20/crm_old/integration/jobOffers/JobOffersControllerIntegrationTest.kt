package it.polito.waii_24.g20.crm_old.integration.jobOffers

import it.polito.waii_24.g20.crm_old.dtos.createDTOs.*
import it.polito.waii_24.g20.crm_old.integration.IntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Rollback
class JobOffersControllerIntegrationTest: IntegrationTest() {

    @Autowired
    lateinit var mockMvc: MockMvc


    @Test
    fun checkGetAllJobOffers() {
        // create customer 1 to contact 1
        val customer1 = createCorrectCustomer(createUnknownCategoryContact())
        // create jobOffer 1 to customer 1 // state: created
       createCorrectJobOffer(customer1)

        // Get all job offers from the database
        mockMvc.get("/jobOffers?page=0&size=10")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(1) }
            }

        // create professional
        val professional1 = createCorrectProfessional(createUnknownCategoryContact())
        val customer2 = createCorrectCustomer(createUnknownCategoryContact())

        // create 3 consolidated jobOffers

        createDoneJobOffer(customer1, professional1)
        createDoneJobOffer(customer2, professional1)
        createConsolidatedJobOffer(customer1, professional1)

        // Get all job offers from the database with a specific professionalId
        mockMvc.get("/jobOffers?page=0&size=10&professionalId=$professional1")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(3) }
            }

        // Get all job offers from the database with a specific professionalId that does not exist in the database
        mockMvc.get("/jobOffers?page=0&size=10&professionalId=1000")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(0) }
            }

        // Get all job offers from the database with a specific customerId
        mockMvc.get("/jobOffers?page=0&size=10&customerId={customerId}", customer2)
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(1) }
            }

        // Get all job offers from the database with a specific customerId that does not exist in the database
        mockMvc.get("/jobOffers?page=0&size=10&customerId={customerId}", 1000)
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(0) }
            }

        // Get all job offers from the database with a specific status
        mockMvc.get("/jobOffers?page=0&size=10&status=created")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(1) }
            }

        // Get all job offers from the database with a specific status that does not exist in the database
        mockMvc.get("/jobOffers?page=0&size=10&status=NoState")
            .andExpect {
                status { isBadRequest() }
            }

        // Get all job offers from the database with a specific professionalId and customerId
        mockMvc.get("/jobOffers?page=0&size=10&professionalId={professionalId}&customerId={customerId}", professional1, customer1)
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(2) }
            }

        // Get all job offers from the database with a specific professionalId, customerId and status
    }

    @Test
    fun  checkGetJobOfferValue() {
        val customer = createCorrectCustomer(createUnknownCategoryContact())
        val professional = createCorrectProfessional(createUnknownCategoryContact())
        val job = createConsolidatedJobOffer(customer, professional)
        val expectedValue: Double = 100*10*1.1

        mockMvc.get("/jobOffers/$job/value")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.value") { value(expectedValue) }
            }
    }

    @Test
    fun  checkGetOpenJobOffers() {
        val customer = createCorrectCustomer(createUnknownCategoryContact())
        createCorrectJobOffer(customer)
        createCorrectJobOffer(customer)
        createCorrectJobOffer(customer)
        createSelectionPhaseJobOffer(customer)
        createSelectionPhaseJobOffer(customer)
        createSelectionPhaseJobOffer(customer)
        createCandidateProposalJobOffer(customer)

        mockMvc.get("/jobOffers/open/$customer")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(7) }
            }
    }

    @Test
    fun checkGetAcceptedJobOffers() {
        val customer = createCorrectCustomer(createUnknownCategoryContact())
        val professional1 = createCorrectProfessional(createUnknownCategoryContact())
        createConsolidatedJobOffer(customer, professional1)
        val professional2 = createCorrectProfessional(createUnknownCategoryContact())
        createConsolidatedJobOffer(customer, professional2)
        val professional3 = createCorrectProfessional(createUnknownCategoryContact())
        createConsolidatedJobOffer(customer, professional3)
        val professional4 = createCorrectProfessional(createUnknownCategoryContact())
        createConsolidatedJobOffer(customer, professional4)
        val professional5 = createCorrectProfessional(createUnknownCategoryContact())
        createConsolidatedJobOffer(customer, professional5)

        mockMvc.get("/jobOffers/accepted/$customer")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(5) }
            }
    }

    @Test
    fun  checkGetAbortedJobOffers() {
        val customer1 = createCorrectCustomer(createUnknownCategoryContact())
        val customer2 = createCorrectCustomer(createUnknownCategoryContact())
        val professional1 = createCorrectProfessional(createUnknownCategoryContact())
        val professional2 = createCorrectProfessional(createUnknownCategoryContact())
        createAbortedFromSelectionPhaseJobOffer(customer1)
        createAbortedFromSelectionPhaseJobOffer(customer1)
        createAbortedFromSelectionPhaseJobOffer(customer1)
        createAbortedFromSelectionPhaseJobOffer(customer1)
        createAbortedFromSelectionPhaseJobOffer(customer1)
        createAbortedFromSelectionPhaseJobOffer(customer1)
        createAbortedFromSelectionPhaseJobOffer(customer2)

        // get all aborted
        mockMvc.get("/jobOffers/aborted")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(7) }
            }

        createAbortedFromConsolidatedJobOffer(customer1, professional1)
        createAbortedFromConsolidatedJobOffer(customer1, professional2)
        createAbortedFromConsolidatedJobOffer(customer2, professional2)

        // get aborted with customer
        mockMvc.get("/jobOffers/aborted?customerId={customerId}", customer2)
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(2) }
            }

        // get aborted with professional
        mockMvc.get("/jobOffers/aborted?professionalId={professionalId}", professional2)
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(2) }
            }

        // get aborted with customer and professional
        mockMvc.get("/jobOffers/aborted?customerId={customerId}&professionalId={professionalId}", customer1, professional2)
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(1) }
            }
    }

    @Test
    fun  checkUpdateJobOfferStatus() {
        // create contact 1
        val contactId = createUnknownCategoryContact()
        // create customer 1 to contact 1
        val customerId = createCorrectCustomer(contactId)
        // create jobOffer 1 to customer 1 // state: created
        val jobId = createCorrectJobOffer(customerId)

        // check if the jobOffer has created as a state
        mockMvc.get("/jobOffers") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateJobOfferDTO(
                    setOf("Java", "Python"),
                    "description",
                    "test notes",
                    customerId,
                    10
                )
            )}.andExpect {
            status { isOk() }
            content { contentType("application/json") }
            jsonPath("\$.content[0].status") { value("created") }
        }

        // created to selection_phase
        mockMvc.post("/jobOffers/{jobOfferId}", jobId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    "status" to "selection_phase",
                    "comment" to "comments",
                    //"professionalId" to 1
                )
            )}.andExpect { status { isOk() } }

        // check if the jobOffer has selection_phase as a state
        mockMvc.get("/jobOffers") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateJobOfferDTO(
                    setOf("Java", "Python"),
                    "description",
                    "test notes",
                    customerId,
                    10
                )
            )}.andExpect {
            status { isOk() }
            content { contentType("application/json") }
            jsonPath("\$.content[0].status") { value("selection_phase") }
        }

        // selection_phase to candidate_proposal
        mockMvc.post("/jobOffers/{jobOfferId}", jobId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    "status" to "candidate_proposal",
                    "comment" to "comments",
                    //"professionalId" to 1
                )
            )}.andExpect { status { isOk() } }

        // check if the jobOffer has candidate_proposal as a state
        mockMvc.get("/jobOffers") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateJobOfferDTO(
                    setOf("Java", "Python"),
                    "description",
                    "test notes",
                    customerId,
                    10
                )
            )}.andExpect {
            status { isOk() }
            content { contentType("application/json") }
            jsonPath("\$.content[0].status") { value("candidate_proposal") }
        }

        // create contact 2
        val contactId2 = createUnknownCategoryContact()
        // create professional 1 to contact 2
        val professionalId = createCorrectProfessional(contactId2)

        // candidate_proposal to consolidated
        mockMvc.post("/jobOffers/{jobOfferId}", jobId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    "status" to "consolidated",
                    "comment" to "comments",
                    "professionalId" to professionalId
                )
            )}.andExpect { status { isOk() } }

        // check if the jobOffer has candidate_proposal as a state
        mockMvc.get("/jobOffers") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateJobOfferDTO(
                    setOf("Java", "Python"),
                    "description",
                    "test notes",
                    customerId,
                    10
                )
            )}.andExpect {
            status { isOk() }
            content { contentType("application/json") }
            jsonPath("\$.content[0].status") { value("consolidated") }
            jsonPath("\$.content[0].professionalId") { value(professionalId) }
        }

    }

    @Test
    fun  checkCreateJobOffer() {
        // create contact 1
        val contactId = createUnknownCategoryContact()
        // create customer 1 to contact 1
        val customerId = createCorrectCustomer(contactId)

        // Add a job offer to the database
        mockMvc.post("/jobOffers") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateJobOfferDTO(
                    setOf("Java", "Python"),
                    "description",
                    "test notes",
                    customerId,
                    10
                )
            )}.andExpect { status { isOk() } }

        // Check if the job offer has been added to the database
        mockMvc.get("/jobOffers?page=0&size=10")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(1) }
                jsonPath("$.content[0].requiredSkills[0]") { value("Java") }
                jsonPath("$.content[0].requiredSkills[1]") { value("Python") }
                jsonPath("$.content[0].description") { value("description") }
                jsonPath("$.content[0].notes") { value("test notes") }
                jsonPath("$.content[0].customerId") { value(customerId) }
                jsonPath("$.content[0].duration") { value(10) }
                jsonPath("$.content[0].status") { value("created") }
            }

        // Check wrong creation (duration < 1)
        mockMvc.post("/jobOffers") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateJobOfferDTO(
                    setOf("Java", "Python"),
                    "description",
                    "test notes",
                    customerId,
                    0
                )
            )}.andExpect { status { isBadRequest() } }
    }

    @Test
    fun  checkUpdateSkills() {
        // create contact 1
        val contactId = createUnknownCategoryContact()
        // create customer 1 to contact 1
        val customerId = createCorrectCustomer(contactId)
        // create jobOffer 1 to customer 1 // state: created
        val jobId = createCorrectJobOffer(customerId)

        // Update the skills of the job offer
        mockMvc.put("/jobOffers/$jobId/skills") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf("requiredSkills" to setOf(CreateSkillDTO("Java"), CreateSkillDTO("Python"), CreateSkillDTO("C++")))
            )
        }.andExpect { status { isOk() } }

        // Check if the skills have been updated
        mockMvc.get("/jobOffers/$jobId")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.requiredSkills[0]") { value("Java") }
                jsonPath("$.requiredSkills[1]") { value("C++") }
                jsonPath("$.requiredSkills[2]") { value("Python") }
            }
    }

   @Test
    fun  checkUpdateNotes() {
        // create contact 1
        val contactId = createUnknownCategoryContact()
        // create customer 1 to contact 1
        val customerId = createCorrectCustomer(contactId)
        // create jobOffer 1 to customer 1 // state: created
        val jobId = createCorrectJobOffer(customerId)

        // Update the notes of the job offer
        mockMvc.put("/jobOffers/$jobId/notes") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    "value" to "new notes"
                )
            )
        }.andExpect { status { isOk() } }

        // Check if the notes have been updated
        mockMvc.get("/jobOffers/$jobId")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.notes") { value("new notes") }
            }
    }



    // ------------------- UTILITY -------------------

    private fun createUnknownCategoryContact(): Long {
        // Add a contact with Unknown category to the database
        val contactId = mockMvc.post("/contacts") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateContactDTO(
                    "Pippo",
                    "Baudo",
                    "ssn"
                )
            )
        }.andExpect { status { isOk() } }.andReturn().response.contentAsString.toLong()
        return contactId
    }

    fun createCorrectCustomer(contactId: Long): Long {
        // Add a customer to the database
        val customerId = mockMvc.post("/customers/{contactId}", contactId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateCustomerDTO(
                    "Only graduated professionals",
                    "The best customer"
                )
            )
        }.andExpect { status { isOk() } }.andReturn().response.contentAsString.toLong()
        return customerId
    }

    fun createCorrectProfessional(contactId: Long): Long {
        // Add a professional to the database
        val professionalId = mockMvc.post("/professionals/{contactId}", contactId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateProfessionalDTO(
                    "Torino",
                    100.0,
                    "notes",
                    "Unemployed"
                )
            )}.andExpect { status { isOk() } }
        return professionalId.andReturn().response.contentAsString.toLong()
    }

    fun createCorrectJobOffer(customerId: Long): Long {
        // Add a job offer to the database
        val jobId = mockMvc.post("/jobOffers") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateJobOfferDTO(
                    setOf("Java", "Python"),
                    "description",
                    "test notes",
                    customerId,
                    10
                )
            )}.andExpect { status { isOk() } }.andReturn().response.contentAsString.toLong()
        return jobId
    }

    fun createSelectionPhaseJobOffer(customerId: Long): Long{
        val jobId = createCorrectJobOffer(customerId)

        // from created to selection_phase
        mockMvc.post("/jobOffers/{jobOfferId}", jobId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    "status" to "selection_phase",
                    "comment" to "comments",
                    //"professionalId" to 1
                )
            )}.andExpect { status { isOk() } }
        return jobId
    }

    fun createCandidateProposalJobOffer(customerId: Long): Long{
        val jobId = createSelectionPhaseJobOffer(customerId)
        // from selection_phase to candidate_proposal
        mockMvc.post("/jobOffers/{jobOfferId}", jobId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    "status" to "candidate_proposal",
                    "comment" to "comments",
                )
            )}.andExpect { status { isOk() } }
        return jobId
    }

    fun createConsolidatedJobOffer(customerId: Long, professionalId: Long): Long{
        val jobId = createCandidateProposalJobOffer(customerId)
        // from candidate_proposal to consolidated
        mockMvc.post("/jobOffers/{jobOfferId}", jobId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    "status" to "consolidated",
                    "comment" to "comments",
                    "professionalId" to professionalId
                )
            )}.andExpect { status { isOk() } }
        return jobId
    }

    fun createDoneJobOffer(customerId: Long, professionalId: Long): Long{
        val jobId = createConsolidatedJobOffer(customerId, professionalId)
        // from consolidated to done
        mockMvc.post("/jobOffers/{jobOfferId}", jobId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    "status" to "done",
                    "comment" to "comments",
                )
            )}.andExpect { status { isOk() } }
        return jobId
    }

    fun createAbortedFromSelectionPhaseJobOffer(customerId: Long): Long {
        val jobId = createSelectionPhaseJobOffer(customerId)
        // from consolidated to done
        mockMvc.post("/jobOffers/{jobOfferId}", jobId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    "status" to "aborted",
                    "comment" to "comments",
                )
            )}.andExpect { status { isOk() } }
        return jobId
    }

    fun createAbortedFromConsolidatedJobOffer(customerId: Long, professionalId: Long): Long {
        val jobId = createConsolidatedJobOffer(customerId, professionalId)
        // from consolidated to done
        mockMvc.post("/jobOffers/{jobOfferId}", jobId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    "status" to "aborted",
                    "comment" to "comments",
                )
            )}.andExpect { status { isOk() } }
        return jobId
    }
}
