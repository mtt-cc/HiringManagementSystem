package it.polito.waii_24.g20.document_store.dtos

/**
 * Data Transfer Object representing the content of a document during the entity creation.
 *
 * @param content[ByteArray] the content of the document
 */
class CreateDocumentDTO(val content: ByteArray)