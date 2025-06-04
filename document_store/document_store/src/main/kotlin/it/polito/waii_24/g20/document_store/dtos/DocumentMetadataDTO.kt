package it.polito.waii_24.g20.document_store.dtos
import it.polito.waii_24.g20.document_store.entities.DocumentMetaData

/**
 * Data Transfer Object representing the metadata of a document.
 *
 * @param id[Long] the id of the document
 * @param name[String] the name of the document
 * @param size[Int] the size (in bytes) of the document content
 * @param contentType[String] the content type of the document
 * @param creationTimeStamp[String] the creation timestamp of the document
 */

data class DocumentMetadataDTO(

    val id: Long,

    val name: String,

    val size: Int,

    val contentType : String,

    val creationTimeStamp: String,
)

/**
 * Extension function to convert a [DocumentMetaData] entity to a DocumentMetadataDTO.
 *
 * @return[DocumentMetadataDTO] the DTO representing the metadata of the document
 */
fun DocumentMetaData.toDTO(): DocumentMetadataDTO =
    DocumentMetadataDTO(this.id, this.name, this.size, this.contentType, this.timestamp)

