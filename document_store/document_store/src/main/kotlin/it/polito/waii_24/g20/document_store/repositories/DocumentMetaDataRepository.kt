package it.polito.waii_24.g20.document_store.repositories

import it.polito.waii_24.g20.document_store.entities.DocumentMetaData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * [Repository] for the [DocumentMetaData] entity.
 */
@Repository
interface DocumentMetaDataRepository: JpaRepository<DocumentMetaData,Long> {
    /**
     * Function to check if a document with a given name exists.
     *
     * @param name[String] the name of the document
     * @return[Boolean] true if a document with the given name exists, false otherwise
     */
    fun existsByName(name: String): Boolean

    /**
     * Function to find a document by its name.
     *
     * @param name[String] the name of the document
     * @return[DocumentMetaData] the metadata of the document
     */
    fun findByName(name: String): DocumentMetaData?
}