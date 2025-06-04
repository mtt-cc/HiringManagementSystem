package it.polito.waii_24.g20.document_store.dtos

/**
 * Data Transfer Object representing the metadata of a document during the entity creation.
 *
 * @param name[String] the name of the document
 * @param size[Int] the size (in bytes) of the document content
 * @param contentType[String] the content type of the document
 * @param creationTimeStamp[String] the creation timestamp of the document
 */
class CreateDocumentMetadataDTO(val name: String,
                                val size: Int,
                                val contentType : String,
                                val creationTimeStamp: String)