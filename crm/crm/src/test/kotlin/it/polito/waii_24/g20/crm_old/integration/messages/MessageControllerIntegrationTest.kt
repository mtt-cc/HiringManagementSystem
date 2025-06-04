package it.polito.waii_24.g20.crm_old.integration.messages

import it.polito.waii_24.g20.crm_old.dtos.createDTOs.CreateMessageDTO
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
import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Rollback
class MessageControllerIntegrationTest: IntegrationTest(){

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun checkGetMessages() {
        createCorrectMessage()

        // Check if the message is in the database
        mockMvc.get("/messages")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("\$.content[0].sender") { value("example@gmail.com") }
                jsonPath("\$.content[0].subject") { value("World") }
                jsonPath("\$.content[0].attachments[0]") { value(1) }
                jsonPath("\$.content[0].attachments[1]") { value(2) }
                jsonPath("\$.content[0].attachments[2]") { value(3) }
                jsonPath("\$.content[0].attachments[3]") { value(4) }
                jsonPath("\$.content[0].body") { value("test") }
                jsonPath("\$.content[0].actualMessageState.value") { value("RECEIVED") }
                jsonPath("\$.content[0].channel") { value("Email") }
            }
        // Check if the filtered message is in the database
        mockMvc.get("/messages?state=received")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("\$.content[0].sender") { value("example@gmail.com") }
                jsonPath("\$.content[0].subject") { value("World") }
                jsonPath("\$.content[0].body") { value("test") }
                jsonPath("\$.content[0].attachments[0]") { value(1) }
                jsonPath("\$.content[0].attachments[1]") { value(2) }
                jsonPath("\$.content[0].attachments[2]") { value(3) }
                jsonPath("\$.content[0].attachments[3]") { value(4) }
                jsonPath("\$.content[0].actualMessageState.value") { value("RECEIVED") }
                jsonPath("\$.content[0].channel") { value("Email") }
            }
        // Check if the filtered message is not in the database
        mockMvc.get("/messages?state=read")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
            }
        // Check if the filter is wrong
        mockMvc.get("/messages?state=Wrong")
            .andExpect {
                status { isBadRequest()  }
                content { contentType("application/problem+json") }
            }
    }

    @Test
    fun checkAddMessage() {
        // Add a message to the database with correct mail
        mockMvc.post("/messages") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateMessageDTO(
                    "example@gmail.com",
                    LocalDateTime.now().toString(),
                    "World",
                    listOf(1,2,3,4),
                    "test",
                    "Email",
                    "received"
                )
            )
        }.andExpect {
            status { isOk() }
        }
        // Add a message to the database with correct phone number
        mockMvc.post("/messages") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateMessageDTO(
                    "+39 5676235487",
                    LocalDateTime.now().toString(),
                    "World",
                    listOf(1,2,3,4),
                    "test",
                    "PhoneNumber",
                    "received"
                )
            )
        }.andExpect {
            status { isOk() }
        }
        // Add a message to the database with wrong mail
        mockMvc.post("/messages") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateMessageDTO(
                    "example.com",
                    LocalDateTime.now().toString(),
                    "World",
                    listOf(1,2,3,4),
                    "test",
                    "Email",
                    "received"
                )
            )
        }.andExpect {
            status { isBadRequest() }
        }
        // Add a message to the database with wrong phone number
        mockMvc.post("/messages") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateMessageDTO(
                    "23234134234134",
                    LocalDateTime.now().toString(),
                    "World",
                    listOf(1,2,3,4),
                    "test",
                    "PhoneNumber",
                    "received"
                )
            )
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun checkGetMessageById() {
        val msgId = createCorrectMessage()

        // Get the message by its ID
        mockMvc.get("/messages/{msg_id}", msgId)
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("\$.sender") { value("example@gmail.com") }
                jsonPath("\$.subject") { value("World") }
                jsonPath("\$.body") { value("test") }
                jsonPath("\$.actualMessageState.value") { value("RECEIVED") }
                jsonPath("\$.channel") { value("Email") }
            }
        // Get the message by a wrong ID
        mockMvc.get("/messages/45")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun checkUpdateMessageState() {
        createCorrectMessage()

        // Update the message to a wrong state
        mockMvc.post("/messages/1") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    "targetState" to "Wrong",
                    "comment" to "Comment of the non existent state"
                )
            )
        }.andExpect {
            status { isBadRequest() }
        }

        // Update the message to a non-existent state
        mockMvc.post("/messages/1") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    "targetState" to "Done",
                    "comment" to "Comment of the invalid state"
                )
            )
        }.andExpect {
            status { isBadRequest() }
        }

        // Update the message to a valid state
        mockMvc.post("/messages/1") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    "targetState" to "Read",
                    "comment" to "Comment of the message"
                )
            )
        }.andExpect {
            status { isOk() }
        }

        // Check if the message is correctly updated
        mockMvc.get("/messages/1")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("\$.actualMessageState.value") { value("READ") }
            }

        // Try to update a non-existent message (id)
        mockMvc.post("/messages/40") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    "targetState" to "Read",
                    "comment" to "Comment of the message"
                )
            )
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun checkGetMessageHistory() {
        val msgId = createCorrectMessage()

        // Update the message to a valid state
        mockMvc.post("/messages/{msgId}", msgId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    "targetState" to "Read",
                    "comment" to "Comment of the message"
                )
            )
        }.andExpect {
            status { isOk() }
        }

        // Get the history of the message
        mockMvc.get("/messages/{msgId}/history", msgId)
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("\$[0].fromState") { value("received") }
                jsonPath("\$[0].toState") { value("read") }
                jsonPath("\$[0].comments") { value("Comment of the message") }
            }

        // Get the history of a non-existent message
        mockMvc.get("/messages/5/history")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun checkUpdateMessagePriority() {
        val msgId = createCorrectMessage()

        // Update the priority of the message
        mockMvc.put("/messages/{msgId}/priority", msgId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    "newPriority" to "3"
                )
            )
        }.andExpect {
            status { isOk() }
        }

        // Check if the message is correctly updated
        mockMvc.get("/messages/{msgId}", msgId)
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("\$.priority") { value(3) }
            }

        // Try to update a non-existent message (id)
        mockMvc.put("/messages/673/priority") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    "newPriority" to 2
                )
            )
        }.andExpect {
            status { isNotFound() }
        }

        // Try to update a message to a wrong priority
        mockMvc.put("/messages/{msgId}/priority", msgId) {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                mapOf(
                    "priority" to 5
                )
            )
        }.andExpect {
            status { isBadRequest() }
        }
    }


    // ------------------ Utility functions ------------------
    private fun createCorrectMessage(): Long {
        // Add a message to the database
        val res = mockMvc.post("/messages") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(
                CreateMessageDTO(
                    "example@gmail.com",
                    LocalDateTime.now().toString(),
                    "World",
                    attachments = listOf(1,2,3,4),
                    "test",
                    "Email",
                    "received"
                )
            )
        }.andExpect {
            status { isOk() }
        }
        return res.andReturn().response.contentAsString.toLong()
    }
}