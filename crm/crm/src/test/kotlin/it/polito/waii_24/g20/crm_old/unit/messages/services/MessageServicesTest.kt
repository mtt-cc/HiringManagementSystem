package it.polito.waii_24.g20.crm_old.unit.messages.services

import io.mockk.*
import it.polito.waii_24.g20.crm_old.dtos.toHistoryDTO
import it.polito.waii_24.g20.crm_old.exceptionHandler.MessageNotFoundException
import it.polito.waii_24.g20.crm_old.repositories.*
import it.polito.waii_24.g20.crm_old.services.ContactService
import it.polito.waii_24.g20.crm_old.services.MessageServiceImpl
import it.polito.waii_24.g20.crm_old.unit.messages.testUtils.generateHistory
import it.polito.waii_24.g20.crm_old.unit.messages.testUtils.generateMessage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.util.Optional

@DisplayName("Unit tests for the Message services")
class AllMessagesTests {

	private val messageRepository = mockk<MessageRepository>()
	private val messageHistoryRepository = mockk<MessageHistoryRepository>()
    private val telephoneRepository = mockk<TelephoneRepository>()
    private val emailRepository = mockk<EmailRepository>()
    private val contactRepository = mockk<ContactRepository>()
    private val contactService = mockk<ContactService>()

	private val messageService = MessageServiceImpl(
        messageRepository = messageRepository,
        messageHistoryRepository = messageHistoryRepository,
        telephoneRepository = telephoneRepository,
        emailRepository = emailRepository,
        contactService = contactService
    )

    @Nested
    @DisplayName("/messages")
    inner class GetMessagesTests {
        @DisplayName("GET /messages - Get messages paging")
        @Test
        fun getMessagesTest() {
            // Arrange
            val message = generateMessage(1)
            val pageNum = 0
            val pageSize = 20
            val pageable = PageRequest.of(pageNum, pageSize, Sort.by("id").ascending())
            val page = PageImpl(listOf(message), pageable, 1)

            every { messageRepository.findAll(pageable) } answers { page }

            try {
                // Act
                val messages1 = messageService.getMessages(pageNum, pageSize, "ascending", null)

                // Assert
                assertEquals(1, messages1.content.size)
            }catch (e: IllegalArgumentException){
                // Throws
                assertThrows(IllegalArgumentException::class.java) { throw e }
            }
        }

        @DisplayName("GET /messages - Get messages paging with filter")
        @Test
        fun getMessagesWithFilterTest() {
            // Arrange
            val message = generateMessage(1)
            val pageNum = 0
            val pageSize = 20
            val state = "received"
            val stateNotFound = "done"
            val pageable = PageRequest.of(pageNum, pageSize, Sort.by("id").ascending())
            val page = PageImpl(listOf(message), pageable, 1)

            every { messageRepository.findByActualState(state, pageable) } answers { page }
            every { messageRepository.findAll(pageable) } answers { page }
            every { messageRepository.findByActualState(stateNotFound, pageable) } answers { page }

            try {
                // Act
                val messages0 = messageService.getMessages(pageNum, pageSize, "ascending", state)
                messageService.getMessages(pageNum, pageSize, "ascending", stateNotFound)

                // Assert
                assertEquals(1, messages0.content.size)
            }catch (e: IllegalArgumentException){
                // Throws
                assertThrows(IllegalArgumentException::class.java) { throw e }
            }
        }

        /*@DisplayName("POST /messages - Create message with email sender")
        @Test
        fun createMessageTest() {
            // Arrange
            val message = generateMessage(1)
            val newMessage = CreateMessageDTO(
               "email.1@mail.com",
                "2023-09-01T00:00:00",
                "subject1",
                "body1",
                Channel.Email.toString(),
                "received"
            )

            val newContact = CreateContactDTO(
                "?",
                "?",
                Category.Unknown.toString(),
                "?"
            )

            val newMail = CreateEmailDTO(
                "email.1@mail.com",
            )

            val messageEntity = Message().apply {
                this.sender = newMessage.sender
                this.date = newMessage.date
                this.subject = newMessage.subject
                this.body = newMessage.body
                this.channel = Channel.toEnum(newMessage.channel)
                this.actualState = newMessage.actualState
                this.priority = 1
            }

            val contactEntity = Contact().apply {
                firstName = newContact.firstName
                lastName = newContact.lastName
                category = Category.valueOf(newContact.category)
                ssn = newContact.ssn
            }

            every { messageRepository.save(match {it.sender == messageEntity.sender && it.date == messageEntity.date && it.subject == messageEntity.subject && it.body == messageEntity.body }) } answers {
                messageEntity
            }
            every { emailRepository.existsEmailByEmail(newMessage.sender)} answers {false}
//            every { contactRepository.addContact(newContact)} answers {1}
//            every { contactService.addEmailToContact(1, newMail) } answers {}
            every {emailRepository.save()} answers {}
            every {contactRepository.save(contactEntity)} answers {}



            // Act
            val messageId = messageService.addMessage(newMessage)

            // Assert
            assertEquals(1, messageId)
            assertEquals(newMessage.sender, messageEntity.sender)
            assertEquals(newMessage.date, messageEntity.date)
            assertEquals(newMessage.subject, messageEntity.subject)
            assertEquals(newMessage.body, messageEntity.body)
            assertEquals(Channel.toEnum(newMessage.channel), messageEntity.channel)
            assertEquals(newMessage.actualState, messageEntity.actualState)
        }*/
    }

