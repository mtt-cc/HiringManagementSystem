package it.polito.waii_24.g20.crm_old.unit.messages.testUtils

import it.polito.waii_24.g20.crm_old.entities.Attachment
import it.polito.waii_24.g20.crm_old.entities.Email
import it.polito.waii_24.g20.crm_old.entities.MessageHistory
import it.polito.waii_24.g20.crm_old.entities.Message
import it.polito.waii_24.g20.crm_old.unit.common.randomString
import it.polito.waii_24.g20.crm_old.util.Channel
import it.polito.waii_24.g20.crm_old.util.MessageStateValue
import java.util.*
import java.util.concurrent.TimeUnit

fun generateHistory(seed: Long): MessageHistory {
    return MessageHistory().apply {
        this.id = seed
        this.dateOfStateChange = generateRandomDate(seed).toString()
        this.fromState = MessageStateValue.READ.toString()
        this.toState = MessageStateValue.READ.toString()
        this.message = generateMessage(1)
    }
}

fun generateMessage(id: Long): Message {
    return Message().apply {
        this.id = id
        this.sender = generateEmail(1).toString()
        this.date = generateRandomDate(id).toString()
        this.subject = randomString(10)
        this.attachments = listOf(Attachment().apply { this.id = 1; this.attachmentId = 1 })
        this.body = randomString(20)
        this.channel = Channel.Email
        this.actualState = "received"
        this.priority = 1
    }
}

fun generateRandomDate(seed: Long): Date {
    val random = Random(seed)
    val now = System.currentTimeMillis()
    val randomOffset = random.nextLong(TimeUnit.DAYS.toMillis(365))
    return Date(now - randomOffset)
}

fun generateEmail(seed: Long): Email {
    return Email().apply {
        this.id = seed
        this.email = "email.$seed@mail.com"
    }
}


