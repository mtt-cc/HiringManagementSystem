package it.polito.waii_24.g20.com_manager.integration

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.annotation.Rollback
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.waii_24.g20.com_manager.configurations.CRMConfig
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Rollback
@Suppress("UNCHECKED_CAST")
class ComManagerIntegrationTests(): IntegrationTest() {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var restTemplate: RestTemplate

    @Autowired
    lateinit var crm: CRMConfig

    private final val toValue = "consegnamoentrole2359@gmail.com"
    private final val subjectValue = "Test Email as last one sent"
    private final val bodyValue = "This email is automatically sent through Camel"

    val to = MockMultipartFile("to", "", "text/plain", toValue.toByteArray())
    val subject = MockMultipartFile("subject", "", "text/plain", subjectValue.toByteArray())
    val body = MockMultipartFile("body", "", "text/plain", bodyValue.toByteArray())

    val wrongTo = MockMultipartFile("wrongTo", "", "text/plain", subjectValue.toByteArray())
    val wrongSubject = MockMultipartFile("wrongSubject", "", "text/plain", subjectValue.toByteArray())
    val wrongBody = MockMultipartFile("wrongBody", "", "text/plain", subjectValue.toByteArray())

    @Test
    fun checkSendEmail() {
        mockMvc.perform(
            multipart("/emails")
                .file(to)
                .file(subject)
                .file(body)
        ).andExpect( status().isAccepted )

        // Perform a GET request to another application running on port 8082 using RestTemplate
        val response: ResponseEntity<String> = restTemplate.getForEntity("${crm.url}/messages?size=10000", String::class.java)

        // Assert the response status and body
        val responseBody = response.body ?: throw IllegalStateException("Response body is null")
        val responseMap = objectMapper.readValue(responseBody, Map::class.java)

        // Extract the last object from the "content" list
        val contentList = responseMap["content"] as? List<Map<*, *>> ?: throw IllegalStateException("Content list not found")
        val lastObject = contentList.lastOrNull() ?: throw IllegalStateException("No content found")
        println("Response from port 8082: $lastObject")
        assert(response.statusCode.is2xxSuccessful)
        assert(lastObject["sender"] == toValue)
        assert(lastObject["subject"] == subjectValue)
        assert(lastObject["body"].toString().removePrefix("\n") == bodyValue)
    }

    @Test
    fun checkSendEmailUnCorrect(){
        mockMvc.perform(
            multipart("/emails")
                .file(wrongTo)
                .file(subject)
                .file(body)
        )
            .andExpect( status().isBadRequest )
            .andExpect(jsonPath("$.detail").value("Required part 'to' is not present."))

        mockMvc.perform(
            multipart("/emails")
                .file(to)
                .file(wrongSubject)
                .file(body)
        )
            .andExpect( status().isBadRequest )
            .andExpect(jsonPath("$.detail").value("Required part 'subject' is not present."))

        mockMvc.perform(
            multipart("/emails")
                .file(to)
                .file(subject)
                .file(wrongBody)
        )
            .andExpect( status().isBadRequest )
            .andExpect(jsonPath("$.detail").value("Required part 'body' is not present."))
    }
}