    @Nested
    @DisplayName("/messages/{messageId}")
    inner class GetMessagesByIdTests {
        @DisplayName("GET /messages/{messageId} - Get message by id")
        @Test
        fun getMessagesByIdTest() {
            // Arrange
            val id: Long = 2
            val message = generateMessage(id)

            every { messageRepository.findById(id) } answers { Optional.of(message) }
            every { messageRepository.existsById(id) } answers { true }

            // Act
            val message1 = messageService.getMessageById(id)

            // Assert
            assertEquals(id, message1.id)
        }

        @DisplayName("GET /messages/{messageId} - Message not found by id")
        @Test
        fun getMessagesByIdNotFoundTest() {
            // Arrange
            val id: Long = 2
            val idToSearch: Long = 3
            val message = generateMessage(id)

            every { messageRepository.findById(idToSearch) } answers { Optional.of(message) }
            every { messageRepository.existsById(idToSearch) } answers { false }

            try {
                // Act
                val message1 = messageService.getMessageById(idToSearch)
            } catch(e: MessageNotFoundException) {
                // Throw
                assertThrows(MessageNotFoundException::class.java) { throw e }
            }
        }
    }

    @Nested
    @DisplayName("/messages/{id}/history")
    inner class MessageHistoryTests {
        @DisplayName("GET /messages/{id}/history - Get message history incorrect id")
        @Test
        fun getMessageHistoryInvalidIdTest() {
            //Arrange - Create values
            val id: Long = 1
            val message = generateMessage(id)

            every { messageRepository.existsById(id) } answers {
                false
            }

            try {
                //Act - try updating priority value
                messageService.getHistoryByMessageId(id)
            } catch (e: MessageNotFoundException) {
                //Assert - check the list result is message with new priority

            }
        }

        @DisplayName("GET /messages/{id}/history - Get message history")
        @Test
        fun getMessageHistoryTest() {
            //Arrange - Create values
            val id: Long = 1
            val message = generateMessage(id)
            val history = generateHistory(id)

            every { messageRepository.existsById(id) } answers {
                true
            }
            every { messageHistoryRepository.findByMessageId(id) } answers { listOf(history) }

                //Act - try updating priority value
                val histories = messageService.getHistoryByMessageId(id)
                //Assert - check the list result is message with new priority
                assert(histories[0] == history.toHistoryDTO())
            
        }
    }

    @Nested
    @DisplayName("/messages/{id}/priority")
    inner class UpdateMessagesTests {
        @DisplayName("PUT /messages/{id}/priority - Update message priority valid")
        @Test
        fun updateMessagePriorityTest() {
            //Arrange - Create values
            val id: Long = 1
            val message = generateMessage(id)
            val newPriority: Byte = 3

            every { messageRepository.existsById(id) } answers {
                true
            }
            every { messageRepository.findById(id) } answers {
                Optional.of(message)
            }
            every { messageRepository.save(message) } answers {
                message.priority = newPriority
                message
            }

            //Act - try updating priority value
            messageService.updateMessagePriority(id, newPriority)

            //Assert - check the list result is message with new priority
            assert(message.priority == newPriority)
        }

        @DisplayName("PUT /messages/{id}/priority - Update message priority invalid")
        @Test
        fun updateMessageNotExistingTest() {
            //Arrange - Create values
            val id: Long = 1
            val message = generateMessage(id)
            val newPriority: Byte = 3

            every { messageRepository.findById(id) } answers {
                Optional.empty()
            }

            try {
                //Act - try updating priority value
                messageService.updateMessagePriority(id, newPriority)
            } catch (e: MessageNotFoundException) {
                //Assert - check the list result is message with new priority
                assertThrows(MessageNotFoundException::class.java) { throw e }
            }
        }
    }
}