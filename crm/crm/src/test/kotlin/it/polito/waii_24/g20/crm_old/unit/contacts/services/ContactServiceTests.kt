package it.polito.waii_24.g20.crm_old.unit.contacts.services

import io.mockk.every
import io.mockk.mockk
import it.polito.waii_24.g20.crm_old.dtos.createDTOs.CreateAddressDTO
import it.polito.waii_24.g20.crm_old.dtos.createDTOs.CreateContactDTO
import it.polito.waii_24.g20.crm_old.dtos.createDTOs.CreateEmailDTO
import it.polito.waii_24.g20.crm_old.dtos.createDTOs.CreateTelephoneDTO
import it.polito.waii_24.g20.crm_old.dtos.toDTO
import it.polito.waii_24.g20.crm_old.entities.*
import it.polito.waii_24.g20.crm_old.exceptionHandler.*
import it.polito.waii_24.g20.crm_old.repositories.AddressRepository
import it.polito.waii_24.g20.crm_old.repositories.ContactRepository
import it.polito.waii_24.g20.crm_old.repositories.EmailRepository
import it.polito.waii_24.g20.crm_old.repositories.TelephoneRepository
import it.polito.waii_24.g20.crm_old.services.ContactServiceImpl
import it.polito.waii_24.g20.crm_old.unit.contacts.testUtils.*
import it.polito.waii_24.g20.crm_old.util.Category
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.util.*

annotation class Ok


@Ok
@DisplayName("Unit tests for the Contact services")
class AllContactTests {

    private val contactRepository = mockk<ContactRepository>()
    private val emailRepository = mockk<EmailRepository>()
    private val telephoneRepository = mockk<TelephoneRepository>()
    private val addressRepository = mockk<AddressRepository>()

    private val contactService = ContactServiceImpl(
        contactRepository = contactRepository,
        emailRepository = emailRepository,
        telephoneRepository = telephoneRepository,
        addressRepository = addressRepository
    )

    @Ok
    @Nested
    @DisplayName("/contacts")
    inner class ApiContacts {
        @DisplayName("GET /contacts - Get contacts paging")
        @Test
        fun getContactsTest() {
            // Arrange
            val contacts = generateContactState1()
            val p0 = 0
            val p1 = 1
            val s = 2
            val pageable0 = PageRequest.of(p0, s, Sort.by("id").ascending())
            val pageable1 = PageRequest.of(p1, s, Sort.by("id").ascending())
            val page0gold = PageImpl(contacts.subList(0, 2))
            val page1gold = PageImpl(contacts.subList(2, 4))

            every { contactRepository.getFilteredContacts(pageable0) } returns page0gold
            every { contactRepository.getFilteredContacts(pageable1) } returns page1gold

            // Act
            val page0 = contactService.getContacts(p0, s)
            val page1 = contactService.getContacts(p1, s)

            // Assert
            assertEquals(s, page0.content.size, "Content size should be the same (page 0)")
            assertEquals(s, page1.content.size, "Content size should be the same (page 1)")
            assertTrue(
                page0.content.toTypedArray().zip(page0gold.map { it.toDTO() }.content.toTypedArray())
                    .all { it.first.equalsTo(it.second) },
                "Content should be the same (page 0)"
            )
            assertTrue(
                page1.content.toTypedArray().zip(page1gold.map { it.toDTO() }.content.toTypedArray())
                    .all { it.first.equalsTo(it.second) },
                "Content should be the same (page 1)"
            )
        }

        @DisplayName("GET /contacts - Get contacts paging with filter")
        @Test
        fun getContactsFilteredTest() {
            // Arrange
            val contacts = generateContactState2("John", "Doe", "0123456789")

            val pageable = PageRequest.of(0, 10, Sort.by("id").ascending())
            val pageGold = PageImpl(listOf(contacts[0], contacts[4]))

            every {
                contactRepository.getFilteredContacts(
                    pageable,
                    "John",
                    "Doe",
                    null,
                    "0123456789"
                )
            } returns pageGold

            // Act
            val page = contactService.getContacts(0, 10, "John", "Doe", null, "0123456789")

            // Assert
            assertEquals(2, page.content.size, "Content size should be the same")
            assertTrue(
                page.content.toTypedArray().zip(pageGold.map { it.toDTO() }.content.toTypedArray())
                    .all { it.first.equalsTo(it.second) },
                "Content should be the same"
            )
        }

        @DisplayName("POST /contacts - Create contact")
        @Test
        fun createContactTest() {
            // Arrange
            val contacts = generateContactState1().toMutableList()
            val newContact = CreateContactDTO(
                "NewFirstName",
                "NewLastName",
                "NewSSN"
            )

            val contactEntity = Contact().apply {
                firstName = newContact.firstName
                lastName = newContact.lastName
                ssn = newContact.ssn
                category = Category.Unknown
            }

            every { contactRepository.save(match { it.firstName == contactEntity.firstName && it.lastName == contactEntity.lastName && it.ssn == contactEntity.ssn }) } answers {
                contactEntity.id = 5
                contacts.add(contactEntity)
                contactEntity
            }

            // Act
            val contactId = contactService.addContact(newContact)

            // Assert
            assertEquals(5, contacts.size, "Size should be 5")
            assertEquals(contactId, contacts[4].id, "Id should be 5")
            assertEquals("NewFirstName", contacts[4].firstName, "First name should be NewFirstName")
            assertEquals("NewLastName", contacts[4].lastName, "Last name should be NewLastName")
            assertEquals(Category.Unknown, contacts[4].category, "Category should be Unknown")
            assertEquals("NewSSN", contacts[4].ssn, "SSN should be NewSSN")
        }

        @DisplayName("POST /contacts - Create contact without ssn")
        @Test
        fun createContactWithoutSSNTest() {
            // Arrange
            val contacts = generateContactState1().toMutableList()
            val newContact = CreateContactDTO(
                "NewFirstName",
                "NewLastName",
                null
            )

            val contactEntity = Contact().apply {
                firstName = newContact.firstName
                lastName = newContact.lastName
                ssn = newContact.ssn
                category = Category.Unknown
            }

            every { contactRepository.save(match { it.firstName == contactEntity.firstName && it.lastName == contactEntity.lastName &&  it.ssn == contactEntity.ssn }) } answers {
                contactEntity.id = 5
                contacts.add(contactEntity)
                contactEntity
            }

            // Act
            val contactId = contactService.addContact(newContact)

            // Assert
            assertEquals(5, contacts.size, "Size should be 5")
            assertEquals(contactId, contacts[4].id, "Id should be 5")
            assertEquals("NewFirstName", contacts[4].firstName, "First name should be NewFirstName")
            assertEquals("NewLastName", contacts[4].lastName, "Last name should be NewLastName")
            assertEquals(Category.Unknown, contacts[4].category, "Category should be Unknown")
            assertNull(contacts[4].ssn, "SSN should be NewSSN")
        }
    }

    @Ok
    @Nested
    @DisplayName("/contacts/{contactId}")
    inner class ApiContactsContactId {
        @DisplayName("GET /contacts/{contactId} - Get contact by id")
        @Test
        fun getContactByIdTest() {
            // Arrange
            val contacts = generateContactState1()
            val target = 3
            val golden = contacts[target]

            every { contactRepository.findByIdAndDeletedIsFalse(target.toLong()) } returns Optional.of(golden)

            // Act
            val contact = contactService.getContactById(target.toLong())

            // Assert
            assertTrue(contact.equalsTo(golden.toDTO()), "Contact should be the same")
        }

        @DisplayName("GET /contacts/{contactId} - Get contact by id not found")
        @Test
        fun getContactByIdNotFoundTest() {
            // Arrange
            val target = 3

            every { contactRepository.findByIdAndDeletedIsFalse(target.toLong()) } returns Optional.empty()

            try {
                // Act
                contactService.getContactById(target.toLong())
            } catch (e: ContactNotFoundException) {
                // Assert
                assertThrows(ContactNotFoundException::class.java) { throw e }
            }
        }

        @DisplayName("DELETE /contacts/{contactId} - Delete contact by id")
        @Test
        fun deleteContactByIdTest() {
            // Arrange
            val contacts = generateContactState1().toMutableList()
            val target = 3
            val golden = contacts[target]

            every { contactRepository.findByIdAndDeletedIsFalse(target.toLong()) } answers { Optional.of(golden) }
            for (e in golden.emails) {
                every { emailRepository.delete(e) } returns Unit
            }
            for (t in golden.phoneNumbers) {
                every { telephoneRepository.delete(t) } returns Unit
            }
            for (a in golden.addresses) {
                every { addressRepository.delete(a) } returns Unit
            }
            every { contactRepository.save(golden) } answers {
                golden.clean()
            }

            // Act
            contactService.deleteContactById(target.toLong())

            // Assert
            val c = contactService.getContactById(target.toLong())
            assertEquals("*****", c.firstName, "First name should be *****")
            assertEquals("*****", c.lastName, "Last name should be *****")
            assertNull(c.ssn, "SSN should be null")
            assertEquals(Category.Unknown.toString(), c.category, "Category should be Unknown")
            c.emails.forEach { assertEquals("*****", it.email, "Email should be *****") }
            c.phoneNumbers.forEach { assertEquals("*****", it.telephone, "Telephone should be *****") }
            c.addresses.forEach {
                assertEquals("*****", it.street, "Street should be *****")
                assertEquals("*****", it.number, "Number should be *****")
                assertEquals("*****", it.city, "City should be *****")
                assertEquals("*****", it.postalCode, "Postal code should be *****")
                assertEquals("*****", it.country, "Country should be *****")
            }
        }

        @DisplayName("DELETE /contacts/{contactId} - Delete contact by id not found")
        @Test
        fun deleteContactByIdNotFoundTest() {
            // Arrange
            val target = 3

            every { contactRepository.findByIdAndDeletedIsFalse(target.toLong()) } returns Optional.empty()

            try {
                // Act
                contactService.deleteContactById(target.toLong())
            } catch (e: ContactNotFoundException) {
                // Assert
                assertThrows(ContactNotFoundException::class.java) { throw e }
            }
        }
    }

