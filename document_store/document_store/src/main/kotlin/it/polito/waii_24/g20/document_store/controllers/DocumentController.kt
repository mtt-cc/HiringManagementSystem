package it.polito.waii_24.g20.document_store.controllers

import it.polito.waii_24.g20.document_store.documentation.CustomerMethods.*
import it.polito.waii_24.g20.document_store.documentation.DocumentParameters.*
import it.polito.waii_24.g20.document_store.documentation.PageParameters.*
import it.polito.waii_24.g20.document_store.dtos.*
import it.polito.waii_24.g20.document_store.exceptionHandlers.InvalidDocumentMetadata
import it.polito.waii_24.g20.document_store.services.DocumentService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.*
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

/**
 * This class is the controller for the document related APIs.
 *
 * @property documentService The service that handles the document related operations.
 */
@RestController
@RequestMapping("/documents")
class DocumentController(
    private val documentService: DocumentService,
) {
    // Logger for the DocumentController class
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * This method lists all the documents in the database (with a paged approach).
     * Usage example: GET http://example.com/documents?page=2&size=20
     *
     * @param page The page number to be retrieved.
     * @param size The number of elements to be retrieved.
     * @return The page of [DocumentMetadataDTO] objects.
     * @throws ResponseStatusException (HttpStatus.BAD_REQUEST) If the page or size are negative.
     **/
    @GetAllDocumentMetadata
    @GetMapping("/", "")
    fun listAll(
        @RequestParam(required = false, defaultValue = "0") @PageNumberParam page: Int,
        @RequestParam(required = false, defaultValue = "10") @PageSizeParam size: Int
    ): Page<DocumentMetadataDTO> {
        try {
            if (page < 0 || size < 0)
                throw IllegalArgumentException("Page and size must be positive")

            return documentService.listAll(page, size)
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    /**
     * This method adds a document to the database.
     * Usage example: POST http://example.com/documents
     * Body: form-data with key "file" and value the file to be added.
     *
     * @param file The file to be added.
     * @throws ResponseStatusException (HttpStatus.INTERNAL_SERVER_ERROR) If an internal error occurs.
     * @throws ResponseStatusException (HttpStatus.CONFLICT) If the document already exists.
     * @throws ResponseStatusException (HttpStatus.BAD_REQUEST) If the file name is empty or null.
     */
    @AddDocument
    @PostMapping("/", "")
    fun add(
        @RequestPart("file") @FileBody file: MultipartFile
    ): Long {
        logger.info("File ${file.originalFilename} received: Content-Type: ${file.contentType}, Size: ${file.size} bytes")

        if (file.originalFilename.isNullOrBlank()) {
            logger.info("The file name is empty or null")
            throw InvalidDocumentMetadata("The file name is empty or null")
        }

        val metadataDTO = CreateDocumentMetadataDTO(
            file.originalFilename!!,                        // Assert that the file name is not null as consequence of the previous check
            file.size.toInt(),
            file.contentType ?: "<unknown>",
            LocalDateTime.now().toString()
        )

        val contentDTO = CreateDocumentDTO(file.bytes)

        try {
            return documentService.add(metadataDTO, contentDTO)
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    /**
     * This method retrieves the metadata of a document using the metadata ID as key.
     * Usage example: GET http://example.com/documents/1
     *
     * @param metadataId The ID of the metadata to be retrieved.
     * @return The [DocumentMetadataDTO] object corresponding to the metadata ID.
     * @throws ResponseStatusException (HttpStatus.NOT_FOUND) If the document does not exist.
     */
    @GetDocumentMetadata
    @GetMapping("/{metadataId}")
    fun getMetadata(
//        @ApiParam(value = "The ID of the metadata to be retrieved", required = true)
        @PathVariable("metadataId") @MetadataId metadataId: Long
    ): DocumentMetadataDTO {
        try {
            return documentService.getMetadata(metadataId)
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    /**
     * This method updates the metadata of a document using the metadata ID as key.
     * Usage example: PUT http://example.com/documents/1
     * Body: form-data with key "file" and value the file to be updated.
     *
     * @param metadataId The ID of the metadata to be updated.
     * @param file The file to be updated.
     * @return A message indicating that the document was updated successfully.
     * @throws ResponseStatusException (HttpStatus.NOT_FOUND) If the document does not exist.
     * @throws ResponseStatusException (HttpStatus.INTERNAL_SERVER_ERROR) If an internal error occurs.
     * @throws ResponseStatusException (HttpStatus.CONFLICT) If the document already exists.
     * @throws ResponseStatusException (HttpStatus.BAD_REQUEST) If the file name is empty or null.
     */
    @UpdateDocument
    @PutMapping("/{metadataId}")
    fun update(
        @PathVariable("metadataId") @MetadataId metadataId: Long,
        @RequestPart("file") @FileBody file: MultipartFile
    ): String {
        logger.info("New file ${file.originalFilename} received: Content-Type: ${file.contentType}, Size: ${file.size} bytes - Updating file $metadataId")

        if (file.originalFilename.isNullOrBlank())
            throw InvalidDocumentMetadata("The file name is empty or null")

        val metadataDTO = CreateDocumentMetadataDTO(
            file.originalFilename!!,                        // Assert that the file name is not null as consequence of the previous check
            file.size.toInt(),
            file.contentType ?: "<unknown>",
            LocalDateTime.now().toString()
        )

        val contentDTO = CreateDocumentDTO(file.bytes)

        try {
            documentService.update(metadataId, metadataDTO, contentDTO)
            return "File updated successfully"
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }

    }

    @PutMapping("/{metadataId}/name")
    fun updateName(
        @PathVariable("metadataId") @MetadataId metadataId: Long,
        @RequestBody name: String
    ): String {
        try {
            documentService.updateName(metadataId, name)
            return "Name updated successfully"
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    /**
     * This method deletes a document using the metadata ID as key.
     * Usage example: DELETE http://example.com/documents/1
     *
     * @param metadataId The ID of the metadata to be deleted.
     * @return A message indicating that the document was deleted successfully.
     * @throws ResponseStatusException (HttpStatus.NOT_FOUND) If the document does not exist.
     */
    @DeleteDocument
    @DeleteMapping("/{metadataId}")
    fun delete(@PathVariable("metadataId") @MetadataId metadataId: Long): String {
        try {
            documentService.delete(metadataId)
            return "document $metadataId deleted successfully"
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }


    /**
     * This method retrieves the content of a document using the metadata ID as key. OLD IMPLEMENTATION -> retrieves only raw bytes.
     * Usage example: GET http://example.com/documents/1/data
     *
     * param metadataId The ID of the metadata whose content to be retrieved.
     * return The [DocumentContentDTO] object corresponding to the metadata ID.
     * throws ResponseStatusException (HttpStatus.NOT_FOUND) If the document does not exist.
     * @deprecated
     */
//    @GetMapping("/{metadataId}/data")
//    fun getContent(@PathVariable("metadataId") metadataId: Long): DocumentContentDTO {
//        try {
//            return documentService.getContent(metadataId)
//        } catch (e: Exception) {
//            logger.info(e.message)
//            throw e
//        }
//
//    }

    /**
     * This method retrieves the file using the metadata ID as key, the file will be downloaded from the browser.
     * Usage example: GET http://example.com/documents/1/data
     *
     * @param metadataId The ID of the metadata of the file to be retrieved.
     * @return The [ResponseEntity] with the requested file.
     * @throws ResponseStatusException (HttpStatus.NOT_FOUND) If the document does not exist.
     */
    @GetDocument
    @GetMapping("/{metadataId}/data")
    fun getContent(@PathVariable("metadataId") @MetadataId metadataId: Long): ResponseEntity<ByteArray> {
        try {
            val metadata = documentService.getMetadata(metadataId)
            val content = documentService.getContent(metadataId)

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${metadata.name}")
                .body(content.content)
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }
}