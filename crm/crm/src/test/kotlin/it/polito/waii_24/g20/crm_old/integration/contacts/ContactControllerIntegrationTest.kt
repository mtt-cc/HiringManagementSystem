package it.polito.waii_24.g20.crm_old.integration.contacts

import it.polito.waii_24.g20.crm_old.dtos.createDTOs.CreateContactDTO
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
class ContactControllerIntegrationTest: IntegrationTest() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun checkGetAllContacts() {
        createCorrectContact()

        mockMvc.get("/contacts")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("\$.content[0].firstName") { value("Pippo") }
                jsonPath("\$.content[0].lastName") { value("Baudo") }
                jsonPath("\$.content[0].category") { value("Unknown") }
                jsonPath("\$.content[0].ssn") { value("ssn") }
            }
    }

    @Test
    fun checkAddContact() {
        // Add a contact to the database with correct mail
        mockMvc.post("/contacts") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateContactDTO(
                    "Pippo",
                    "Baudo",
                    "ssn"
                )
            )
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun checkGetContactById() {
        val id = createCorrectContact()

        // Get the contact by its ID
        mockMvc.get("/contacts/{id}", id)
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("\$.firstName") { value("Pippo") }
                jsonPath("\$.lastName") { value("Baudo") }
                jsonPath("\$.category") { value("Unknown") }
                jsonPath("\$.ssn") { value("ssn") }
            }

        // Get the contact by a wrong ID
        mockMvc.get("/contacts/{id}", 989)
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun checkUpdateContactCategory() {
        val id = createCorrectContact()

        // Update the contact to a wrong category
        mockMvc.put("/contacts/{id}/category", id) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    "category" to "Wrong category",
                )
            )
        }.andExpect {
            status { isBadRequest() }
        }


        // Update the contact to a null category
        mockMvc.put("/contacts/{id}/category", id) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    "category" to null,
                )
            )
        }.andExpect {
            status { isBadRequest() }
        }

        // Update the contact to a valid category
        mockMvc.put("/contacts/{id}/category", id) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    "category" to "Customer",
                )
            )
        }.andExpect {
            status { isOk() }
        }

        // Check if the contact is correctly updated
        mockMvc.get("/contacts/{id}", id)
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("\$.category") { value("Customer") }
            }

        // Try to update a non-existent contact (id)
        mockMvc.put("/contacts/40/category") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    "category" to "Unknown",
                )
            )
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun checkTelephoneContact(){
        val contactId = createCorrectContact()

        // add telephone
        val telId = mockMvc.post("/contacts/{id}/telephone", contactId){
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    "telephone" to "+39 3553933327"
                )
            )
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString.toLong()

        // get telephone
        mockMvc.get("/contacts"){
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("\$.content[0].phoneNumbers[0].telephone") { value("+39 3553933327") }
        }

        // update telephone
        mockMvc.put("/contacts/{cid}/telephone/{tid}", contactId, telId){
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    "telephone" to "+39 1234567890"
                )
            )
        }.andExpect {
            status { isOk() }
        }

        // get telephone
        mockMvc.get("/contacts"){
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("\$.content[0].phoneNumbers[0].telephone") { value("+39 1234567890") }
        }

        // delete telephone
        mockMvc.delete("/contacts/{cid}/telephone/{tid}", contactId, telId){
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }

        // get telephone
        mockMvc.get("/contacts"){
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("\$.content[0].phoneNumbers") {
                isEmpty()
            }
        }
    }

    @Test
    fun checkAddressContact(){
        val contactId = createCorrectContact()

        // add address
        val addrId = mockMvc.post("/contacts/{id}/address", contactId){
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    //"address" to "", // non in uso
                    "street" to "via street name",
                    "number" to "12",
                    "city" to "Sanremo",
                    "postalCode" to "63821",
                    "country" to "Italy",
                )
            )
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString.toLong()

        // get address
        mockMvc.get("/contacts"){
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("\$.content[0].addresses[0].street") { value("via street name") }
            jsonPath("\$.content[0].addresses[0].number") { value("12") }
            jsonPath("\$.content[0].addresses[0].city") { value("Sanremo") }
            jsonPath("\$.content[0].addresses[0].postalCode") { value("63821") }
            jsonPath("\$.content[0].addresses[0].country") { value("Italy") }
        }

        // update address
        mockMvc.put("/contacts/{cid}/address/{eid}", contactId, addrId){
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    //"address" to "",
                    "street" to "via street name 2",
                    "number" to "12 2",
                    "city" to "Sanremo 2",
                    "postalCode" to "63821 2",
                    "country" to "Italy 2",
                )
            )
        }.andExpect {
            status { isOk() }
        }

        // get address
        mockMvc.get("/contacts"){
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("\$.content[0].addresses[0]") {
                jsonPath("\$.content[0].addresses[0].street") { value("via street name 2") }
                jsonPath("\$.content[0].addresses[0].number") { value("12 2") }
                jsonPath("\$.content[0].addresses[0].city") { value("Sanremo 2") }
                jsonPath("\$.content[0].addresses[0].postalCode") { value("63821 2") }
                jsonPath("\$.content[0].addresses[0].country") { value("Italy 2") }
            }
        }

        // delete address
        mockMvc.delete("/contacts/{cid}/address/{eid}", contactId, addrId){
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }

        // get address
        mockMvc.get("/contacts"){
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("\$.content[0].addresses") { isEmpty() }
        }
    }

    //------------------------

    private fun createCorrectContact(): Long {
        // Add a contact to the database
        val res = mockMvc.post("/contacts") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateContactDTO(
                    "Pippo",
                    "Baudo",
                    "ssn"
                )
            )
        }.andExpect {
            status { isOk() }
        }.andReturn()
        return res.response.contentAsString.toLong()
    }
}