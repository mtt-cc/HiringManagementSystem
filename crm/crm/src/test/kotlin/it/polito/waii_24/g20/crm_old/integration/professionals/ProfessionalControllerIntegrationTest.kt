package it.polito.waii_24.g20.crm_old.integration.professionals

import it.polito.waii_24.g20.crm_old.dtos.createDTOs.CreateContactDTO
import it.polito.waii_24.g20.crm_old.dtos.createDTOs.CreateProfessionalDTO
import it.polito.waii_24.g20.crm_old.dtos.createDTOs.CreateSkillDTO
import it.polito.waii_24.g20.crm_old.integration.IntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.web.servlet.*
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Rollback
class ProfessionalControllerIntegrationTest: IntegrationTest() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun checkGetProfessionals() {
        val proId = createCorrectProfessional()
        createCorrectProfessional2()

        // Get all professionals from the database
        mockMvc.get("/professionals?page=0&size=10")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(2) }
        }

        // Get all professionals from the database with a specific location
        mockMvc.get("/professionals?page=0&size=10&location=Turin")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(1) }
        }

        // Get all professionals from the database with a specific location that does not exist in the database
        mockMvc.get("/professionals?page=0&size=10&location=Rome")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(0) }
            }

        // Add a skill to the professional
        mockMvc.post("/professionals/{proId}/skills", proId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateSkillDTO("Python")
            )
        }.andExpect { status { isOk() } }

        // Get all professionals from the database with a specific location and a specific skill
        mockMvc.get("/professionals?page=0&size=10&location=Turin&skills=Python")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(1) }
            }

        // Get all professionals from the database with a specific location and a specific skill that does not exist in the database
        mockMvc.get("/professionals?page=0&size=10&location=Turin&skills=Java")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(0) }
            }

        // Add another skill to the professional
        mockMvc.post("/professionals/{proId}/skills", proId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateSkillDTO("Math")
            )
        }.andExpect { status { isOk() } }

        // Get all professionals from the database with a specific location and two specific skills
        mockMvc.get("/professionals?page=0&size=10&location=Turin&skills=Python,Math")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(1) }
            }
    }

    @Test
    fun checkGetProfessionalById() {
        val proId = createCorrectProfessional()

        // Get the professional by id
        mockMvc.get("/professionals/{proId}", proId)
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.location") { value("Turin") }
                jsonPath("$.dailyRate") { value(100.0) }
                jsonPath("$.notes") { value("I'm a professional") }
                jsonPath("$.employmentState") { value("UNEMPLOYED") }
            }

        // Get the professional by id that does not exist in the database
        mockMvc.get("/professionals/{proId}", 1000)
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun checkAddProfessional() {
        val contactId = createUnknownCategoryContact()

        // Add a professional to the database with correct data
        mockMvc.post("/professionals/{contactId}", contactId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateProfessionalDTO(
                    "Turin",
                    100.0,
                    "I'm a professional",
                    "Unemployed"
                )
            )
        }.andExpect {
            status { isOk() }
        }

        // Add a professional to the database with correct data and a contact that does not exist
        mockMvc.post("/professionals/{contactId}", 1000) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateProfessionalDTO(
                    "Turin",
                    100.0,
                    "I'm a professional",
                    "Unemployed"
                )
            )
        }.andExpect { status { isNotFound() } }

        // Add a professional to the database with correct data and a contact that is already connected to a Professional or Customer profile
        mockMvc.post("/professionals/{contactId}", contactId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateProfessionalDTO(
                    "Turin",
                    100.0,
                    "I'm a professional",
                    "Unemployed"
                )
            )
        }.andExpect { status { isBadRequest() } }

        // Add a professional with missing data on the body
        mockMvc.post("/professionals/{contactId}", contactId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateProfessionalDTO(
                    "Turin",
                    100.0,
                    "I'm a professional"
                )
            )
        }.andExpect { status { isBadRequest() } }

        // Add a professional with wrong daily rate
        mockMvc.post("/professionals/{contactId}", contactId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateProfessionalDTO(
                    "Turin",
                    -100.0,
                    "I'm a professional",
                    "Unemployed"
                )
            )
        }.andExpect { status { isBadRequest() } }
    }

    @Test
    fun checkDeleteProfessional() {
        val proId = createCorrectProfessional()
        addSkillToProfessional(proId, "Python")

        // Delete the professional from the database
        mockMvc.delete("/professionals/{proId}", proId)
            .andExpect { status { isOk() } }

        // Check if the professional has been deleted
        mockMvc.get("/professionals/{proId}", proId)
            .andExpect {
                status { isNotFound() }
            }

        // Delete the professional from the database that does not exist
        mockMvc.delete("/professionals/{proId}", 1000)
            .andExpect { status { isNotFound() } }
    }

    @Test
    fun checkUpdateLocation() {
        val proId = createCorrectProfessional()

        // Update the location of the professional
        mockMvc.put("/professionals/{proId}/location", proId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(mapOf("value" to "Rome"))
        }.andExpect { status { isOk() } }

        // Check if the location has been updated
        mockMvc.get("/professionals/{proId}", proId)
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("\$.location") { value("Rome") }
            }

        // Update the location of the professional that does not exist
        mockMvc.put("/professionals/{proId}/location", 1000) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(mapOf("value" to "Milan"))
        }.andExpect { status { isNotFound() } }
    }

    @Test
    fun checkUpdateDailyRate() {
        val proId = createCorrectProfessional()

        // Update the daily rate of the professional
        mockMvc.put("/professionals/{proId}/dailyRate", proId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(mapOf("value" to 200.0))
        }.andExpect { status { isOk() } }

        // Check if the daily rate has been updated
        mockMvc.get("/professionals/{proId}", proId)
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("\$.dailyRate") { value(200.0) }
            }

        // Update the daily rate of the professional that does not exist
        mockMvc.put("/professionals/{proId}/dailyRate", 1000) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(mapOf("value" to 200.0))
        }.andExpect { status { isNotFound() } }
    }

    @Test
    fun checkUpdateNotes() {
        val proId = createCorrectProfessional()

        // Update the notes of the professional
        mockMvc.put("/professionals/{proId}/notes", proId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf("value" to "I'm a professional but I'm not unemployed")
            )
        }.andExpect { status { isOk() } }

        // Check if the notes have been updated
        mockMvc.get("/professionals/{proId}", proId)
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("\$.notes") { value("I'm a professional but I'm not unemployed") }
            }

        // Update the notes of the professional that does not exist
        mockMvc.put("/professionals/{proId}/notes", 1000) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf("value" to "I'm a professional but I'm not unemployed")
            )
        }.andExpect { status { isNotFound() } }
    }

    @Test
    fun checkUpdateEmploymentState() {
        val proId = createCorrectProfessional()

        // Update the employment state of the professional
        mockMvc.put("/professionals/{proId}/employmentState", proId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(mapOf("value" to "Employed"))
        }.andExpect { status { isOk() } }

        // Check if the employment state has been updated
        mockMvc.get("/professionals/{proId}", proId)
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("\$.employmentState") { value("EMPLOYED") }
            }

        // Update the employment state of the professional that does not exist
        mockMvc.put("/professionals/{proId}/employmentState", 1000) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(mapOf("value" to "Wrong"))
        }.andExpect { status { isBadRequest() } }

        // Update the employment state of the professional that does not exist
        mockMvc.put("/professionals/{proId}/employmentState", 1000) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(mapOf("value" to "Employed"))
        }.andExpect { status { isNotFound() } }
    }

    @Test
    fun checkAddSkillToProfessional() {
        val proId = createCorrectProfessional()

        // Add a skill to the professional
        mockMvc.post("/professionals/{proId}/skills", proId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateSkillDTO(
                    "Python"
                )
            )
        }.andExpect { status { isOk() } }

        // Check if the skill has been added to the professional
        mockMvc.get("/professionals/{proId}", proId)
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.skills.size()") { value(2) }
                jsonPath("$.skills[1].skill") { value("Python") }
            }

        // Add a skill to the professional that does not exist
        mockMvc.post("/professionals/{proId}/skills", 1000) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateSkillDTO(
                    "Python"
                )
            )
        }.andExpect { status { isNotFound() } }
    }

    @Test
    fun checkRemoveSkillToProfessional() {
        val proId = createCorrectProfessional()

        // Add a skill to the professional
        val skillId = mockMvc.post("/professionals/{proId}/skills", proId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateSkillDTO(
                    "Python"
                )
            )
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString.toLong()

        // Remove the skill from the professional
        mockMvc.delete("/professionals/{proId}/skills/{skillId}", proId, skillId)
            .andExpect {
                status { isOk() }
            }

        // Check if the skill has been removed from the professional
        mockMvc.get("/professionals/{proId}", proId)
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.skills.size()") { value(1) }
            }

        // Remove the skill from the professional that does not exist
        mockMvc.delete("/professionals/{proId}/skills/{skillId}", proId, 1000)
            .andExpect {
                status { isNotFound() }
            }
    }


    // ------------------ Utility functions ------------------
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
        }.andExpect { status { isOk() } }
        return contactId.andReturn().response.contentAsString.toLong()
    }

    private fun createProfessionalCategoryContact(): Long {
        // Add a contact with Unknown category to the database
        val res = mockMvc.post("/contacts") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateContactDTO(
                    "Pippo",
                    "Baudo",
                    "ssn"
                )
            )
        }.andExpect { status { isOk() } }
        return res.andReturn().response.contentAsString.toLong()
    }

    private fun createCorrectProfessional(): Long {
        val contactId = createUnknownCategoryContact()
        // Add the professional to the database
        val proId = mockMvc.post("/professionals/{contactId}", contactId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateProfessionalDTO(
                    "Turin",
                    100.0,
                    "I'm a professional",
                    "Unemployed"
                )
            )
        }.andExpect { status { isOk() } }
        addSkillToProfessional(proId.andReturn().response.contentAsString.toLong(), "Math")
        return proId.andReturn().response.contentAsString.toLong()
    }

    private fun createCorrectProfessional2(): Long {
        val contactId = createUnknownCategoryContact()
        // Add the professional to the database
        val proId = mockMvc.post("/professionals/{contactId}", contactId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateProfessionalDTO(
                    "Milan",
                    50.0,
                    "I'm more professional than the other one",
                    "Unemployed"
                )
            )
        }.andExpect { status { isOk() } }
        addSkillToProfessional(proId.andReturn().response.contentAsString.toLong(), "Java")
        return proId.andReturn().response.contentAsString.toLong()
    }

    private fun addSkillToProfessional(id: Long, skill: String): Long {
        // Add a skill to the professional
        val skillId = mockMvc.post("/professionals/{professionalId}/skills", id) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateSkillDTO(
                    skill
                )
            )
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString.toLong()
        return skillId
    }
}