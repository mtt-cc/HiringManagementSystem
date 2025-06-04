package it.polito.waii_24.g20.crm.entities.message

import it.polito.waii_24.g20.crm.common.Channel
import it.polito.waii_24.g20.crm.entities.BaseEntity
import it.polito.waii_24.g20.crm.entities.contact.Contact
import jakarta.persistence.*

@Entity
class Message : BaseEntity<Long>() {

    lateinit var sender: String
    lateinit var date: String

    @Column(nullable = true)
    lateinit var subject: String

    @OneToMany(cascade = [CascadeType.ALL])
    lateinit var attachments: List<Attachment>

    @Column(nullable = true, length = 10000)
    lateinit var body: String

    lateinit var channel: Channel
    lateinit var actualState: String

    var priority: Byte = 1

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", referencedColumnName = "id")
    lateinit var contact: Contact
}