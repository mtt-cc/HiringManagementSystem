package it.polito.waii_24.g20.com_manager.services

import com.google.api.services.gmail.model.MessagePartBody
import it.polito.waii_24.g20.com_manager.exceptions.ImpossibleToUploadAttachmentException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.util.Base64

interface AttachmentService {
    fun uploadAttachment(url: String, attachment: MessagePartBody, filename: String, mimeType: String): String
}

@Service
class AttachmentServiceImpl (
    val rt: RestTemplate,
) : AttachmentService {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Value("\${spring.security.oauth2.client.provider.keycloak.token-uri}")
    private lateinit var tokenUri: String

    @Value("\${spring.security.oauth2.client.registration.keycloak.client-id}")
    private lateinit var clientId: String

    @Value("\${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private lateinit var clientSecret: String

    private fun getAccessToken(): String {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val body = "grant_type=client_credentials&client_id=$clientId&client_secret=$clientSecret"

        val request = HttpEntity(body, headers)
        val response = rt.postForEntity(tokenUri, request, Map::class.java)

        return response.body?.get("access_token") as? String
            ?: throw RuntimeException("Failed to obtain access token from Keycloak")
    }

    /**
     * Uploads an attachment to the specified URL.
     *
     * @param url[String] The URL where the attachment should be uploaded. Normally it should be the URL of the Document Store add document endpoint.
     * @param attachment[MessagePartBody] The attachment to be uploaded.
     * @param filename[String] The name of the file.
     * @param mimeType[String] The MIME type of the file.
     *
     * @return The ID of the uploaded attachment.
     */
    override fun uploadAttachment(url: String, attachment: MessagePartBody, filename: String, mimeType: String): String {
        logger.info("Uploading attachment to $url")

        val token = getAccessToken()
        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA
        headers.setBearerAuth(token)

        val httpEntity = genHttpEntity(filename, attachment, mimeType, token)

        val rs = try {
            rt.postForEntity(url, httpEntity, String::class.java)
        } catch (e: HttpClientErrorException) {
            throw ImpossibleToUploadAttachmentException("Impossible to upload attachment $filename to $url: ${e.message}")
        }

        logger.info("Attachment uploaded")
        return rs.body ?: throw ImpossibleToUploadAttachmentException("Impossible to upload attachment $filename to $url")
    }

    /**
     * Generates an [HttpEntity] object to be used in the uploadAttachment function. The entity is a multipart.
     *
     * @param filename[String] The name of the file.
     * @param attachment[MessagePartBody] The attachment to be uploaded.
     * @param mimeType[String] The MIME type of the file.
     *
     * @return The generated [HttpEntity]<[MultiValueMap]<[String],[Any]>> object.
     */
    private fun genHttpEntity(filename: String, attachment: MessagePartBody, mimeType: String, token: String): HttpEntity<MultiValueMap<String, Any>> {
        val data = Base64.getUrlDecoder().decode(attachment.data)
        return HttpEntity<MultiValueMap<String, Any>>(
            LinkedMultiValueMap<String, Any>().also {
                it.add("file", getByteArrayEntity(filename, data, mimeType))
            },
            HttpHeaders().also {
                it.contentType = MediaType.MULTIPART_FORM_DATA
                it.setBearerAuth(token)
            }
        )
    }

    /**
     * Generates an [HttpEntity] object to be used in the uploadAttachment function. The entity is a ByteArrayResource representing the single attachment.
     *
     * @param filename[String] The name of the file.
     * @param data[ByteArray] The data of the file.
     * @param mimeType[String] The MIME type of the file.
     *
     * @return The generated [HttpEntity]<[ByteArrayResource]> object.
     */
    private fun getByteArrayEntity(filename: String, data: ByteArray, mimeType: String): HttpEntity<ByteArrayResource> {
        return HttpEntity(
            object: ByteArrayResource(data) {
                override fun getFilename() = filename
            },
            HttpHeaders().also {
                it.contentType = MediaType.parseMediaType(mimeType)
            }
        )
    }
}