package it.polito.waii_24.g20.crm_old.integration.customers

import it.polito.waii_24.g20.crm_old.dtos.createDTOs.CreateContactDTO
import it.polito.waii_24.g20.crm_old.dtos.createDTOs.CreateCustomerDTO
import it.polito.waii_24.g20.crm_old.integration.IntegrationTest
import org.apache.commons.lang3.stream.IntStreams.range
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
class CustomerControllerIntegrationTest: IntegrationTest() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun checkGetCustomers() {
        createCorrectCustomer()
        createCorrectCustomer2()

        // Get all Customers from the database
        mockMvc.get("/customers?page=0&size=10")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(2) }
        }

        // Get all customers from the database with a specific preference
        mockMvc.get("/customers?page=0&size=10&preferences=graduated")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(1) }
        }

        // Get all customers from the database with a specific preference that does not exist in the database
        mockMvc.get("/customers?page=0&size=10&preferences=blue")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(0) }
            }

        // Get all customers from the database with a specific preference and a specific note
        mockMvc.get("/customers?page=0&size=10&preferences=experienced&notes=worst")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(1) }
            }

        // Get all customers from the database with a specific note and a specific preference that does not exist in the database
        mockMvc.get("/customers?page=0&size=10&preferences=blue&notes=yellow")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(0) }
            }

        // Add many customers
        for (i in range(100)){
            createCorrectCustomer()
        }

        // Get all Customers from the database
        mockMvc.get("/customers?page=0&size=10")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(10) }
            }

        // Get all Customers from the database
        mockMvc.get("/customers?page=0&size=200")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.content.size()") { value(102) }
            }
    }

    @Test
    fun checkGetCustomerById() {
        val customerId = createCorrectCustomer()

        // Get the Customer by id
        mockMvc.get("/customers/{customerId}", customerId)
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.preferences") { value("Only graduated professionals") }
                jsonPath("$.notes") { value("The best customer") }
            }

        // Get the Customer by id that does not exist in the database
        mockMvc.get("/customers/{customerId}", 1000)
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun checkAddCustomer() {
        val contactId = createUnknownCategoryContact()

        // Add a Customer to the database with correct data
        mockMvc.post("/customers/{contactId}", contactId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateCustomerDTO(
                    "Only graduated professionals",
                    "A good customer"
                )
            )
        }.andExpect {
            status { isOk() }
        }

        // Add a Customer to the database with correct data and a contact that does not exist
        mockMvc.post("/customers/{contactId}", 1000) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateCustomerDTO(
                    "Only graduated professionals",
                    "A good customer"
                )
            )
        }.andExpect { status { isNotFound() } }

        // Add a Customer to the database with correct data and a contact that is already connected to a Customer or Customer profile
        mockMvc.post("/customers/{contactId}", contactId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateCustomerDTO(
                    "Only graduated professionals",
                    "A good customer"
                )
            )
        }.andExpect { status { isBadRequest() } }
    }

    @Test
    fun checkDeleteCustomer() {
        val customerId = createCorrectCustomer()

        // Delete the Customer from the database
        mockMvc.delete("/customers/{customerId}", customerId)
            .andExpect { status { isOk() } }

        // Check if the Customer has been deleted
        mockMvc.get("/customers/{customerId}", customerId)
            .andExpect {
                status { isNotFound() }
            }

        // Delete the Customer from the database that does not exist
        mockMvc.delete("/customers/{customerId}", 1000)
            .andExpect { status { isNotFound() } }
    }

    @Test
    fun checkUpdatePreferences() {
        val customerId = createCorrectCustomer()

        // Update the preferences of the Customer
        mockMvc.put("/customers/{customerId}/preferences", customerId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(mapOf("value" to "New preference"))
        }.andExpect { status { isOk() } }

        // Check if the preferences have been updated
        mockMvc.get("/customers/{customerId}", customerId)
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("\$.preferences") { value("New preference") }
            }

        // Update the preferences of the Customer that does not exist
        mockMvc.put("/customers/{customerId}/preferences", 1000) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(mapOf("value" to "Another new preference"))
        }.andExpect { status { isNotFound() } }
    }

    @Test
    fun checkUpdateNotes() {
        val customerId = createCorrectCustomer()

        // Update the notes of the Customer
        mockMvc.put("/customers/{customerId}/notes", customerId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf("value" to "A very bad Customer")
            )
        }.andExpect { status { isOk() } }

        // Check if the notes have been updated
        mockMvc.get("/customers/{customerId}", customerId)
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("\$.notes") { value("A very bad Customer") }
            }

        // Update the notes of the Customer that does not exist
        mockMvc.put("/customers/{customerId}/notes", 1000) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf("value" to "Again a very bad Customer")
            )
        }.andExpect { status { isNotFound() } }
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

    private fun createCorrectCustomer(): Long {
        val contactId = createUnknownCategoryContact()
        // Add the Customer to the database
        val customerId = mockMvc.post("/customers/{contactId}", contactId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateCustomerDTO(
                    "Only graduated professionals",
                    //"graduated",
                    "The best customer"
                )
            )
        }.andExpect { status { isOk() } }
        return customerId.andReturn().response.contentAsString.toLong()
    }

    private fun createCorrectCustomer2(): Long {
        val contactId = createUnknownCategoryContact()
        // Add the Customer to the database
        val customerId = mockMvc.post("/customers/{customerId}", contactId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateCustomerDTO(
                    "Only experienced professionals",
                    "The worst customer"
                )
            )
        }.andExpect { status { isOk() } }
        return customerId.andReturn().response.contentAsString.toLong()
    }

}