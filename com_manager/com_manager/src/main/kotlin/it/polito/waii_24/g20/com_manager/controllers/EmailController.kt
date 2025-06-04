package it.polito.waii_24.g20.com_manager.controllers

import it.polito.waii_24.g20.com_manager.documentation.SendEmail
import it.polito.waii_24.g20.com_manager.dtos.createRequests.CreateMailRequest
import it.polito.waii_24.g20.com_manager.exceptions.ImpossibleToSendEmailException
import it.polito.waii_24.g20.com_manager.exceptions.InvalidEmailAddressException
import org.apache.camel.CamelContext
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.ExchangeBuilder
import org.apache.commons.validator.routines.EmailValidator
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/emails")
class EmailController(private val camelContext: CamelContext, private val producerTemplate: ProducerTemplate) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @SendEmail
    @PostMapping("/", "")
    fun sendEmail(
        @RequestPart("to") to: String,
        @RequestPart("subject") subject: String,
        @RequestPart("body") body: String,
        @RequestPart("attachments", required = false) attachments: List<MultipartFile>?
    ): ResponseEntity<HttpStatus> {
        val mail = CreateMailRequest(to, subject, body, attachments ?: emptyList())
        logger.info("Sending email to ${mail.to} with subject ${mail.subject}")

        if (!EmailValidator.getInstance().isValid(mail.to)) {
            logger.info("Invalid email address $to")
            throw InvalidEmailAddressException("Invalid email address $to")
        }

        try {
            val mailExchange = ExchangeBuilder.anExchange(camelContext)
                .withBody(mail)
                .build()
            producerTemplate.send(
                "direct:sendEmail",
                mailExchange
            )
        } catch (e: Exception) {
            logger.error("Error while sending email: ${e.message}")
            throw ImpossibleToSendEmailException("Error while sending email: ${e.message}")
        }

        logger.info("Email sent successfully")
        return ResponseEntity(HttpStatus.ACCEPTED)
    }
}

