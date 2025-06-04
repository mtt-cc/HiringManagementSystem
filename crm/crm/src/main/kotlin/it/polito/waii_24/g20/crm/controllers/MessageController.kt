package it.polito.waii_24.g20.crm.controllers

import it.polito.waii_24.g20.crm.common.MessageStateValue
import it.polito.waii_24.g20.crm.dtos.message.CreateMessageRequest
import it.polito.waii_24.g20.crm.dtos.message.MessageDTO
import it.polito.waii_24.g20.crm.dtos.message.CreateMessageDTO
import it.polito.waii_24.g20.crm.dtos.message.MessageHistoryDTO
import it.polito.waii_24.g20.crm.dtos.message.TargetStateRequest
import it.polito.waii_24.g20.crm.dtos.message.UpdatePriorityRequest
import it.polito.waii_24.g20.crm.repositories.MessageRepository
import it.polito.waii_24.g20.crm.services.interfaces.MessageService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

/**
 * This class is the controller for the crm related APIs.
 */
@RestController
@RequestMapping("/messages")
class MessageController(
    private val messageService: MessageService,
    private val messageRepository: MessageRepository
)  {
    // Logger for the MessageController class
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * This method lists all the messages in the database (with a paged approach).
     * Usage example: GET http://example.com/messages?page=1&size=20&sorting=Ascending&state=Received
     *
     * @param page The page number to be retrieved.
     * @param size The number of elements to be retrieved.
     * @param sorting The sorting order by ID (ascending or descending).
     * @param state The state of the messages to be retrieved (received, read, discarded, processing, done, failed).
     * @return The page of [MessageDTO] objects.
     */
    @GetMapping("/", "")
    fun getMessages(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, defaultValue = "ascending") sorting: String,
        @RequestParam(required = false) state: String?
    ): Page<MessageDTO> {
        try {
            // KAFKA
            messageService.sendKafkaMessage( "GET MESSAGES" )

            if (page < 0 || size < 0 || sorting != "ascending" && sorting != "descending" || state != null && state != "received" && state != "read" && state != "discarded" && state != "processing" && state != "done" && state != "failed" && state != "not-discarded")
                throw IllegalArgumentException("Arguments must be valid (page >= 0, size >= 0, sorting = ascending or descending, state = received, read, discarded, processing, done, failed")

            return messageService.getMessages(page, size, sorting, state)
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    /**
     * This method adds a message to the database.
     * Usage example: POST http://example.com/messages
     * Body: form-data with key "sender" and value the sender of the message.
     * Body: form-data with key "subject" and value the subject of the message.
     * Body: form-data with key "body" and value the body of the message.
     * Body: form-data with key "channel" and value the channel of the message.
     *
     * @param request The request containing the message to be added.
     * @return A string indicating the success of the operation.
     */
    @PostMapping("/", "")
    fun addMessage(
        @RequestBody request: CreateMessageRequest
    ) : Long {

        val messageDTO = CreateMessageDTO(
            request.sender,
            request.date ?: LocalDateTime.now().toString(),
            request.subject ?: "",
            request.attachments ?: arrayListOf(),
            request.body ?: "",
            request.channel ?: "",
            "received"
        )

        try {
            // KAFKA
            messageService.sendKafkaMessage( "POST MESSAGE" )

            return messageService.addMessage(messageDTO)
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    /**
     * This method retrieves a message by its ID.
     * Usage example: GET http://example.com/messages/1
     *
     * @param messageId The ID of the message to be retrieved.
     * @return The [MessageDTO] object.
     */
    @GetMapping("/{messageId}")
    fun getMessageById(
        @PathVariable("messageId") messageId: Long,
    ): MessageDTO {
        try {
            // KAFKA
            messageService.sendKafkaMessage( "GET MESSAGE: $messageId" )

            return messageService.getMessageById(messageId)
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    /**
     * This method updates the state of a message.
     * Usage example: POST http://example.com/messages/1
     * Body: form-data with key "state" and value the new state of the message.
     *
     * @param request The request containing the new state of the message.
     */
    @PostMapping("/{messageId}")
    fun updateMessageState(
        @PathVariable("messageId") messageId: Long,
        @RequestBody request: TargetStateRequest
    ): String {
        logger.info("Updating message state to ${request.targetState}")

        try {
            // KAFKA
            messageService.sendKafkaMessage( "POST MESSAGE STATE: $messageId" )

            messageService.updateMessageState(MessageStateValue.stringToEnum(request.targetState), request.comment, messageId)
            return "State updated successfully"
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    /**
     * This method retrieves the history of a message.
     * Usage example: GET http://example.com/messages/1/history
     *
     * @param messageId The ID of the message to be retrieved.
     * @return The list of [HistoryDTO] objects.
     */
    @GetMapping("/{messageId}/history")
    fun getHistoryByMessageId(
        @PathVariable("messageId") messageId: Long
    ): List<MessageHistoryDTO> {
        logger.info("Getting history for message id: $messageId")

        try {
            // KAFKA
            messageService.sendKafkaMessage( "GET MESSAGE HISTORY: $messageId" )

            return messageService.getHistoryByMessageId(messageId)
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    /**
     * This method updates the priority of a message.
     * Usage example: PUT http://example.com/messages/1/priority
     * Body: form-data with key "priority" and value the new priority of the message.
     *
     * @param messageId The ID of the message to be updated.
     * @param request The request containing the new priority of the message.
     */
    @PutMapping("/{messageId}/priority")
    fun updateMessagePriority(
        @PathVariable("messageId") messageId: Long,
        @RequestBody request: UpdatePriorityRequest
    ): String {
        logger.info("Updating message with id ${messageId} to new priority: ${request.newPriority}")

        try {
            // KAFKA
            messageService.sendKafkaMessage( "GET MESSAGE PRIORITY: $messageId" )

            if (request.newPriority<1 || request.newPriority>3) throw IllegalArgumentException("Priority should be between 1 and 3, provided was ${request.newPriority}") //InvalidMessageArgumentException("Priority should be between 1 and 3, provided was ${request.newPriority}")
            if (!messageRepository.existsById(messageId)) throw IllegalArgumentException("Message $messageId does not exist")//MessageNotFoundException("Message $messageId does not exist")
            messageService.updateMessagePriority(messageId, request.newPriority)
            return "Priority updated successfully"
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }
}