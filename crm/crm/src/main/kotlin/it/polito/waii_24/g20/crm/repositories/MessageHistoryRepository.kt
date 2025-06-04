package it.polito.waii_24.g20.crm.repositories

import it.polito.waii_24.g20.crm.entities.message.MessageHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageHistoryRepository : JpaRepository<MessageHistory, Long> {
    fun findByMessageId(messageId: Long): List<MessageHistory>
}