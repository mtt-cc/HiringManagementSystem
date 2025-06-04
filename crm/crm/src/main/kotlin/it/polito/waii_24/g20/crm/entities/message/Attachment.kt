package it.polito.waii_24.g20.crm.entities.message

import it.polito.waii_24.g20.crm.entities.BaseEntity
import jakarta.persistence.Entity

@Entity
class Attachment : BaseEntity<Long>() {
    var attachmentId : Long = 0

    // Can be useful track which contact has sent the attachment?
}