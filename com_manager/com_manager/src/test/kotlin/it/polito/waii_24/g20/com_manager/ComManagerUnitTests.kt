package it.polito.waii_24.g20.com_manager

import io.mockk.every
import io.mockk.mockk


import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import io.mockk.verify
import it.polito.waii_24.g20.com_manager.controllers.EmailController
import it.polito.waii_24.g20.com_manager.exceptions.ImpossibleToSendEmailException
import it.polito.waii_24.g20.com_manager.exceptions.InvalidEmailAddressException
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.ProducerTemplate
import org.junit.jupiter.api.Assertions.assertThrows
import org.springframework.web.multipart.MultipartFile

annotation class Ok

@Ok
@DisplayName("Unit tests for the com manager")
class AllComManagerTests {

    private val camelContext: CamelContext = mockk(relaxed = true)
    private val producerTemplate: ProducerTemplate = mockk(relaxed = true)
    private val emailController = EmailController(camelContext, producerTemplate)

    @Ok
    @Nested
    @DisplayName("/emails")
    inner class ApiEmails {
        @DisplayName("POST /emails")
        @Test
        fun testInvalidEmail() {
            val to = "thisemailisinvalid@invalid@com"
            val subject = "subject"
            val body = "body"
            val attachments: List<MultipartFile>? = null

            assertThrows(InvalidEmailAddressException::class.java) {
                emailController.sendEmail(to, subject, body, attachments)
            }
        }

        @Test
        fun testSendEmailRuntimeException() {
            val to = "valid.email@example.com"
            val subject = "subject"
            val body = "body"
            val attachments: List<MultipartFile>? = null

            every { producerTemplate.send(any<String>(), any<Exchange>()) } throws RuntimeException()

            assertThrows(ImpossibleToSendEmailException::class.java) {
                emailController.sendEmail(to, subject, body, attachments)
            }

            verify(exactly = 1) { producerTemplate.send(any<String>(), any<Exchange>()) }
        }

   }
}
