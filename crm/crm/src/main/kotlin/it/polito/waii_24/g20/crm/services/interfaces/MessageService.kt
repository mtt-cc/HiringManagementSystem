package it.polito.waii_24.g20.crm.services.interfaces

import it.polito.waii_24.g20.crm.common.MessageStateValue
import it.polito.waii_24.g20.crm.dtos.message.CreateMessageDTO
import it.polito.waii_24.g20.crm.dtos.message.MessageDTO
import it.polito.waii_24.g20.crm.dtos.message.MessageHistoryDTO
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
interface MessageService {

    /**
     * This method lists all the messages in the database (with a paged approach).
     * Usage example: GET http://example.com/messages?page=1&size=20&sorting=Ascending&state=Received
     *
     * @param page[Int] The page number to be retrieved.
     * @param size[Int] The number of elements to be retrieved.
     * @param sorting[String] The sorting order by ID (Ascending or Descending).
     * @param state[String] The state of the messages to be retrieved (Received, Read, Discarded, Processing, Done, Failed).
     *
     * @return The page of [MessageDTO] objects.
     *
     * @throws IllegalArgumentException if the state is not valid.
     */
    fun getMessages(page: Int, size: Int, sorting: String, state: String?): Page<MessageDTO>

    /**
     * This method gets a message by its id.
     * Usage example: GET http://example.com/messages/1
     *
     * @param messageId[Long] The id of the message to be retrieved.
     *
     * @return The [MessageDTO] object.
     *
     * @throws MessageNotFoundException if the message does not exist.
     */
    fun getMessageById(messageId: Long): MessageDTO

    /**
     * This method adds a message to the database.
     * Usage example: POST http://example.com/messages
     * Body: form-data with key "sender" and value the sender of the message.
     * Body: form-data with key "subject" and value the subject of the message.
     * Body: form-data with key "body" and value the body of the message.
     * Body: form-data with key "channel" and value the channel of the message.
     *
     * @param message[CreateMessageDTO] The request containing the message to be added.
     *
     * @return A [String] indicating the success of the operation.
     *
     * @throws IllegalArgumentException if the channel is not valid.
     */
    fun addMessage(message: CreateMessageDTO): Long

    /**
     * This method updates the state of a message.
     * Body: form-data with key "state" and value the new state of the message.
     *
     * @param targetState[MessageStateValue] The new state of the message.
     * @param comment[String] The comment to be added to the history.
     * @param messageId[Long] The id of the message to be updated.
     *
     * @return [Unit]
     *
     * @throws InvalidMessageStateException if the state is not valid.
     * @throws MessageNotFoundException if the message does not exist.
     * @throws InternalException if the operation fails.
     */
    fun updateMessageState(targetState: MessageStateValue, comment: String?, messageId: Long)

    /**
     * This method lists all the history of a message.
     * Usage example: GET http://example.com/messages/1/history
     *
     * @param messageId[Long] The id of the message to be retrieved.
     *
     * @return The list of [HistoryDTO] objects.
     *
     * @throws MessageNotFoundException if the message does not exist.
     */
    fun getHistoryByMessageId(messageId: Long): List<MessageHistoryDTO>

    /**
     * This method updates the priority of a message.
     * Body: form-data with key "priority" and value the new priority of the message.
     *
     * @param id[Long] The id of the message to be updated.
     * @param newPriority[Byte] The new priority of the message.
     *
     * @return [Unit]
     *
     * @throws MessageNotFoundException if the message does not exist.
     */
    fun updateMessagePriority(id: Long, newPriority: Byte)

    fun sendKafkaMessage(msg: String)
}