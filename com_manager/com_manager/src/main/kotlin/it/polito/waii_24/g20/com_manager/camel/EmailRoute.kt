package it.polito.waii_24.g20.com_manager.camel

import com.google.api.services.gmail.model.Message
import com.google.api.services.gmail.model.MessagePart
import it.polito.waii_24.g20.com_manager.configurations.DSConfig
import it.polito.waii_24.g20.com_manager.dtos.MessageDTO
import it.polito.waii_24.g20.com_manager.dtos.createRequests.CreateMailRequest
import it.polito.waii_24.g20.com_manager.dtos.createRequests.toMessage
import it.polito.waii_24.g20.com_manager.services.AttachmentService
import org.apache.camel.EndpointInject
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.google.mail.GoogleMailEndpoint
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.Base64
import java.util.UUID

/**
 * CAMEL
 */
@Component
class EMailRoute(
    val ds: DSConfig,
    val attachmentService: AttachmentService
) : RouteBuilder() {
    @EndpointInject("google-mail:messages/get")
    lateinit var ep: GoogleMailEndpoint

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun configure() {
        /**
         * Route that listens to incoming emails from the google mail stream and processes them.
         */
        from("google-mail-stream:0?markAsRead=true&scopes=https://mail.google.com")
            .process {
                val id = it.getIn().getHeader("CamelGoogleMailId").toString()
                logger.info("Processing message with id: $id")

                val message = ep.client.users().messages().get("me", id).execute()
                logger.info("Message retrieved: ${message.payload.headers.find { it.name == "Message-ID" }?.value}")

                it.getIn().body = message
                val info = message.extractInformation()

                // Main processing
                val content = mutableListOf<String>()
                val attachments = mutableMapOf<String, Pair<String, String>>()
                logger.info("Parsing message")
                if (message.payload.parts == null) {
                    content.add(String(Base64.getUrlDecoder().decode(message.payload.body.data)))
                } else {
                    parse(message.payload.mimeType, message.payload.parts, content, attachments)
                }
                logger.info("Message parsed")

                // TODO this stuff should be done in a transaction (SAGAS)

                // Download attachments from google and save in the document store (now just in a folder)
                logger.info("Processing attachments")
                val processedAttachments = attachments.processAttachments(ep, id, attachmentService)
                logger.info("Attachments processed")

                logger.info("Processing content")
                val processedContent = content.processContent(processedAttachments)
                logger.info("Content processed")

                it.getIn().body = MessageDTO(
                    info["from"]!!,
                    info["date"]!!,
                    info["subject"]!!,
                    processedContent,
                    processedAttachments.values.toList()
                )
                logger.info("Message processed, sending it to the CRM")
            }
            .to("bean:messageServiceImpl?method=registerIncomingMessage")


        /**
         * Route that sends an email using the google mail API.
         */
        from("direct:sendEmail")
            .process {
                val mail = it.getIn().body as CreateMailRequest

                try {
                    logger.info("Trying to send the email")
                    ep.client.users().messages().send("me", mail.toMessage()).execute()
                } catch (e: Exception) {
                    logger.error("Error while sending email: ${e.message}")
                    throw e
                }
            }
    }

    /**
     * Extracts the general information from a message.
     *
     * @return A [Map]<[String],[String]> containing the following keys:
     *  - "from": the sender of the message
     *  - "date": the date of the message
     *  - "messageId": the message id
     *  - "subject": the subject of the message
     */
    private fun Message.extractInformation(): Map<String, String> {
        val fromValue = this.payload.headers.find { it.name == "From" }?.value!!

            // .split("<")[1].trimEnd('>')
        return mapOf(
            "from" to "${extractEmail(fromValue)}",
            "date" to "${this.payload.headers.find { it.name == "Date" }?.value}",
            "messageId" to "${this.payload.headers.find { it.name == "Message-ID" }?.value}",
            "subject" to "${this.payload.headers.find { it.name == "Subject" }?.value}"
        )
    }

    /**
     * Extracts the email from a string.
     *
     * @param text[String] The text to extract the email from.
     *
     * @return The email as [String] if found, null otherwise.
     */
    private fun extractEmail(text: String): String? {
        val emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        val regex = Regex(emailPattern)
        val matchResult = regex.find(text)
        return matchResult?.value
    }

    /**
     * Processes the attachments of a message.
     * This function is an extension of a [MutableMap]<[String],[Pair]<[String],[String]>>.
     * This data structure is defined in the main route and populated with the attachments of the message by the parse function.
     *
     * The semantics of the [Map] is:
     * - key [String]: the cid of the attachment
     * - value [Pair]<[String],[String]>: the attachment id [String] and the MIME type [String] of the attachment
     *
     * @param ep[GoogleMailEndpoint] The google mail endpoint.
     * @param messageId[String] The id of the message.
     * @param srv[AttachmentService] The attachment service.
     *
     * @return A [Map]<[String],[Long]> containing the processed attachments. The key is the cid [String] of the attachment and
     * the value is the id [Long] of the attachment in the document store.
     */
    private fun MutableMap<String, Pair<String, String>>.processAttachments(
        ep: GoogleMailEndpoint,
        messageId: String,
        srv: AttachmentService
    ): MutableMap<String, Long> {
        return this.mapValues { (filename, attach) ->
            val attachment = ep.client.users().messages().attachments().get("me", messageId, attach.first).execute()
            val mimeType = attach.second

            srv.uploadAttachment(ds.url + ds.addRoute, attachment, filename, mimeType).toLong()
        }.toMutableMap()
    }

    /**
     * Processes the content of a message.
     * The content of the message is parsed in a list of strings, where each string represent a single part of the message.
     * This extension function iterate on the list of this parts, replace the cid with the url of the embedded attachment in the
     * document store and collapse the list in a single string.
     *
     * @param attachments[MutableMap]<[String],[Long]> The attachments of the message.
     *
     * @return The processed content of the message as [String].
     */
    private fun MutableList<String>.processContent(attachments: MutableMap<String, Long>): String {
        var content = ""

        val cidRegex = Regex("\"cid:(.*?)\"")
        this.forEach { part ->
            content += "\n"
            content += cidRegex.replace(part) { match ->
                val cidVal = match.groupValues[1]
                val id = attachments[cidVal]

                if (id != null) {
                    attachments.remove(cidVal)
                    "\"${ds.embeddedAttachmentRoute.replace("{id}", id.toString())}\""
                } else {
                    "\"---NOT-FOUND---\""
                }
            }
        }

        return content
    }

    /**
     * Parses the message parts recursively.
     *
     * @param mimeType[String] The MIME type of the part analyzed (initially the mime of the global message).
     * @param parts[List]<[MessagePart]> The parts of the message.
     * @param content[MutableList]<[String]> The content of the message.
     * @param attachmentIds[MutableMap]<[String],[Pair]<[String],[String]>> The attachments of the message.
     */
    private fun parse(
        mimeType: String,
        parts: List<MessagePart>,
        content: MutableList<String>,
        attachmentIds: MutableMap<String, Pair<String, String>>
    ) {
        /** Analyze the main MIME type*/
        when (mimeType) {
            /** Simple case: if it is a text, decode it and push it in the content list */
            "text/plain", "text/html" -> {
                content.add(String(Base64.getUrlDecoder().decode(parts[0].body.data)))
            }

            /** Multipart alternative: this means that all the parts represent the same content with different encoding.
             * We choose to consider only the first HTML part
             * */
            "multipart/alternative" -> {
                // we are interested in the first part with MIME type "text/html", we parse it
                parts.asSequence().filter { it.mimeType == "text/html" }.first().let {
                    parse(it.mimeType, listOf(it), content, attachmentIds)
                }
            }

            /**
             * Multipart mixed or related: this means that the parts are either content or attachments.
             * We parse each part recursively.
             */
            "multipart/mixed", "multipart/related" -> {
                // we parse each part as either content or attachment, this is done recursively
                parts.forEach {
                    parse(it.mimeType, it.parts ?: listOf(it), content, attachmentIds)
                }
            }

            /** Default case: the parts are attachments, they are parsed as attachments according with them MIME type */
            else -> {
                parts.forEach {
                    val cid = it.headers?.find { it.name == "Content-ID" }?.value?.trim('<', '>') ?: UUID.randomUUID().toString()
                    attachmentIds[cid] = Pair(it.body.attachmentId, mimeType)
                }
            }
        }
    }
}