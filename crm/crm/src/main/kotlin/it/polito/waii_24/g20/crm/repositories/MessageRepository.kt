package it.polito.waii_24.g20.crm.repositories

import it.polito.waii_24.g20.crm.entities.message.Message
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : JpaRepository<Message, Long> {
    /*    @Modifying
        @Transactional
        @Query("UPDATE Message m SET m.priority = :priority WHERE m.id = :id")
        fun updatePriority(id: Long, priority: Byte)*/

    fun findByActualState(actualState: String?, pageable: Pageable?): Page<Message?>

    fun findByActualStateIn(actualStates: List<String>, pageable: Pageable?): Page<Message?>
}