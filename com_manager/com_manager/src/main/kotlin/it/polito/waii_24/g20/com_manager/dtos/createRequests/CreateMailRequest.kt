package it.polito.waii_24.g20.com_manager.dtos.createRequests

import com.google.api.services.gmail.model.Message
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.activation.DataHandler
import jakarta.mail.Message.RecipientType
import jakarta.mail.internet.MimeMessage
import jakarta.mail.Session
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeBodyPart
import jakarta.mail.internet.MimeMultipart
import jakarta.mail.util.ByteArrayDataSource
import jakarta.validation.constraints.NotBlank
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import java.util.*

/**
 * How to use:
 *
 * The body will be parsed as multipart/form-data, so you can send the attachments as files.
 *
 * Example (Postman / Insomnia):
 * - to [String] -> text
 * - subject [String] -> text
 * - body [String] -> text
 * - attachments [MultipartFile] -> file
 * - attachments [MultipartFile] -> file
 * - attachments [MultipartFile] -> file
 *
 * All the attachments will be understood as [List]<[MultipartFile]>.
 */
@Schema(description = "Data Transfer Object representing the creation of an email that will be sent via Gmail API")
class CreateMailRequest (
    @Schema(description = "The recipient of the email", example = "my.mail@mail.com")
    @NotBlank
    val to: String,

    @Schema(description = "The subject of the email", example = "Meeting at 3pm")
    val subject: String,

    @Schema(description = "The body of the email", example = "Hello, I'm writing to inform you that the meeting has been postponed to 4pm")
    val body: String,

    @Schema(description = "The attachments of the email")
    val attachments: List<MultipartFile> = emptyList()
)

/**
 * Converts a [CreateMailRequest] to a [Message] that can be sent via the Gmail API
 *
 * @return the [Message] object
 */
fun CreateMailRequest.toMessage() = this.createEmail().createMessageWithEmail()

/**
 * Creates a [MimeMessage] from a [CreateMailRequest]
 *
 * @return the [MimeMessage] object
 */
private fun CreateMailRequest.createEmail(): MimeMessage {
    val props = Properties()
    val session = Session.getDefaultInstance(props, null)
    val email = MimeMessage(session)

    email.setFrom(InternetAddress("me@me.com"))
    email.addRecipient(RecipientType.TO, InternetAddress(this.to))
    email.subject = this.subject

    if (this.attachments.isEmpty()) {
        email.setText(this.body)
    }
    else {
        val multipart = MimeMultipart()

        val textPart = MimeBodyPart()
        textPart.setText(this.body)
        multipart.addBodyPart(textPart)

        attachments.forEach {
            val attachmentPart = MimeBodyPart()
            val dataSource = ByteArrayDataSource(it.bytes, it.contentType)
            attachmentPart.dataHandler = DataHandler(dataSource)
            attachmentPart.fileName = it.originalFilename
            multipart.addBodyPart(attachmentPart)
        }

        email.setContent(multipart)
    }

    return email
}

/**
 * Creates a [Message] from a [MimeMessage]
 *
 * @return the [Message] object
 */
private fun MimeMessage.createMessageWithEmail(): Message {
    val buffer = ByteArrayOutputStream()
    this.writeTo(buffer)
    val bytes = buffer.toByteArray()
    val encodedEmail = Base64.getUrlEncoder().encodeToString(bytes)
    val message = Message()
    message.raw = encodedEmail
    return message
}