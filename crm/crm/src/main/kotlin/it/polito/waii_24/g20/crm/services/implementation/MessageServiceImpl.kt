package it.polito.waii_24.g20.crm.services.implementation

import it.polito.waii_24.g20.crm.common.Channel
import it.polito.waii_24.g20.crm.common.MessageStateValue
import it.polito.waii_24.g20.crm.dtos.message.CreateMessageDTO
import it.polito.waii_24.g20.crm.dtos.message.MessageDTO
import it.polito.waii_24.g20.crm.dtos.message.MessageHistoryDTO
import it.polito.waii_24.g20.crm.dtos.message.toHistoryDTO
import it.polito.waii_24.g20.crm.dtos.message.toMessageDTO
import it.polito.waii_24.g20.crm.entities.message.Attachment
import it.polito.waii_24.g20.crm.entities.message.Message
import it.polito.waii_24.g20.crm.entities.message.MessageHistory
import it.polito.waii_24.g20.crm.repositories.EmailRepository
import it.polito.waii_24.g20.crm.repositories.MessageHistoryRepository
import it.polito.waii_24.g20.crm.repositories.MessageRepository
import it.polito.waii_24.g20.crm.repositories.PhoneNumberRepository
import it.polito.waii_24.g20.crm.services.interfaces.MessageService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import org.springframework.kafka.core.KafkaTemplate

/**
 * [Service] implementation for the Message entities.
 *
 * @property messageRepository[MessageRepository] the repository for the [Message] entity
 * @property messageHistoryRepository[MessageHistoryRepository] the repository for the [MessageHistory] entity
 */
@Service
class MessageServiceImpl(
    private val messageRepository: MessageRepository,
    private val messageHistoryRepository: MessageHistoryRepository,
    private val telephoneRepository: PhoneNumberRepository,
    private val emailRepository: EmailRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>
): MessageService {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun getMessages(page: Int, size: Int, sorting: String, state: String?): Page<MessageDTO> {
        val sort: Sort = if (sorting == "ascending") {
            Sort.by("date").ascending()
        } else {
            Sort.by("date").descending()
        }

        val pageable = PageRequest.of(page, size, sort)
        return if (state != null && state != "not-discarded") {
            messageRepository.findByActualState(state, pageable).map { it?.toMessageDTO() ?: throw IllegalArgumentException("Invalid state")}
        } else if (state == "not-discarded") {
            messageRepository.findByActualStateIn(listOf("received", "read"), pageable).map { it?.toMessageDTO() ?: throw IllegalArgumentException("Invalid state")}
        } else {
            messageRepository.findAll(pageable).map { it.toMessageDTO() }
        }
    }

    override fun getMessageById(messageId: Long): MessageDTO {
        if (!messageRepository.existsById(messageId)) {
//            throw MessageNotFoundException("Message with id $messageId does not exist")
            throw IllegalArgumentException("Message with id $messageId does not exist")
        }
        return messageRepository.findById(messageId).map { it.toMessageDTO() }.get()
    }

    @Transactional
    override fun addMessage(message: CreateMessageDTO): Long {
        try {
            // Create Message entity
            val m = Message()
            m.sender = message.sender
            m.date = message.date
            m.subject = message.subject
            m.body = message.body
            m.channel = Channel.toEnum(message.channel)
            m.actualState = message.actualState
            m.attachments = message.attachments.map { Attachment().apply { this.attachmentId = it } }

            // Save data
            val savedMessage = messageRepository.save(m)

            logger.info("Message form ${m.sender}, subject: ${m.subject} - uploaded successfully")

            // Return id of the saved message
            return savedMessage.id!!
        } catch (e: Exception) {
            throw e
        }
    }

    @Transactional
    override fun updateMessageState(targetState: MessageStateValue, comment: String?, messageId: Long) {
        if (targetState !in MessageStateValue.entries.toTypedArray()) {
            throw IllegalArgumentException("Invalid State value") //InvalidMessageStateException("Invalid state value")
        }

        val message = messageRepository.findById(messageId).orElseThrow { throw IllegalArgumentException("Message with id $messageId not found") } //MessageNotFoundException("Message with id $messageId not found") }
        val actualState = MessageStateValue.createMessageState(message.actualState)

        // Verify legality of the state transition
        val nextState = actualState.next(targetState)

        // Update message
        message.actualState = nextState.getValue().toString()

        try {
            messageRepository.save(message)
            logger.info("Message with id $messageId updated")
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to update message with id $messageId") //InternalException("Failed to update message with id $messageId")
        }

        // Insert new history entry
        val newMessageHistory = MessageHistory()
        newMessageHistory.message = message
        newMessageHistory.dateOfStateChange = LocalDateTime.now().toString()
        newMessageHistory.fromState = actualState.getValue().toString()
        newMessageHistory.toState = nextState.getValue().toString()
        newMessageHistory.comments = comment?: ""

        try {
            // Save history
            messageHistoryRepository.save(newMessageHistory)
            logger.info("Inserted new history")
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to insert new History with id $messageId") // InternalException("Failed to insert new History with id $messageId")
        }

    }

    override fun getHistoryByMessageId(messageId: Long): List<MessageHistoryDTO> {
        if (!messageRepository.existsById(messageId)) {
            throw IllegalArgumentException("Message with id $messageId does not exist") //MessageNotFoundException("Message with id $messageId does not exist")
        }
        try {
            val h = messageHistoryRepository.findByMessageId(messageId).map { it.toHistoryDTO() }
            return h
        } catch (e: Exception) {
            throw e
        }

    }

    override fun updateMessagePriority(id: Long, newPriority: Byte) {
        val message = messageRepository.findById(id)
            .orElseThrow{
                throw IllegalArgumentException("Message with id $id not found") //MessageNotFoundException("Message with id $id not found")
            }

        message.priority = newPriority
        messageRepository.save(message)
    }

    override fun sendKafkaMessage(message: String) {
        kafkaTemplate.send("test-topic", message)
    }
}