    @Ok
    @Nested
    @DisplayName("/contacts/{contactId}/category")
    inner class ApiContactsContactIdCategory {
        @DisplayName("PUT /contacts/{contactId}/category - Update category to contact by id")
        @Test
        fun updateCategoryToContactByIdTest() {
            // Arrange
            val contacts = generateContactState1().toMutableList()
            val target = 3
            val golden = contacts[target]
            val newCategory = Category.Customer
            golden.category = Category.Unknown

            every { contactRepository.findByIdAndDeletedIsFalse(target.toLong()) } answers { Optional.of(golden) }
            every { contactRepository.save(golden) } answers {
                golden.category = newCategory
                golden
            }

            // Act
            contactService.updateCategoryToContact(target.toLong(), newCategory.toString())

            // Assert
            assertEquals(newCategory, contacts[target].category, "Category should be updated")
        }

        @DisplayName("PUT /contacts/{contactId}/category - Update category to contact by id not found")
        @Test
        fun updateCategoryToContactByIdNotFoundTest() {
            // Arrange
            val target = 100
            val newCategory = Category.Customer

            every { contactRepository.findByIdAndDeletedIsFalse(target.toLong()) } returns Optional.empty()

            try {
                // Act
                contactService.updateCategoryToContact(target.toLong(), newCategory.toString())
            } catch (e: ContactNotFoundException) {
                // Assert
                assertThrows(ContactNotFoundException::class.java) { throw e }
            }
        }

        @DisplayName("PUT /contacts/{contactId}/category - Update category with invalid category")
        @Test
        fun updateCategoryWithInvalidCategoryTest() {
            // Arrange
            val contacts = generateContactState1().toMutableList()
            val target = 3
            val golden = contacts[target]
            val newCategory = "InvalidCategory"

            every { contactRepository.findByIdAndDeletedIsFalse(target.toLong()) } answers { Optional.of(golden) }

            try {
                // Act
                contactService.updateCategoryToContact(target.toLong(), newCategory)
            } catch (e: Exception) {
                // Assert
                assertThrows(InvalidCategoryException::class.java) { throw e }
            }
        }

        @DisplayName("DELETE /contacts/{contactId}/category - Delete category from contact by id")
        @Test
        fun deleteCategoryFromContactByIdTest() {
            // Arrange
            val contacts =
                generateContactState1().toMutableList().map { it.category = Category.Customer; it }.toMutableList()
            val target = 3
            val golden = contacts[target]

            every { contactRepository.findByIdAndDeletedIsFalse(target.toLong()) } answers { Optional.of(golden) }
            every { contactRepository.save(golden) } answers {
                golden.category = Category.Unknown
                contacts[contacts.indexOf(golden)] = golden
                golden
            }

            // Act
            contactService.deleteCategoryFromContact(target.toLong())

            // Assert
            assertTrue(true, "No exception should be thrown")
            assertEquals(Category.Unknown, contacts[target].category, "Category should be null")
        }

        @DisplayName("DELETE /contacts/{contactId}/category - Delete category from contact by id not found")
        @Test
        fun deleteCategoryFromContactByIdNotFoundTest() {
            // Arrange
            val target = 3

            every { contactRepository.findByIdAndDeletedIsFalse(target.toLong()) } returns Optional.empty()

            try {
                // Act
                contactService.deleteCategoryFromContact(target.toLong())
            } catch (e: ContactNotFoundException) {
                // Assert
                assertThrows(ContactNotFoundException::class.java) { throw e }
            }
        }
    }

    @Ok
    @Nested
    @DisplayName("/contacts/{contactId}/email")
    inner class ApiContactsContactIdEmail {
        @DisplayName("POST /contacts/{contactId}/email - Create email and assign to contact")
        @Test
        fun createEmailAndAssignToContactTest() {
            // Arrange
            val contacts = generateContactState1().toMutableList()
            val ctid = 3
            val newEmail = CreateEmailDTO("e@email.com")

            val emailEntity = Email().apply {
                email = newEmail.email
            }

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.of(contacts[ctid])
            }

            every { emailRepository.save(match { it.email == emailEntity.email }) } answers {
                emailEntity.id = 4
                emailEntity
            }

            every { contactRepository.save(contacts[ctid]) } answers {
                contacts[ctid].apply { emails.map { if (it.id == null) it.id = 4 } }
            }

            // Act
            contactService.addEmailToContact(ctid.toLong(), newEmail)

            // Assert
            assertEquals(3, contacts[ctid].emails.size, "Size should be 3")
            assertEquals(4, contacts[ctid].emails[2].id, "Id should be 4")
            assertEquals("e@email.com", contacts[ctid].emails[2].email, "Email should be e@email.com")
        }

        @DisplayName("POST /contacts/{contactId}/email - Create email and assign to contact not found")
        @Test
        fun createEmailAndAssignToContactNotFoundTest() {
            // Arrange
            val ctid = 300
            val newEmail = CreateEmailDTO("e@email.com")

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } returns Optional.empty()

