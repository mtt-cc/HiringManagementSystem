package it.polito.waii_24.g20.document_store.repositories

import it.polito.waii_24.g20.document_store.entities.DocumentContent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * [Repository] for the [DocumentContent] entity.
 */
@Repository
interface DocumentContentRepository: JpaRepository<DocumentContent,Long>