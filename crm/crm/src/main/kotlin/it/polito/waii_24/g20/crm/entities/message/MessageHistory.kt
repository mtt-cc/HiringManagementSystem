package it.polito.waii_24.g20.crm.entities.message

import it.polito.waii_24.g20.crm.entities.BaseEntity
import jakarta.persistence.*

@Suppress("JpaDataSourceORMInspection")
@Entity
class MessageHistory : BaseEntity<Long>() {

    @ManyToOne
    @JoinColumn(name="message_id", nullable=false)
    lateinit var message: Message                     // MessageId

    lateinit var dateOfStateChange: String
    lateinit var fromState: String
    lateinit var toState: String

    @Column(nullable = true)
    var comments: String? = null
}