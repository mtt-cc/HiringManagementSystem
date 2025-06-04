package it.polito.waii_24.g20.document_store.entities

import jakarta.persistence.*

/**
 * [Entity] representing the metadata of a document.
 *
 * @property id[Long] the id of the document metadata
 * @property documentContent[DocumentContent] the content of the document ([OneToOne] relationship with [DocumentContent] entity)
 * @property name[String] the name of the document
 * @property size[Int] the size (in bytes) of the document content
 * @property contentType[String] the content type of the document
 * @property timestamp[String] the creation timestamp of the document
 */
@Entity
class DocumentMetaData {
    // the db creates itself a unique value // AUTO for auto incrementing ids
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0

    @OneToOne(mappedBy = "documentMetadata", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    lateinit var documentContent: DocumentContent

    lateinit var name: String
    var size: Int = 0                   // bytea can store up to 1 GB
    lateinit var contentType: String
    lateinit var timestamp: String
}


