package it.polito.waii_24.g20.document_store.services

import it.polito.waii_24.g20.document_store.entities.*
import it.polito.waii_24.g20.document_store.repositories.*
import it.polito.waii_24.g20.document_store.dtos.*
import it.polito.waii_24.g20.document_store.exceptionHandlers.DocumentNotFoundException
import it.polito.waii_24.g20.document_store.exceptionHandlers.DocumentServiceInternalErrorException
import it.polito.waii_24.g20.document_store.exceptionHandlers.DuplicateDocumentException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate

/**
 * [Service] interface for the Document entities.
 */
@Service
interface DocumentService {/**/
    /**
     * Function to list all the documents in the system.
     *
     * @param page[Int] the page number
     * @param size[Int] the size of the page
     * @return[Page<DocumentMetadataDTO>] the page of documents
     */
    fun listAll(page: Int, size: Int): Page<DocumentMetadataDTO>

    /**
     * Function to add a document to the system.
     *
     * @param metadata[CreateDocumentMetadataDTO] the metadata of the document
     * @param content[CreateDocumentDTO] the content of the document
     * @throws Exception DuplicateDocumentException if the document already exists
     * @throws Exception DocumentServiceInternalErrorException if an internal error occurs (failing to save the document)
     */
//    fun add(metadata: CreateDocumentMetadataDTO, content: CreateDocumentDTO): ResponseEntity<Any>
    fun add(metadata: CreateDocumentMetadataDTO, content: CreateDocumentDTO, messageId: Long? = null) : Long

    /**
     * Function to get the metadata of a document.
     *
     * @param id[Long] the id of the document
     * @return[DocumentMetadataDTO] the metadata of the document
     * @throws Exception DocumentNotFoundException if the document is not found
     */
    fun getMetadata(id: Long): DocumentMetadataDTO

    /**
     * Function to update the metadata of a document.
     *
     * @param id[Long] the id of the document's metadata
     * @param metadata[CreateDocumentMetadataDTO] the new metadata
     * @param content[CreateDocumentDTO] the new content
     * @throws Exception DocumentNotFoundException if the document is not found
     * @throws Exception DocumentServiceInternalErrorException if an internal error occurs (failing to save the document)
     */
    fun update(id: Long, metadata: CreateDocumentMetadataDTO, content: CreateDocumentDTO)

    fun updateName(id: Long, name: String)

    /**
     * Function to delete a document.
     *
     * @param id[Long] the id of the document
     * @throws Exception DocumentNotFoundException if the document is not found
     */
    fun delete(id: Long)

    /**
     * Function to get the content of a document.
     *
     * @param id[Long] the id of the document
     * @return [DocumentContentDTO] the content of the document
     * @throws Exception DocumentNotFoundException if the document is not found
     */
    fun getContent(id: Long): DocumentContentDTO
}



/**
 * [Service] implementation for the Document entities.
 *
 * @property metaDataRepository[DocumentMetaDataRepository] the repository for the [DocumentMetaData] entity
 * @property contentRepository[DocumentContentRepository] the repository for the [DocumentContent] entity
 */
@Service
class DocumentServiceImpl(
    private val metaDataRepository: DocumentMetaDataRepository,
    private val contentRepository: DocumentContentRepository,
    ) : DocumentService {
    // Logger for this class
    val logger = LoggerFactory.getLogger(this::class.java)


    override fun listAll(page: Int, size: Int): Page<DocumentMetadataDTO> {
        val pageable = PageRequest.of(page, size, Sort.by("id").ascending())
        return metaDataRepository.findAll(pageable).map { it.toDTO() }
    }

    @Transactional
    override fun add(metadata: CreateDocumentMetadataDTO, content: CreateDocumentDTO, messageId: Long?): Long {
//        if (metaDataRepository.existsByName(metadata.name)) {
//            throw DuplicateDocumentException("File with name ${metadata.name} already exists")
//        }

        try {
            // Create DocumentMetadata entity
            val m = DocumentMetaData()
            m.name = metadata.name
            m.size = metadata.size
            m.contentType = metadata.contentType
            m.timestamp = metadata.creationTimeStamp

            // Save metadata
            val savedMetadata = metaDataRepository.save(m)

            // Create DocumentContent entity
            val c = DocumentContent()
            c.content = content.content
            c.documentMetadata = savedMetadata

            // Save content
            val savedContent = contentRepository.save(c)

            // Update the relationship in DocumentMetadata entity
            savedMetadata.documentContent = savedContent
            val doc = metaDataRepository.save(savedMetadata)

            logger.info("File ${metadata.name} uploaded successfully")

            return doc.id

        } catch (e: Exception) {
            throw DocumentServiceInternalErrorException("Failed to upload file ${metadata.name}")
        }
    }

    override fun getMetadata(id: Long): DocumentMetadataDTO {
        val fileMetadata = metaDataRepository.findById(id).orElseThrow { throw DocumentNotFoundException("File with id $id not found") }
        return DocumentMetadataDTO(fileMetadata.id, fileMetadata.name, fileMetadata.size, fileMetadata.contentType, fileMetadata.timestamp)
    }

    @Transactional
    override fun update(id: Long, metadata: CreateDocumentMetadataDTO, content: CreateDocumentDTO) {
        val fileMetadata = metaDataRepository.findById(id).orElseThrow { throw DocumentNotFoundException("File with id $id not found") }
        val fileContent = contentRepository.findById(id).orElseThrow { throw DocumentNotFoundException("File with id $id not found") }

        // Check if the new file name already exists in the database for another file
//        val existingFile = metaDataRepository.findByName(metadata.name)
//        if (existingFile != null && existingFile.id != id) {
//            throw DuplicateDocumentException("File with name ${metadata.name} already exists")
//        }

        // Update metadata
        fileMetadata.name = metadata.name
        fileMetadata.size = metadata.size
        fileMetadata.contentType = metadata.contentType
        fileMetadata.timestamp = metadata.creationTimeStamp

        // Update content
        fileContent.content = content.content

        try {
            metaDataRepository.save(fileMetadata)
            contentRepository.save(fileContent)
            logger.info("Document with id $id updated")
        } catch (e: Exception) {
            throw DocumentServiceInternalErrorException("Failed to update file with id $id")
        }
    }

    override fun updateName(id: Long, name: String) {
        val fileMetadata = metaDataRepository.findById(id).orElseThrow { throw DocumentNotFoundException("File with id $id not found") }

        fileMetadata.name = name.trim { it == '"' }

        try {
            metaDataRepository.save(fileMetadata)
            logger.info("Document with id $id updated")
        } catch (e: Exception) {
            throw DocumentServiceInternalErrorException("Failed to update file with id $id")
        }
    }

    @Transactional
    override fun delete(id: Long) {
        // test if the document exists
        metaDataRepository.findById(id).orElseThrow { throw DocumentNotFoundException("File with id $id not found") }
        metaDataRepository.deleteById(id)       // Delete metadata
        contentRepository.deleteById(id)        // Delete content
        logger.info("Document with id $id deleted")
    }

    override fun getContent(id: Long): DocumentContentDTO {
        val fileContent = contentRepository.findById(id).orElseThrow { throw DocumentNotFoundException("File with id $id not found") }
        return DocumentContentDTO(fileContent.id, fileContent.content)
    }
}