            try {
                // Act
                contactService.addEmailToContact(ctid.toLong(), newEmail)
            } catch (e: ContactNotFoundException) {
                // Assert
                assertThrows(ContactNotFoundException::class.java) { throw e }
            }
        }
    }

    @Ok
    @Nested
    @DisplayName("/contacts/{contactId}/email/{emailId}")
    inner class ApiContactsContactIdEmailEmailId {
        @DisplayName("PUT /contacts/{contactId}/email/{emailId} - Update email to contact by id")
        @Test
        fun updateEmailToContactByIdTest() {
            // Arrange
            val contacts = generateContactStateForContactMethodDelete().toMutableList()
            val ctid = 3
            val eid = 1

            val newEmail = CreateEmailDTO("new.email@example.com")

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.of(contacts[ctid])
            }

            every { emailRepository.findByIdAndDeletedIsFalse(eid.toLong()) } answers {
                Optional.of(contacts[ctid].emails[eid])
            }

            every { emailRepository.save(contacts[ctid].emails[eid]) } answers {
                contacts[ctid].emails[eid].email = newEmail.email
                contacts[ctid].emails[eid]
            }

            // Act
            contactService.updateEmailToContact(ctid.toLong(), eid.toLong(), newEmail)

            // Assert
            assertEquals(newEmail.email, contacts[ctid].emails[eid].email, "Email should be updated")
        }

        @DisplayName("PUT /contacts/{contactId}/email/{emailId} - Update email to contact by emailId not found")
        @Test
        fun updateEmailFromContactWithCorrectContactIdAndNotExistingEmailId() {
            // Arrange
            val contacts = generateContactStateForContactMethodDelete().toMutableList()
            val ctid = 1
            val eid = 100

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.of(contacts[ctid])
            }

            every { emailRepository.findByIdAndDeletedIsFalse(eid.toLong()) } answers {
                Optional.empty()
            }

            try {
                // Act
                contactService.updateEmailToContact(
                    ctid.toLong(),
                    eid.toLong(),
                    CreateEmailDTO("new.email@example.com")
                )
            } catch (e: Exception) {
                // Assert
                assertThrows(EmailNotFoundException::class.java) { throw e }
            }
        }

        @DisplayName("PUT /contacts/{contactId}/email/{emailId} - Update email to contact by contactId not found")
        @Test
        fun updateEmailToContactByIdNotFoundTest() {
            // Arrange
            val ctid = 100
            val eid = 1

            val newEmail = CreateEmailDTO("new.email@example.com")

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.empty()
            }

            try {
                // Act
                contactService.updateEmailToContact(ctid.toLong(), eid.toLong(), newEmail)
            } catch (e: ContactNotFoundException) {
                // Assert
                assertThrows(ContactNotFoundException::class.java) { throw e }
            }
        }

        @DisplayName("PUT /contacts/{contactId}/email/{emailId} - Update email to contact by existing contactId, not existing emailId but not linked (the email exists but it is not for that contact")
        @Test
        fun updateEmailToContactWithCorrectContactIdAndExistingEmailIdNotLinkedTest() {
            // Arrange
            val contacts = generateContactStateForContactMethodDelete().toMutableList()
            val ctid = 1
            val eid = 1

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.of(contacts[ctid])
            }

            every { emailRepository.findByIdAndDeletedIsFalse(eid.toLong()) } answers {
                Optional.of(contacts[3].emails[1])
            }

            try {
                // Act
                contactService.updateEmailToContact(
                    ctid.toLong(),
                    eid.toLong(),
                    CreateEmailDTO("new.email@example.com")
                )
            } catch (e: Exception) {
                // Assert
                assertThrows(InvalidContactBindingException::class.java) { throw e }
            }
        }

        @DisplayName("DELETE /contacts/{contactId}/email/{emailId} - Delete email from contact by contactId and emailId")
        @Test
        fun deleteEmailFromContactTest() {
            // Arrange
            val contacts = generateContactStateForContactMethodDelete().toMutableList()
            val ctid = 0
            val eid = 1

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.of(contacts[ctid])
            }
            every { emailRepository.findByIdAndDeletedIsFalse(eid.toLong()) } answers {
                Optional.of(contacts[ctid].emails[eid])
            }
            every { emailRepository.save(contacts[ctid].emails[eid]) } answers {
                contacts[ctid].emails.forEach { if (it.id == eid.toLong()) it.clean() }
                contacts[ctid].emails[eid]
            }

            // Act
            contactService.deleteEmailFromContact(ctid.toLong(), eid.toLong())

            // Assert
            assertEquals(2, contacts[ctid].emails.size, "Email should not be deleted but only cleaned (size fail)")
            assertEquals("*****", contacts[ctid].emails[eid].email, "Email should be cleaned")
        }

        @DisplayName("DELETE /contacts/{contactId}/email/{emailId} - Delete email from contact by existing contactId and not existing emailId")
        @Test
        fun deleteEmailFromContactWithCorrectContactIdAndNotExistingEmailIdTest() {
            // Arrange
            val contacts = generateContactStateForContactMethodDelete().toMutableList()
            val ctid = 0
            val eid = 100

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.of(contacts[ctid])
            }
            every { emailRepository.findByIdAndDeletedIsFalse(eid.toLong()) } answers {
                Optional.empty()
            }

            try {
                // Act
                contactService.deleteEmailFromContact(ctid.toLong(), eid.toLong())
            } catch (e: Exception) {
                // Assert
                assertThrows(EmailNotFoundException::class.java) { throw e }
            }
        }

        @DisplayName("DELETE /contacts/{contactId}/email/{emailId} - Delete email from contact by non existing contactId")
        @Test
        fun deleteEmailFromContactWithNotExistingContactIdTest() {
            // Arrange
            val ctid = 100
            val eid = 0

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.empty()
            }

            try {
                // Act
                contactService.deleteEmailFromContact(ctid.toLong(), eid.toLong())
            } catch (e: Exception) {
                // Assert
                assertThrows(ContactNotFoundException::class.java) { throw e }
            }
        }

        @DisplayName("DELETE /contacts/{contactId}/email/{emailId} - Delete email from contact by existing contactId, not existing emailId but not linked (the email exists but it is not for that contact")
        @Test
        fun deleteEmailFromContactWithCorrectContactIdAndExistingEmailIdNotLinkedTest() {
            // Arrange
            val contacts = generateContactStateForContactMethodDelete().toMutableList()
            val ctid = 0
            val eid = 2

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.of(contacts[ctid])
            }
            every { emailRepository.findByIdAndDeletedIsFalse(eid.toLong()) } answers {
                Optional.of(contacts[1].emails[0])
            }


            try {
                // Act
                contactService.deleteEmailFromContact(ctid.toLong(), eid.toLong())
            } catch (e: Exception) {
                // Assert
                assertThrows(EmailNotFoundException::class.java) { throw e }
            }
        }
    }

    @Ok
    @Nested
    @DisplayName("/contacts/{contactId}/telephone")
    inner class ApiContactsContactIdTelephone {
        @DisplayName("POST /contacts/{contactId}/telephone - Create telephone and assign to contact")
        @Test
        fun createTelephoneAndAssignToContactTest() {
            // Arrange
            val contacts = generateContactState1().toMutableList()
            val ctid = 2
            val newTelephone = CreateTelephoneDTO("+39 1234567890")

            val telephoneEntity = Telephone().apply {
                telephone = newTelephone.telephone
            }

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.of(contacts[ctid])
            }

            every { telephoneRepository.save(match { it.telephone == telephoneEntity.telephone }) } answers {
                telephoneEntity.id = 3
                telephoneEntity
            }

            every { contactRepository.save(contacts[ctid]) } answers {
                contacts[ctid].apply { phoneNumbers.map { if (it.id == null) it.id = 3 } }
            }

            // Act
            contactService.addTelephoneToContact(ctid.toLong(), newTelephone)

            // Assert
            assertEquals(3, contacts[ctid].phoneNumbers.size, "Size should be 3")
            assertEquals(3, contacts[ctid].phoneNumbers[2].id, "Id should be 3")
            assertEquals("+39 1234567890", contacts[ctid].phoneNumbers[2].telephone, "Telephone should be +1234567890")
        }

        @DisplayName("POST /contacts/{contactId}/telephone - Create telephone and assign to contact not found")
        @Test
        fun createTelephoneAndAssignToContactNotFoundTest() {
            // Arrange
            val ctid = 300
            val newTelephone = CreateTelephoneDTO("+1234567890")

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } returns Optional.empty()

            try {
                // Act
                contactService.addTelephoneToContact(ctid.toLong(), newTelephone)
            } catch (e: ContactNotFoundException) {
                // Assert
                assertThrows(ContactNotFoundException::class.java) { throw e }
            }
        }
    }

    @Ok
    @Nested
    @DisplayName("/contacts/{contactId}/telephone/{telephoneId}")
    inner class ApiContactsContactIdTelephoneTelephoneId {
        @DisplayName("PUT /contacts/{contactId}/telephone/{telephoneId} - Update telephone to contact by id")
        @Test
        fun updateTelephoneToContactByIdTest() {
            // Arrange
            val contacts = generateContactStateForContactMethodDelete().toMutableList()
            val ctid = 2
            val tid = 1

            val newTelephone = CreateTelephoneDTO("+39 1234567890")

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.of(contacts[ctid])
            }

            every { telephoneRepository.findByIdAndDeletedIsFalse(tid.toLong()) } answers {
                Optional.of(contacts[ctid].phoneNumbers[tid])
            }

            every { telephoneRepository.save(contacts[ctid].phoneNumbers[tid]) } answers {
                contacts[ctid].phoneNumbers[tid].telephone = newTelephone.telephone
                contacts[ctid].phoneNumbers[tid]
            }

            // Act
            contactService.updateTelephoneToContact(ctid.toLong(), tid.toLong(), newTelephone)

            // Assert
            assertEquals(
                newTelephone.telephone,
                contacts[ctid].phoneNumbers[tid].telephone,
                "Telephone should be updated"
            )
        }

        @DisplayName("PUT /contacts/{contactId}/telephone/{telephoneId} - Update telephone to contact by telephoneId not found")
        @Test
        fun updateTelephoneFromContactWithCorrectContactIdAndExistingTelephoneIdNotLinkedTest() {
            // Arrange
            val contacts = generateContactStateForContactMethodDelete().toMutableList()
            val ctid = 1
            val tid = 2

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.of(contacts[ctid])
            }

            every { telephoneRepository.findByIdAndDeletedIsFalse(tid.toLong()) } answers {
                Optional.empty()
            }

            try {
                // Act
                contactService.updateTelephoneToContact(
                    ctid.toLong(),
                    tid.toLong(),
                    CreateTelephoneDTO("+393333333333")
                )
            } catch (e: Exception) {
                // Assert
                assertThrows(TelephoneNotFoundException::class.java) { throw e }
            }
        }

        @DisplayName("PUT /contacts/{contactId}/telephone/{telephoneId} - Update telephone to contact by contactId not found")
        @Test
        fun updateTelephoneToContactByIdNotFoundTest() {
            // Arrange
            val ctid = 100
            val tid = 1

            val newTelephone = CreateTelephoneDTO("+393333333333")

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.empty()
            }

            try {
                // Act
                contactService.updateTelephoneToContact(ctid.toLong(), tid.toLong(), newTelephone)
            } catch (e: ContactNotFoundException) {
                // Assert
                assertThrows(ContactNotFoundException::class.java) { throw e }
            }
        }

        @DisplayName("PUT /contacts/{contactId}/telephone/{telephoneId} - Update telephone to contact by existing contactId, not existing telephoneId but not linked (the telephone exists but it is not for that contact")
        @Test
        fun updateTelephoneToContactWithCorrectContactIdAndExistingTelephoneIdNotLinkedTest() {
            // Arrange
            val contacts = generateContactStateForContactMethodDelete().toMutableList()
            val ctid = 1
            val tid = 1

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.of(contacts[ctid])
            }

            every { telephoneRepository.findByIdAndDeletedIsFalse(tid.toLong()) } answers {
                Optional.of(contacts[2].phoneNumbers[1])
            }

            try {
                // Act
                contactService.updateTelephoneToContact(
                    ctid.toLong(),
                    tid.toLong(),
                    CreateTelephoneDTO("+393333333333")
                )
            } catch (e: Exception) {
                // Assert
                assertThrows(InvalidContactBindingException::class.java) { throw e }
            }
        }

        @DisplayName("DELETE /contacts/{contactId}/telephone/{telephoneId} - Delete telephone from contact by contactId and telephoneId")
        @Test
        fun deleteTelephoneFromContactTest() {
            // Arrange
            val contacts = generateContactStateForContactMethodDelete().toMutableList()
            val ctid = 2
            val tid = 1

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.of(contacts[ctid])
            }
            every { telephoneRepository.findByIdAndDeletedIsFalse(tid.toLong()) } answers {
                Optional.of(contacts[ctid].phoneNumbers[tid])
            }
            every { telephoneRepository.save(contacts[ctid].phoneNumbers[tid]) } answers {
                contacts[ctid].phoneNumbers.forEach { if (it.id == tid.toLong()) it.clean() }
                contacts[ctid].phoneNumbers[tid]
            }

            // Act
            contactService.deleteTelephoneFromContact(ctid.toLong(), tid.toLong())

            // Assert
            assertEquals(2, contacts[ctid].phoneNumbers.size, "Telephone should not be deleted but only cleaned (size fail)")
            assertEquals("*****", contacts[ctid].phoneNumbers[tid].telephone, "Telephone should be cleaned")
        }

        @DisplayName("DELETE /contacts/{contactId}/telephone/{telephoneId} - Delete telephone from contact by existing contactId and not existing telephoneId")
        @Test
        fun deleteTelephoneFromContactWithCorrectContactIdAndNotExistingTelephoneIdTest() {
            // Arrange
            val contacts = generateContactStateForContactMethodDelete().toMutableList()
            val ctid = 0
            val tid = 100

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.of(contacts[ctid])
            }
            every { telephoneRepository.findByIdAndDeletedIsFalse(tid.toLong()) } answers {
                Optional.empty()
            }

            try {
                // Act
                contactService.deleteTelephoneFromContact(ctid.toLong(), tid.toLong())
            } catch (e: Exception) {
                // Assert
                assertThrows(TelephoneNotFoundException::class.java) { throw e }
            }
        }

        @DisplayName("DELETE /contacts/{contactId}/telephone/{telephoneId} - Delete telephone from contact by non existing contactId")
        @Test
        fun deleteTelephoneFromContactWithNotExistingContactIdTest() {
            // Arrange
            val ctid = 100
            val tid = 0

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.empty()
            }

            try {
                // Act
                contactService.deleteTelephoneFromContact(ctid.toLong(), tid.toLong())
            } catch (e: Exception) {
                // Assert
                assertThrows(ContactNotFoundException::class.java) { throw e }
            }
        }

        @DisplayName("DELETE /contacts/{contactId}/telephone/{telephoneId} - Delete telephone from contact by existing contactId, not existing telephoneId but not linked (the telephone exists but it is not for that contact")
        @Test
        fun deleteTelephoneFromContactWithCorrectContactIdAndExistingTelephoneIdNotLinkedTest() {
            // Arrange
            val contacts = generateContactStateForContactMethodDelete().toMutableList()
            val ctid = 1
            val tid = 2

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.of(contacts[ctid])
            }
            every { telephoneRepository.findByIdAndDeletedIsFalse(tid.toLong()) } answers {
                Optional.of(contacts[2].phoneNumbers[1])
            }

            try {
                // Act
                contactService.deleteTelephoneFromContact(ctid.toLong(), tid.toLong())
            } catch (e: Exception) {
                // Assert
                assertThrows(TelephoneNotFoundException::class.java) { throw e }
            }
        }
    }

    @Ok
    @Nested
    @DisplayName("/contacts/{contactId}/address")
    inner class ApiContactsContactIdAddress {
        @DisplayName("POST /contacts/{contactId}/address - Create address and assign to contact")
        @Test
        fun createAddressAndAssignToContactTest() {
            // Arrange
            val contacts = generateContactState1().toMutableList()
            val ctid = 3
            val newAddress = CreateAddressDTO("Street", "1", "City", "12345", "Country")

            val addressEntity = Address().apply {
                street = newAddress.street
                number = newAddress.number
                city = newAddress.city
                country = newAddress.country
                postalCode = newAddress.postalCode
            }

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.of(contacts[ctid])
            }

            every { addressRepository.save(match { it.street == addressEntity.street && it.number == addressEntity.number && it.city == addressEntity.city && it.country == addressEntity.country && it.postalCode == addressEntity.postalCode }) } answers {
                addressEntity.id = 3
                addressEntity
            }

            every { contactRepository.save(contacts[ctid]) } answers {
                contacts[ctid].apply { addresses.map { if (it.id == null) it.id = 3 } }
            }

            // Act
            contactService.addAddressToContact(ctid.toLong(), newAddress)

            // Assert
            assertEquals(2, contacts[ctid].addresses.size, "Size should be 2")
            assertEquals(3, contacts[ctid].addresses[1].id, "Id should be 3")
            assertEquals("Street", contacts[ctid].addresses[1].street, "Street should be Street")
            assertEquals("1", contacts[ctid].addresses[1].number, "Number should be 1")
            assertEquals("City", contacts[ctid].addresses[1].city, "City should be City")
            assertEquals("Country", contacts[ctid].addresses[1].country, "Country should be Country")
            assertEquals("12345", contacts[ctid].addresses[1].postalCode, "PostalCode should be 12345")
        }

        @DisplayName("POST /contacts/{contactId}/address - Create address and assign to contact not found")
        @Test
        fun createAddressAndAssignToContactNotFoundTest() {
            // Arrange
            val ctid = 300
            val newAddress = CreateAddressDTO("Street", "1", "City", "12345", "Country")

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } returns Optional.empty()

            try {
                // Act
                contactService.addAddressToContact(ctid.toLong(), newAddress)
            } catch (e: ContactNotFoundException) {
                // Assert
                assertThrows(ContactNotFoundException::class.java) { throw e }
            }
        }
    }

    @Ok
    @Nested
    @DisplayName("/contacts/{contactId}/address/{addressId}")
    inner class ApiContactsContactIdAddressAddressId {
        @DisplayName("PUT /contacts/{contactId}/address/{addressId} - Update address to contact by id")
        @Test
        fun updateAddressToContactByIdTest() {
            // Arrange
            val contacts = generateContactStateForContactMethodDelete().toMutableList()
            val ctid = 2
            val aid = 1

            val newAddress = CreateAddressDTO("New Street", "1", "New City", "New Country", "New Zip")

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.of(contacts[ctid])
            }

            every { addressRepository.findByIdAndDeletedIsFalse(aid.toLong()) } answers {
                Optional.of(contacts[ctid].addresses[aid])
            }

            every { addressRepository.save(contacts[ctid].addresses[aid]) } answers {
                contacts[ctid].addresses[aid].street = newAddress.street
                contacts[ctid].addresses[aid].number = newAddress.number
                contacts[ctid].addresses[aid].city = newAddress.city
                contacts[ctid].addresses[aid].country = newAddress.country
                contacts[ctid].addresses[aid].postalCode = newAddress.postalCode
                contacts[ctid].addresses[aid]
            }

            // Act
            contactService.updateAddressToContact(ctid.toLong(), aid.toLong(), newAddress)

            // Assert
            assertEquals(newAddress.city, contacts[ctid].addresses[aid].city, "Address should be updated (city)")
            assertEquals(
                newAddress.country,
                contacts[ctid].addresses[aid].country,
                "Address should be updated (country)"
            )
            assertEquals(
                newAddress.postalCode,
                contacts[ctid].addresses[aid].postalCode,
                "Address should be updated (postalCode)"
            )
            assertEquals(newAddress.street, contacts[ctid].addresses[aid].street, "Address should be updated (street)")
            assertEquals(newAddress.number, contacts[ctid].addresses[aid].number, "Address should be updated (number)")
        }

        @DisplayName("PUT /contacts/{contactId}/address/{addressId} - Update address to contact by addressId not found")
        @Test
        fun updateAddressFromContactWithCorrectContactIdAndExistingAddressIdNotLinkedTest() {
            // Arrange
            val contacts = generateContactStateForContactMethodDelete().toMutableList()
            val ctid = 1
            val aid = 2

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.of(contacts[ctid])
            }

            every { addressRepository.findByIdAndDeletedIsFalse(aid.toLong()) } answers {
                Optional.empty()
            }

            try {
                // Act
                contactService.updateAddressToContact(
                    ctid.toLong(),
                    aid.toLong(),
                    CreateAddressDTO("New Street", "1", "New City", "New Country", "New Zip")
                )
            } catch (e: Exception) {
                // Assert
                assertThrows(AddressNotFoundException::class.java) { throw e }
            }
        }

        @DisplayName("PUT /contacts/{contactId}/address/{addressId} - Update address to contact by contactId not found")
        @Test
        fun updateAddressToContactByIdNotFoundTest() {
            // Arrange
            val ctid = 100
            val aid = 1

            val newAddress = CreateAddressDTO("New Street", "1", "New City", "New Country", "New Zip")

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.empty()
            }

            try {
                // Act
                contactService.updateAddressToContact(ctid.toLong(), aid.toLong(), newAddress)
            } catch (e: ContactNotFoundException) {
                // Assert
                assertThrows(ContactNotFoundException::class.java) { throw e }
            }
        }

        @DisplayName("PUT /contacts/{contactId}/address/{addressId} - Update address to contact by existing contactId, not existing addressId but not linked (the address exists but it is not for that contact")
        @Test
        fun updateAddressToContactWithCorrectContactIdAndExistingAddressIdNotLinkedTest() {
            // Arrange
            val contacts = generateContactStateForContactMethodDelete().toMutableList()
            val ctid = 0
            val aid = 1

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.of(contacts[ctid])
            }

            every { addressRepository.findByIdAndDeletedIsFalse(aid.toLong()) } answers {
                Optional.of(contacts[2].addresses[1])
            }

            try {
                // Act
                contactService.updateAddressToContact(
                    ctid.toLong(),
                    aid.toLong(),
                    CreateAddressDTO("New Street", "1", "New City", "New Country", "New Zip")
                )
            } catch (e: Exception) {
                // Assert
                assertThrows(InvalidContactBindingException::class.java) { throw e }
            }
        }

        @DisplayName("DELETE /contacts/{contactId}/address/{addressId} - Delete address from contact by contactId and addressId")
        @Test
        fun deleteAddressFromContactTest() {
            // Arrange
            val contacts = generateContactStateForContactMethodDelete().toMutableList()
            val ctid = 2
            val aid = 1

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.of(contacts[ctid])
            }
            every { addressRepository.findByIdAndDeletedIsFalse(aid.toLong()) } answers {
                Optional.of(contacts[ctid].addresses[aid])
            }
            every { addressRepository.save(contacts[ctid].addresses[aid]) } answers {
                contacts[ctid].addresses.forEach {
                    if (it.id == aid.toLong()) it.clean()
                }
                contacts[ctid].addresses[aid]
            }

            // Act
            contactService.deleteAddressFromContact(ctid.toLong(), aid.toLong())

            // Assert
            assertEquals(2, contacts[ctid].addresses.size, "Address should not be removed but only cleaned (size fail)")
            assertEquals("*****", contacts[ctid].addresses[aid].street, "Address should be cleaned (street fail)")
            assertEquals("*****", contacts[ctid].addresses[aid].number, "Address should be cleaned (number fail)")
            assertEquals("*****", contacts[ctid].addresses[aid].city, "Address should be cleaned (city fail)")
            assertEquals("*****", contacts[ctid].addresses[aid].country, "Address should be cleaned (country fail)")
            assertEquals("*****", contacts[ctid].addresses[aid].postalCode, "Address should be cleaned (postalCode fail)")
        }

        @DisplayName("DELETE /contacts/{contactId}/address/{addressId} - Delete address from contact by existing contactId and not existing addressId")
        @Test
        fun deleteAddressFromContactWithCorrectContactIdAndNotExistingAddressIdTest() {
            // Arrange
            val contacts = generateContactStateForContactMethodDelete().toMutableList()
            val ctid = 0
            val aid = 100

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.of(contacts[ctid])
            }
            every { addressRepository.findByIdAndDeletedIsFalse(aid.toLong()) } answers {
                Optional.empty()
            }

            try {
                // Act
                contactService.deleteAddressFromContact(ctid.toLong(), aid.toLong())
            } catch (e: Exception) {
                // Assert
                assertThrows(AddressNotFoundException::class.java) { throw e }
            }
        }

        @DisplayName("DELETE /contacts/{contactId}/address/{addressId} - Delete address from contact by non existing contactId")
        @Test
        fun deleteAddressFromContactWithNotExistingContactIdTest() {
            // Arrange
            val ctid = 100
            val aid = 0

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.empty()
            }

            try {
                // Act
                contactService.deleteAddressFromContact(ctid.toLong(), aid.toLong())
            } catch (e: Exception) {
                // Assert
                assertThrows(ContactNotFoundException::class.java) { throw e }
            }
        }

        @DisplayName("DELETE /contacts/{contactId}/address/{addressId} - Delete address from contact by existing contactId, not existing addressId but not linked (the address exists but it is not for that contact")
        @Test
        fun deleteAddressFromContactWithCorrectContactIdAndExistingAddressIdNotLinkedTest() {
            // Arrange
            val contacts = generateContactStateForContactMethodDelete().toMutableList()
            val ctid = 0
            val aid = 1

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.of(contacts[ctid])
            }
            every { addressRepository.findByIdAndDeletedIsFalse(aid.toLong()) } answers {
                Optional.of(contacts[2].addresses[1])
            }

            try {
                // Act
                contactService.deleteAddressFromContact(ctid.toLong(), aid.toLong())
            } catch (e: Exception) {
                // Assert
                assertThrows(AddressNotFoundException::class.java) { throw e }
            }
        }
    }

    @Ok
    @Nested
    @DisplayName("/contacts/{contactId}/firstName")
    inner class ApiContactsContactIdFirstName {
        @DisplayName("PUT /contacts/{contactId}/firstName - Update first name to contact by id")
        @Test
        fun updateFirstNameToContactByIdTest() {
            // Arrange
            val contacts = generateContactState1().toMutableList()
            val target = 3
            val golden = contacts[target]
            val newFirstName = "NewFirstName"

            every { contactRepository.findByIdAndDeletedIsFalse(target.toLong()) } answers { Optional.of(golden) }
            every { contactRepository.save(golden) } answers {
                golden.firstName = newFirstName
                golden
            }

            // Act
            contactService.updateFirstNameToContact(target.toLong(), newFirstName)

            // Assert
            assertEquals(newFirstName, contacts[target].firstName, "First name should be updated")
        }

        @DisplayName("PUT /contacts/{contactId}/firstName - Update first name to contact by id not found")
        @Test
        fun updateFirstNameToContactByIdNotFoundTest() {
            // Arrange
            val target = 100
            val newFirstName = "NewFirstName"

            every { contactRepository.findByIdAndDeletedIsFalse(target.toLong()) } returns Optional.empty()

            try {
                // Act
                contactService.updateFirstNameToContact(target.toLong(), newFirstName)
            } catch (e: ContactNotFoundException) {
                // Assert
                assertThrows(ContactNotFoundException::class.java) { throw e }
            }
        }
    }

    @Ok
    @Nested
    @DisplayName("/contacts/{contactId}/lastName")
    inner class ApiContactsContactIdLastName {
        @DisplayName("PUT /contacts/{contactId}/lastName - Update last name to contact by id")
        @Test
        fun updateLastNameToContactByIdTest() {
            // Arrange
            val contacts = generateContactState1().toMutableList()
            val target = 3
            val golden = contacts[target]
            val newLastName = "NewLastName"

            every { contactRepository.findByIdAndDeletedIsFalse(target.toLong()) } answers { Optional.of(golden) }
            every { contactRepository.save(golden) } answers {
                golden.lastName = newLastName
                golden
            }

            // Act
            contactService.updateLastNameToContact(target.toLong(), newLastName)

            // Assert
            assertEquals(newLastName, contacts[target].lastName, "Last name should be updated")
        }

        @DisplayName("PUT /contacts/{contactId}/lastName - Update last name to contact by id not found")
        @Test
        fun updateLastNameToContactByIdNotFoundTest() {
            // Arrange
            val target = 100
            val newLastName = "NewLastName"

            every { contactRepository.findByIdAndDeletedIsFalse(target.toLong()) } returns Optional.empty()

            try {
                // Act
                contactService.updateLastNameToContact(target.toLong(), newLastName)
            } catch (e: ContactNotFoundException) {
                // Assert
                assertThrows(ContactNotFoundException::class.java) { throw e }
            }
        }
    }

    @Ok
    @Nested
    @DisplayName("/contacts/{contactId}/ssn")
    inner class ApiContactsContactIdSsn {
        @DisplayName("PUT /contacts/{contactId}/ssn - Update SSN to contact by id")
        @Test
        fun updateSSNToContactByIdTest() {
            // Arrange
            val contacts = generateContactState1().toMutableList()
            val target = 3
            val golden = contacts[target]
            val newSSN = "NewSSN"

            every { contactRepository.findByIdAndDeletedIsFalse(target.toLong()) } answers { Optional.of(golden) }
            every { contactRepository.save(golden) } answers {
                golden.ssn = newSSN
                golden
            }

            // Act
            contactService.updateSsnToContact(target.toLong(), newSSN)

            // Assert
            assertEquals(newSSN, contacts[target].ssn, "SSN should be updated")
        }

        @DisplayName("PUT /contacts/{contactId}/ssn - Update SSN to contact by id not found")
        @Test
        fun updateSSNToContactByIdNotFoundTest() {
            // Arrange
            val target = 100
            val newSSN = "NewSSN"

            every { contactRepository.findByIdAndDeletedIsFalse(target.toLong()) } returns Optional.empty()

            try {
                // Act
                contactService.updateSsnToContact(target.toLong(), newSSN)
            } catch (e: ContactNotFoundException) {
                // Assert
                assertThrows(ContactNotFoundException::class.java) { throw e }
            }
        }

        @DisplayName("DELETE /contacts/{contactId}/ssn - Delete SSN from contact by contactId")
        @Test
        fun deleteSSNFromContactTest() {
            // Arrange
            val contacts = generateContactStateForContactMethodDelete().toMutableList()
            val ctid = 2

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.of(contacts[ctid])
            }

            every { contactRepository.save(contacts[ctid]) } answers {
                contacts[ctid].ssn = null
                contacts[ctid]
            }

            // Act
            contactService.deleteSsnFromContact(ctid.toLong())

            // Assert
            assertNull(contacts[ctid].ssn, "SSN should be deleted")
        }

        @DisplayName("DELETE /contacts/{contactId}/ssn - Delete SSN from contact by non existing contactId")
        @Test
        fun deleteSSNFromContactWithNotExistingContactIdTest() {
            // Arrange
            val ctid = 100

            every { contactRepository.findByIdAndDeletedIsFalse(ctid.toLong()) } answers {
                Optional.empty()
            }

            try {
                // Act
                contactService.deleteSsnFromContact(ctid.toLong())
            } catch (e: Exception) {
                // Assert
                assertThrows(ContactNotFoundException::class.java) { throw e }
            }
        }
    }

    @Ok
    @Nested
    @DisplayName("/contacts/{contactId1}/{contactId2}/merge")
    inner class ApiContactsContactId1ContactId2Merge {
        @DisplayName("PUT /contacts/{contactId1}/{contactId2}/merge - Merge contacts with contactId1 and contactId2 into contactId1")
        @Test
        fun mergeContactsTest() {
            // Arrange
            var contact1 = Contact().apply {
                id = 1
                firstName = "John"
                lastName = "Doe"
                ssn = "123456789"
                category = Category.Customer
                addresses = mutableListOf(
                    generateAddress(1)
                )
                emails = mutableListOf(
                    generateEmail(2)
                )
                phoneNumbers = mutableListOf(
                    generateTelephone(1)
                )
            }

            val contact2 = Contact().apply {
                id = 2
                firstName = "John"
                lastName = "Doe"
                ssn = "123456789"
                category = Category.Customer
                addresses = mutableListOf(
                    generateAddress(2)
                )
                emails = mutableListOf(
                    generateEmail(1)
                )
                phoneNumbers = mutableListOf(
                    generateTelephone(1)
                )
            }

            val merged = Contact().apply {
                id = 1
                firstName = "John"
                lastName = "Doe"
                ssn = "123456789"
                addresses = mutableListOf(
                    generateAddress(1),
                    generateAddress(2)
                )
                emails = mutableListOf(
                    generateEmail(1),
                    generateEmail(2)
                )
                phoneNumbers = mutableListOf(
                    generateTelephone(1)
                )
            }

            every { contactRepository.findByIdAndDeletedIsFalse(1) } answers { Optional.of(contact1) }
            every { contactRepository.findByIdAndDeletedIsFalse(2) } answers { Optional.of(contact2) }
            every { contactRepository.save(merged) } answers {
                contact1 = merged
                merged
            }
            every { contactRepository.delete(contact2) } answers { }

            // Act
            contactService.mergeContacts(1, 2)

            // Assert
            assertEquals("John", contact1.firstName, "First name should be John")
            assertEquals("Doe", contact1.lastName, "Last name should be Doe")
            assertEquals("123456789", contact1.ssn, "SSN should be 123456789")
            assertEquals(2, contact1.addresses.size, "Addresses size should be 2")
            assertEquals(2, contact1.emails.size, "Emails size should be 2")
            assertEquals(1, contact1.phoneNumbers.size, "Phone numbers size should be 1")
            assertEquals(1, contact1.addresses[0].id, "Address 1 id should be 1")
            assertEquals(2, contact1.addresses[1].id, "Address 2 id should be 2")
            assertEquals(1, contact1.emails[0].id, "Email 1 id should be 1")
            assertEquals(2, contact1.emails[1].id, "Email 2 id should be 2")
            assertEquals(1, contact1.phoneNumbers[0].id, "Phone number id should be 1")
        }

        @DisplayName("PUT /contacts/{contactId1}/{contactId2}/merge - Merge contacts with same contactId (same contact)")
        @Test
        fun mergeContactsWithSameIdTest() {
            // Arrange
            val contactId1 = 1L
            val contactId2 = 1L

            // Act
            try {
                contactService.mergeContacts(contactId1, contactId2)
            } catch (e: Exception) {
                // Assert
                assertEquals("You are trying to merge the same Contact with id $contactId1", e.message)
                assertThrows(InvalidMergingIdsException::class.java) { throw e }
            }
        }

        @DisplayName("PUT /contacts/{contactId1}/{contactId2}/merge - Merge contacts with not existing contactId1")
        @Test
        fun mergeContactsWithNotExistingFirstContactIdTest() {
            // Arrange
            val contact = Contact().apply {
                id = 1
                firstName = "John"
                lastName = "Doe"
                ssn = "123456789"
                category = Category.Customer
                addresses = mutableListOf(
                    generateAddress(1)
                )
                emails = mutableListOf(
                    generateEmail(2)
                )
                phoneNumbers = mutableListOf(
                    generateTelephone(1)
                )
            }
            val contactNFId = 200L

            every { contactRepository.findByIdAndDeletedIsFalse(contact.id!!) } answers { Optional.of(contact) }
            every { contactRepository.findByIdAndDeletedIsFalse(contactNFId) } answers { Optional.empty() }

            // Act
            try {
                contactService.mergeContacts(contact.id!!, contactNFId)
            } catch (e: Exception) {
                // Assert
                assertEquals("Contact with id $contactNFId not found", e.message)
                assertThrows(ContactNotFoundException::class.java) { throw e }
            }
        }

        @DisplayName("PUT /contacts/{contactId1}/{contactId2}/merge - Merge contacts with not existing contactId2")
        @Test
        fun mergeContactsWithNotExistingSecondContactIdTest() {
            // Arrange
            val contact = Contact().apply {
                id = 1
                firstName = "John"
                lastName = "Doe"
                ssn = "123456789"
                category = Category.Customer
                addresses = mutableListOf(
                    generateAddress(1)
                )
                emails = mutableListOf(
                    generateEmail(2)
                )
                phoneNumbers = mutableListOf(
                    generateTelephone(1)
                )
            }
            val contactNFId = 200L

            every { contactRepository.findByIdAndDeletedIsFalse(contact.id!!) } answers { Optional.of(contact) }
            every { contactRepository.findByIdAndDeletedIsFalse(contactNFId) } answers { Optional.empty() }

            // Act
            try {
                contactService.mergeContacts(contactNFId, contact.id!!)
            } catch (e: Exception) {
                // Assert
                assertEquals("Contact with id $contactNFId not found", e.message)
                assertThrows(ContactNotFoundException::class.java) { throw e }
            }
        }

        @Nested
        @DisplayName("PUT /contacts/{contactId1}/{contactId2}/merge - Testing corner cases on first name")
        inner class MergeContactsFirstNameTests() {
            @DisplayName("PUT /contacts/{contactId1}/{contactId2}/merge - Merge contacts with first name blank and blank (expecting success)")
            @Test
            fun mergeContactsFirstNameBlankBlank() {
                var contact1 = Contact().apply {
                    id = 1
                    firstName = ""
                    lastName = "Doe"
                    ssn = "123456789"
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(1)
                    )
                    emails = mutableListOf(
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val contact2 = Contact().apply {
                    id = 2
                    firstName = ""
                    lastName = "Doe"
                    ssn = "123456789"
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val merged = Contact().apply {
                    id = 1
                    firstName = ""
                    lastName = "Doe"
                    ssn = "123456789"
                    addresses = mutableListOf(
                        generateAddress(1),
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1),
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                every { contactRepository.findByIdAndDeletedIsFalse(1) } answers { Optional.of(contact1) }
                every { contactRepository.findByIdAndDeletedIsFalse(2) } answers { Optional.of(contact2) }
                every { contactRepository.save(merged) } answers {
                    contact1 = merged
                    merged
                }
                every { contactRepository.delete(contact2) } answers { }

                // Act
                contactService.mergeContacts(1, 2)

                // Assert
                assertEquals("", contact1.firstName, "First name should be empty")
            }

            @DisplayName("PUT /contacts/{contactId1}/{contactId2}/merge - Merge contacts with first name not blank and blank (expecting success)")
            @Test
            fun mergeContactsFirstNameNotBlankBlank() {
                var contact1 = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(1)
                    )
                    emails = mutableListOf(
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val contact2 = Contact().apply {
                    id = 2
                    firstName = ""
                    lastName = "Doe"
                    ssn = "123456789"
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val merged = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    addresses = mutableListOf(
                        generateAddress(1),
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1),
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                every { contactRepository.findByIdAndDeletedIsFalse(1) } answers { Optional.of(contact1) }
                every { contactRepository.findByIdAndDeletedIsFalse(2) } answers { Optional.of(contact2) }
                every { contactRepository.save(merged) } answers {
                    contact1 = merged
                    merged
                }
                every { contactRepository.delete(contact2) } answers { }

                // Act
                contactService.mergeContacts(1, 2)

                // Assert
                assertEquals("John", contact1.firstName, "First name should be John")
            }

            @DisplayName("PUT /contacts/{contactId1}/{contactId2}/merge - Merge contacts with first name blank and not blank (exprecting success)")
            @Test
            fun mergeContactsFirstNameBlankNotBlank() {
                var contact1 = Contact().apply {
                    id = 1
                    firstName = ""
                    lastName = "Doe"
                    ssn = "123456789"
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(1)
                    )
                    emails = mutableListOf(
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val contact2 = Contact().apply {
                    id = 2
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val merged = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    addresses = mutableListOf(
                        generateAddress(1),
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1),
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                every { contactRepository.findByIdAndDeletedIsFalse(1) } answers { Optional.of(contact1) }
                every { contactRepository.findByIdAndDeletedIsFalse(2) } answers { Optional.of(contact2) }
                every { contactRepository.save(merged) } answers {
                    contact1 = merged
                    merged
                }
                every { contactRepository.delete(contact2) } answers { }

                // Act
                contactService.mergeContacts(1, 2)

                // Assert
                assertEquals("John", contact1.firstName, "First name should be John")
            }

            @DisplayName("PUT /contacts/{contactId1}/{contactId2}/merge - Merge contacts with first name not blank and not blank, different (expecting fail)")
            @Test
            fun mergeContactsFirstNameNotBlankNotBlankNotEquals() {
                val contact1 = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(1)
                    )
                    emails = mutableListOf(
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val contact2 = Contact().apply {
                    id = 2
                    firstName = "Jane"
                    lastName = "Doe"
                    ssn = "123456789"
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                every { contactRepository.findByIdAndDeletedIsFalse(1) } answers { Optional.of(contact1) }
                every { contactRepository.findByIdAndDeletedIsFalse(2) } answers { Optional.of(contact2) }

                try {
                    // Act
                    contactService.mergeContacts(1, 2)
                } catch (e: Exception) {
                    // Assert
                    assertEquals("Contacts cannot be merged due to firstName conflicts", e.message)
                    assertThrows(InvalidMergingContactsException::class.java) { throw e }
                }
            }
        }

        @Nested
        @DisplayName("PUT /contacts/{contactId1}/{contactId2}/merge - Testing corner cases on last name")
        inner class MergeContactsLastNameTests() {
            @DisplayName("PUT /contacts/{contactId1}/{contactId2}/merge - Merge contacts with last name blank and blank (expecting success)")
            @Test
            fun mergeContactsLastNameBlankBlank() {
                var contact1 = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = ""
                    ssn = "123456789"
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(1)
                    )
                    emails = mutableListOf(
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val contact2 = Contact().apply {
                    id = 2
                    firstName = "John"
                    lastName = ""
                    ssn = "123456789"
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val merged = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = ""
                    ssn = "123456789"
                    addresses = mutableListOf(
                        generateAddress(1),
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1),
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                every { contactRepository.findByIdAndDeletedIsFalse(1) } answers { Optional.of(contact1) }
                every { contactRepository.findByIdAndDeletedIsFalse(2) } answers { Optional.of(contact2) }
                every { contactRepository.save(merged) } answers {
                    contact1 = merged
                    merged
                }
                every { contactRepository.delete(contact2) } answers { }

                // Act
                contactService.mergeContacts(1, 2)

                // Assert
                assertEquals("", contact1.lastName, "Last name should be empty")
            }

            @DisplayName("PUT /contacts/{contactId1}/{contactId2}/merge - Merge contacts with last name not blank and blank (expecting success)")
            @Test
            fun mergeContactsLastNameNotBlankBlank() {
                var contact1 = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(1)
                    )
                    emails = mutableListOf(
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val contact2 = Contact().apply {
                    id = 2
                    firstName = "John"
                    lastName = ""
                    ssn = "123456789"
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val merged = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    addresses = mutableListOf(
                        generateAddress(1),
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1),
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                every { contactRepository.findByIdAndDeletedIsFalse(1) } answers { Optional.of(contact1) }
                every { contactRepository.findByIdAndDeletedIsFalse(2) } answers { Optional.of(contact2) }
                every { contactRepository.save(merged) } answers {
                    contact1 = merged
                    merged
                }
                every { contactRepository.delete(contact2) } answers { }

                // Act
                contactService.mergeContacts(1, 2)

                // Assert
                assertEquals("Doe", contact1.lastName, "Last name should be Doe")
            }

            @DisplayName("PUT /contacts/{contactId1}/{contactId2}/merge - Merge contacts with last name blank and not blank (exprecting success)")
            @Test
            fun mergeContactsLastNameBlankNotBlank() {
                var contact1 = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = ""
                    ssn = "123456789"
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(1)
                    )
                    emails = mutableListOf(
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val contact2 = Contact().apply {
                    id = 2
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val merged = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    addresses = mutableListOf(
                        generateAddress(1),
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1),
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                every { contactRepository.findByIdAndDeletedIsFalse(1) } answers { Optional.of(contact1) }
                every { contactRepository.findByIdAndDeletedIsFalse(2) } answers { Optional.of(contact2) }
                every { contactRepository.save(merged) } answers {
                    contact1 = merged
                    merged
                }
                every { contactRepository.delete(contact2) } answers { }

                // Act
                contactService.mergeContacts(1, 2)

                // Assert
                assertEquals("Doe", contact1.lastName, "Last name should be Doe")
            }

            @DisplayName("PUT /contacts/{contactId1}/{contactId2}/merge - Merge contacts with last name not blank and not blank, different (expecting fail)")
            @Test
            fun mergeContactsLastNameNotBlankNotBlankNotEquals() {
                val contact1 = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(1)
                    )
                    emails = mutableListOf(
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val contact2 = Contact().apply {
                    id = 2
                    firstName = "John"
                    lastName = "Smith"
                    ssn = "123456789"
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                every { contactRepository.findByIdAndDeletedIsFalse(1) } answers { Optional.of(contact1) }
                every { contactRepository.findByIdAndDeletedIsFalse(2) } answers { Optional.of(contact2) }

                try {
                    // Act
                    contactService.mergeContacts(1, 2)
                } catch (e: Exception) {
                    // Assert
                    assertEquals("Contacts cannot be merged due to lastName conflicts", e.message)
                    assertThrows(InvalidMergingContactsException::class.java) { throw e }
                }
            }
        }

        @Nested
        @DisplayName("PUT /contacts/{contactId1}/{contactId2}/merge - Testing corner cases on ssn")
        inner class MergeContactsSsnTests() {
            @DisplayName("PUT /contacts/{contactId1}/{contactId2}/merge - Merge contacts with ssn null null (expecting success)")
            @Test
            fun mergeContactsSsnNullNull() {
                var contact1 = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = "Doe"
                    ssn = null
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(1)
                    )
                    emails = mutableListOf(
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val contact2 = Contact().apply {
                    id = 2
                    firstName = "John"
                    lastName = "Doe"
                    ssn = null
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val merged = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = "Doe"
                    ssn = null
                    addresses = mutableListOf(
                        generateAddress(1),
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1),
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                every { contactRepository.findByIdAndDeletedIsFalse(1) } answers { Optional.of(contact1) }
                every { contactRepository.findByIdAndDeletedIsFalse(2) } answers { Optional.of(contact2) }
                every { contactRepository.save(merged) } answers {
                    contact1 = merged
                    merged
                }
                every { contactRepository.delete(contact2) } answers { }

                // Act
                contactService.mergeContacts(1, 2)

                // Assert
                assertNull(contact1.ssn, "SSN should be empty")
            }

            @DisplayName("PUT /contacts/{contactId1}/{contactId2}/merge - Merge contacts with ssn blank blank (expecting success)")
            @Test
            fun mergeContactsSsnBlankBlank() {
                var contact1 = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = "Doe"
                    ssn = ""
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(1)
                    )
                    emails = mutableListOf(
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val contact2 = Contact().apply {
                    id = 2
                    firstName = "John"
                    lastName = "Doe"
                    ssn = ""
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val merged = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = "Doe"
                    ssn = ""
                    addresses = mutableListOf(
                        generateAddress(1),
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1),
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                every { contactRepository.findByIdAndDeletedIsFalse(1) } answers { Optional.of(contact1) }
                every { contactRepository.findByIdAndDeletedIsFalse(2) } answers { Optional.of(contact2) }
                every { contactRepository.save(merged) } answers {
                    contact1 = merged
                    merged
                }
                every { contactRepository.delete(contact2) } answers { }

                // Act
                contactService.mergeContacts(1, 2)

                // Assert
                assertEquals("", contact1.ssn, "SSN should be empty")
            }

            @DisplayName("PUT /contacts/{contactId1}/{contactId2}/merge - Merge contacts with ssn not blank and blank (expecting success)")
            @Test
            fun mergeContactsSsnNotBlankBlank() {
                var contact1 = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(1)
                    )
                    emails = mutableListOf(
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val contact2 = Contact().apply {
                    id = 2
                    firstName = "John"
                    lastName = "Doe"
                    ssn = ""
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val merged = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    addresses = mutableListOf(
                        generateAddress(1),
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1),
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                every { contactRepository.findByIdAndDeletedIsFalse(1) } answers { Optional.of(contact1) }
                every { contactRepository.findByIdAndDeletedIsFalse(2) } answers { Optional.of(contact2) }
                every { contactRepository.save(merged) } answers {
                    contact1 = merged
                    merged
                }
                every { contactRepository.delete(contact2) } answers { }

                // Act
                contactService.mergeContacts(1, 2)

                // Assert
                assertEquals("123456789", contact1.ssn, "SSN should be 123456789")
            }

            @DisplayName("PUT /contacts/{contactId1}/{contactId2}/merge - Merge contacts with ssn blank and not blank (exprecting success)")
            @Test
            fun mergeContactsSsnBlankNotBlank() {
                var contact1 = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = "Doe"
                    ssn = ""
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(1)
                    )
                    emails = mutableListOf(
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val contact2 = Contact().apply {
                    id = 2
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val merged = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    addresses = mutableListOf(
                        generateAddress(1),
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1),
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                every { contactRepository.findByIdAndDeletedIsFalse(1) } answers { Optional.of(contact1) }
                every { contactRepository.findByIdAndDeletedIsFalse(2) } answers { Optional.of(contact2) }
                every { contactRepository.save(merged) } answers {
                    contact1 = merged
                    merged
                }
                every { contactRepository.delete(contact2) } answers { }

                // Act
                contactService.mergeContacts(1, 2)

                // Assert
                assertEquals("123456789", contact1.ssn, "SSN should be 123456789")
            }

            @DisplayName("PUT /contacts/{contactId1}/{contactId2}/merge - Merge contacts with ssn not blank and not blank, different (expecting fail)")
            @Test
            fun mergeContactsSsnNotBlankNotBlankNotEquals() {
                val contact1 = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(1)
                    )
                    emails = mutableListOf(
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val contact2 = Contact().apply {
                    id = 2
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "987654321"
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                every { contactRepository.findByIdAndDeletedIsFalse(1) } answers { Optional.of(contact1) }
                every { contactRepository.findByIdAndDeletedIsFalse(2) } answers { Optional.of(contact2) }

                try {
                    // Act
                    contactService.mergeContacts(1, 2)
                } catch (e: Exception) {
                    // Assert
                    assertEquals("Contacts cannot be merged due to ssn conflicts", e.message)
                    assertThrows(InvalidMergingContactsException::class.java) { throw e }
                }
            }
        }

        @Nested
        @DisplayName("PUT /contacts/{contactId1}/{contactId2}/merge - Testing corner cases on category")
        inner class MergeContactsCategoryTests() {
            @DisplayName("PUT /contacts/{contactId1}/{contactId2}/merge - Merge contacts with category Unknown Customer (expecting success)")
            @Test
            fun mergeContactsCategoryUnknownOther() {
                var contact1 = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    category = Category.Unknown
                    addresses = mutableListOf(
                        generateAddress(1)
                    )
                    emails = mutableListOf(
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val contact2 = Contact().apply {
                    id = 2
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val merged = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(1),
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1),
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                every { contactRepository.findByIdAndDeletedIsFalse(1) } answers { Optional.of(contact1) }
                every { contactRepository.findByIdAndDeletedIsFalse(2) } answers { Optional.of(contact2) }
                every { contactRepository.save(merged) } answers {
                    contact1 = merged
                    merged
                }
                every { contactRepository.delete(contact2) } answers { }

                // Act
                contactService.mergeContacts(1, 2)

                // Assert
                assertEquals(Category.Customer, contact1.category, "Category should be Customer")
            }

            @DisplayName("PUT /contacts/{contactId1}/{contactId2}/merge - Merge contacts with category Customer Unknown (expecting success)")
            @Test
            fun mergeContactsCategoryOtherUnknown() {
                var contact1 = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(1)
                    )
                    emails = mutableListOf(
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val contact2 = Contact().apply {
                    id = 2
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    category = Category.Unknown
                    addresses = mutableListOf(
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val merged = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(1),
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1),
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                every { contactRepository.findByIdAndDeletedIsFalse(1) } answers { Optional.of(contact1) }
                every { contactRepository.findByIdAndDeletedIsFalse(2) } answers { Optional.of(contact2) }
                every { contactRepository.save(merged) } answers {
                    contact1 = merged
                    merged
                }
                every { contactRepository.delete(contact2) } answers { }

                // Act
                contactService.mergeContacts(1, 2)

                // Assert
                assertEquals(Category.Customer, contact1.category, "Category should be Customer")
            }

            @DisplayName("PUT /contacts/{contactId1}/{contactId2}/merge - Merge contacts with category Unknown Unknown (expecting success)")
            @Test
            fun mergeContactsCategoryUnknownUnknown() {
                var contact1 = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    category = Category.Unknown
                    addresses = mutableListOf(
                        generateAddress(1)
                    )
                    emails = mutableListOf(
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val contact2 = Contact().apply {
                    id = 2
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    category = Category.Unknown
                    addresses = mutableListOf(
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val merged = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    category = Category.Unknown
                    addresses = mutableListOf(
                        generateAddress(1),
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1),
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                every { contactRepository.findByIdAndDeletedIsFalse(1) } answers { Optional.of(contact1) }
                every { contactRepository.findByIdAndDeletedIsFalse(2) } answers { Optional.of(contact2) }
                every { contactRepository.save(merged) } answers {
                    contact1 = merged
                    merged
                }
                every { contactRepository.delete(contact2) } answers { }

                // Act
                contactService.mergeContacts(1, 2)

                // Assert
                assertEquals(Category.Unknown, contact1.category, "Category should be Unknown")
            }

            @DisplayName("PUT /contacts/{contactId1}/{contactId2}/merge - Merge contacts with category Customer Professional (expecting fail)")
            @Test
            fun mergeContactsCategoryConflict() {
                val contact1 = Contact().apply {
                    id = 1
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    category = Category.Customer
                    addresses = mutableListOf(
                        generateAddress(1)
                    )
                    emails = mutableListOf(
                        generateEmail(2)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                val contact2 = Contact().apply {
                    id = 2
                    firstName = "John"
                    lastName = "Doe"
                    ssn = "123456789"
                    category = Category.Professional
                    addresses = mutableListOf(
                        generateAddress(2)
                    )
                    emails = mutableListOf(
                        generateEmail(1)
                    )
                    phoneNumbers = mutableListOf(
                        generateTelephone(1)
                    )
                }

                every { contactRepository.findByIdAndDeletedIsFalse(1) } answers { Optional.of(contact1) }
                every { contactRepository.findByIdAndDeletedIsFalse(2) } answers { Optional.of(contact2) }

                try {
                    // Act
                    contactService.mergeContacts(1, 2)
                } catch (e: Exception) {
                    // Assert
                    assertEquals("Contacts cannot be merged due to category conflicts", e.message)
                    assertThrows(InvalidMergingContactsException::class.java) { throw e }
                }
            }
        }
    }
}
