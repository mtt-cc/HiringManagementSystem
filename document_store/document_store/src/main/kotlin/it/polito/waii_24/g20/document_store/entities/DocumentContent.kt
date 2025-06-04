package it.polito.waii_24.g20.document_store.entities

import jakarta.persistence.*

/**
 * [Entity] representing the content of a document.
 *
 * @property id[Long] the id of the document content
 * @property documentMetadata[DocumentMetaData] the metadata of the document ([OneToOne] relationship with [DocumentMetaData] entity)
 * @property content[ByteArray] the content of the document

 */
@Entity
class DocumentContent {
    @Id
    var id: Long = 0

    @OneToOne @MapsId
    lateinit var documentMetadata: DocumentMetaData
    lateinit var content: ByteArray
}