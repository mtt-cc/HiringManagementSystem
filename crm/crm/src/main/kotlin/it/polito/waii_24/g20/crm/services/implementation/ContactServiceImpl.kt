package it.polito.waii_24.g20.crm.services.implementation

import it.polito.waii_24.g20.crm.common.ContactCategory
import it.polito.waii_24.g20.crm.common.isValidEmail
import it.polito.waii_24.g20.crm.common.isValidTelephone
import it.polito.waii_24.g20.crm.dtos.DTO
import it.polito.waii_24.g20.crm.dtos.contact.*
import it.polito.waii_24.g20.crm.entities.BaseEntity
import it.polito.waii_24.g20.crm.entities.contact.*
import it.polito.waii_24.g20.crm.exceptions.ContactServiceInternalErrorException
import it.polito.waii_24.g20.crm.repositories.*
import it.polito.waii_24.g20.crm.services.interfaces.ContactService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.kafka.core.KafkaTemplate

@Service
class ContactServiceImpl(
    private val contactRepository: ContactRepository,
    private val skillRepository: SkillRepository,
    private val professionalDetailsRepository: ProfessionalDetailsRepository,
    private val customerDetailsRepository: CustomerDetailsRepository,
    private val emailRepository: EmailRepository,
    private val phoneNumberRepository: PhoneNumberRepository,
    private val addressRepository: AddressRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>
) : ContactService {
    private val logger: Logger = LoggerFactory.getLogger(ContactServiceImpl::class.java)

    override fun getContacts(page: Int, size: Int, firstName: String?, lastName: String?, category: ContactCategory?, ssn: String?, country: String?, city: String?, contactOnly: Boolean): Page<ContactDTO> {
        val pageable = PageRequest.of(page, size)
        return contactRepository.getFilteredContacts(
            firstName = firstName,
            lastName = lastName,
            category = category,
            ssn = ssn,
            country = country,
            city = city,
            pageable = pageable
        ).map { it.toContactOnlyDTO() }
    }

    @Transactional
    override fun createContact(contact: ContactDTO): ContactDTO {
        try {
            logger.info("Creating contact: $contact")
            val c = Contact()

            c.firstName = contact.firstName
            c.lastName = contact.lastName
            c.category = contact.category
            c.ssn = contact.ssn

            logger.info("Processing emails")
            c.emails = contact.emails.map { e ->
                Email().also {
                    it.email = e.email.takeIf { v -> isValidEmail(v) } ?: throw IllegalArgumentException("Invalid email format")
                    it.contact = c
                }
            }.toMutableList()
            logger.info("Emails processed")

            logger.info("Processing phone numbers")
            c.phoneNumbers = contact.phoneNumbers.map { p ->
                PhoneNumber().also {
                    it.phoneNumber = p.telephone.takeIf { v -> isValidTelephone(v) } ?: throw IllegalArgumentException("Invalid telephone format")
                    it.contact = c
                }
            }.toMutableList()
            logger.info("Phone numbers processed")

            logger.info("Processing addresses")
            c.addresses = contact.addresses.map { a ->
                Address().also {
                    it.street = a.street
                    it.number = a.number
                    it.city = a.city
                    it.country = a.country
                    it.postalCode = a.postalCode
                    it.contact = c
                }
            }.toMutableList()
            logger.info("Addresses processed")

            when(contact.category) {
                ContactCategory.PROFESSIONAL -> {
                    logger.info("Processing professional details")
                    if (contact.relatedProfessional == null) {
                        throw IllegalArgumentException("Professional contact must have a related professional")
                    }
                    c.professionalDetails = ProfessionalDetails().also {
                        it.contact = c
                        it.notes = contact.relatedProfessional.notes
                        it.dailyRate = contact.relatedProfessional.dailyRate
                        it.location = contact.relatedProfessional.location
                        it.employmentState = contact.relatedProfessional.employmentState
                        it.skills = contact.relatedProfessional.skills.map { s -> findOrInsertSkill(s) }
                        it.jobOffers = mutableListOf()
                        it.candidations = mutableListOf()
                    }
                    c.customerDetails = null
                    professionalDetailsRepository.save(c.professionalDetails!!)
                    logger.info("Professional details processed")
                }
                ContactCategory.CUSTOMER -> {
                    logger.info("Processing customer details")
                    if (contact.relatedCustomer == null) {
                        throw IllegalArgumentException("Customer contact must have a related customer")
                    }
                    c.customerDetails = CustomerDetails().also {
                        it.contact = c
                        it.notes = contact.relatedCustomer.notes
                        it.preferences = contact.relatedCustomer.preferences
                        it.jobOffers = mutableListOf()
                    }
                    c.professionalDetails = null
                    customerDetailsRepository.save(c.customerDetails!!)
                    logger.info("Customer details processed")
                }
                else -> {
                    logger.info("No category related fields to process")
                    c.professionalDetails = null
                    c.customerDetails = null
                }
            }

            val savedContact = contactRepository.save(c)
            logger.info("Contact created: $savedContact")
            return savedContact.toDTO()
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    override fun getContact(id: Long): ContactDTO {
        return contactRepository.findContactByIdAndDeletedIsFalse(id).orElseThrow { IllegalArgumentException("Contact not found") }.toDTO()
    }

    @Transactional
    override fun updateContact(id: Long, contact: ContactDTO): ContactDTO {
        try {
            logger.info("Updating contact with id $id: $contact")
            val c = contactRepository.findContactByIdAndDeletedIsFalse(id).orElseThrow { IllegalArgumentException("Contact not found") }
            logger.info("Contact found: $c")

            if (c.category != ContactCategory.UNKNOWN && contact.category != c.category) {
                throw IllegalArgumentException("Current category (${c.category}) is different from the new category (${contact.category}), only Unknown category can be changed.")
            }

            c.firstName = contact.firstName
            c.lastName = contact.lastName
            c.ssn = contact.ssn
            c.category = contact.category

            logger.info("Merging emails")
            mergeEmails(contact.emails, c)
            logger.info("Emails merged")

            logger.info("Merging phone numbers")
            mergePhoneNumbers(contact.phoneNumbers, c)
            logger.info("Phone numbers merged")

            logger.info("Merging addresses")
            mergeAddresses(contact.addresses, c)
            logger.info("Addresses merged")

            when(c.category) {
                ContactCategory.CUSTOMER -> {
                    logger.info("Processing customer details")
                    if (contact.relatedCustomer == null) {
                        throw IllegalArgumentException("Customer contact must have a related customer")
                    }

                    c.customerDetails?.let {
                        it.notes = contact.relatedCustomer.notes
                        it.preferences = contact.relatedCustomer.preferences
                    } ?: run {
                        logger.info("Creating new customer details")
                        c.customerDetails = CustomerDetails().also {
                            it.contact = c
                            it.notes = contact.relatedCustomer.notes
                            it.preferences = contact.relatedCustomer.preferences
                        }
                    }
                    customerDetailsRepository.save(c.customerDetails!!)
                    logger.info("Customer details processed")
                    c.professionalDetails = null
                }
                ContactCategory.PROFESSIONAL -> {
                    logger.info("Processing professional details")
                    if (contact.relatedProfessional == null) {
                        throw IllegalArgumentException("Professional contact must have a related professional")
                    }

                    c.professionalDetails?.let {
                        it.notes = contact.relatedProfessional.notes
                        it.dailyRate = contact.relatedProfessional.dailyRate
                        it.location = contact.relatedProfessional.location
                        it.employmentState = contact.relatedProfessional.employmentState
                        it.skills = contact.relatedProfessional.skills.map { s -> findOrInsertSkill(s) }
                    } ?: run {
                        logger.info("Creating new professional details")
                        c.professionalDetails = ProfessionalDetails().also {
                            it.contact = c
                            it.notes = contact.relatedProfessional.notes
                            it.dailyRate = contact.relatedProfessional.dailyRate
                            it.location = contact.relatedProfessional.location
                            it.employmentState = contact.relatedProfessional.employmentState
                            it.skills = contact.relatedProfessional.skills.map { s -> findOrInsertSkill(s) }
                        }
                    }
                    professionalDetailsRepository.save(c.professionalDetails!!)
                    logger.info("Professional details processed")
                    c.customerDetails = null
                }
                else -> {
                    logger.info("No category related fields to process")
                    c.professionalDetails = null
                    c.customerDetails = null
                }
            }
            val savedContact = contactRepository.save(c)
            logger.info("Contact updated: $savedContact")
            return savedContact.toDTO()
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    @Transactional
    override fun deleteContact(id: Long) {
        try {
            logger.info("Deleting contact with id $id")
            val c = contactRepository.findContactByIdAndDeletedIsFalse(id).orElseThrow { IllegalArgumentException("Contact not found") }
            c.clean()
            contactRepository.save(c)
            logger.info("Contact deleted")
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    override fun getProfessionalHeaders(): List<ContactHeaderDTO> {
        return contactRepository.findContactsByCategoryAndDeletedIsFalse(ContactCategory.PROFESSIONAL).map { it.toContactHeaderDTO() }
    }

    override fun getCustomerHeaders(): List<ContactHeaderDTO> {
        return contactRepository.findContactsByCategoryAndDeletedIsFalse(ContactCategory.CUSTOMER).map { it.toContactHeaderDTO() }
    }

    override fun getProfessionals(page: Int, size: Int, skillsSet: List<String>, jobOfferId: Long, availableOnly: Boolean): Page<ContactDTO> {
        return if (skillsSet.isEmpty())
            professionalDetailsRepository.findBySkillsContainingAllEmpty(jobOfferId, availableOnly, PageRequest.of(page, size)).map { it.contact.toDTO() }
        else
            professionalDetailsRepository.findBySkillsContainingAll(skillsSet, skillsSet.size, jobOfferId, availableOnly, PageRequest.of(page, size)).map { it.contact.toDTO() }
    }

    private fun findOrInsertSkill(skill: String): Skill {
        try {
            return skillRepository.findSkillBySkill(skill)
                ?: run {
                    val newSkill = skillRepository.save(Skill().apply { this.skill = skill })
                    newSkill
                }
        } catch (e: Exception) {
            logger.debug(e.stackTraceToString())
            throw ContactServiceInternalErrorException("Failed to find or insert skill")
        }
    }

    private fun <D : DTO<Long>, E : BaseEntity<Long>, R : JpaRepository<E, Long>> mergeLists(
        updatedList: List<D>,
        entityList: MutableList<E>,
        repository: R,
        compare: D.(E) -> Boolean,
        create: D.() -> E,
        update: E.(D) -> E,
        delete: E.() -> Unit
    ) {
        val dbMap = entityList.associateBy { it.id }

        val toAdd : List<D> = updatedList.filter { it.id !in dbMap }
        val toUpdate : List<D> = updatedList.filter { it.id in dbMap && !it.compare(dbMap[it.id]!!) }
        val toDelete : Map<Long?, E> = dbMap.filterKeys { it !in updatedList.map { i -> i.id } }

        toAdd.forEach { e ->
            entityList.add(repository.save(e.create()))
        }

        toUpdate.forEach { e ->
            dbMap[e.id]!!.update(e)
            repository.save(dbMap[e.id]!!)
        }

        toDelete.values.forEach { e ->
            e.delete()
            repository.save(e)
        }
    }

    private fun mergeEmails(updatedEmails: List<EmailDTO>, contact: Contact) {
        fun EmailDTO.compare(e: Email) = this.email == e.email

        fun EmailDTO.create() = Email().also {
            it.email = this.email
            it.contact = contact
        }

        fun Email.update(e: EmailDTO) = this.also {
            it.email = e.email
        }

        mergeLists(
            updatedEmails,
            contact.emails,
            emailRepository,
            EmailDTO::compare,
            EmailDTO::create,
            Email::update,
            Email::clean
        )
    }

    private fun mergePhoneNumbers(updatedPhoneNumbers: List<PhoneNumberDTO>, contact: Contact) {
        fun PhoneNumberDTO.compare(p: PhoneNumber) = this.telephone == p.phoneNumber

        fun PhoneNumberDTO.create() = PhoneNumber().also {
            it.phoneNumber = this.telephone
            it.contact = contact
        }

        fun PhoneNumber.update(p: PhoneNumberDTO) = this.also {
            it.phoneNumber = p.telephone
        }

        mergeLists(
            updatedPhoneNumbers,
            contact.phoneNumbers,
            phoneNumberRepository,
            PhoneNumberDTO::compare,
            PhoneNumberDTO::create,
            PhoneNumber::update,
            PhoneNumber::clean
        )
    }

    private fun mergeAddresses(updatedAddresses: List<AddressDTO>, contact: Contact) {
        fun AddressDTO.compare(a: Address) = this.street == a.street && this.number == a.number && this.city == a.city && this.country == a.country && this.postalCode == a.postalCode

        fun AddressDTO.create() = Address().also {
            it.street = this.street
            it.number = this.number
            it.city = this.city
            it.country = this.country
            it.postalCode = this.postalCode
            it.contact = contact
        }

        fun Address.update(a: AddressDTO) = this.also {
            it.street = a.street
            it.number = a.number
            it.city = a.city
            it.country = a.country
            it.postalCode = a.postalCode
        }

        mergeLists(
            updatedAddresses,
            contact.addresses,
            addressRepository,
            AddressDTO::compare,
            AddressDTO::create,
            Address::update,
            Address::clean
        )
    }

    override fun sendKafkaMessage(message: String) {
        kafkaTemplate.send("test-topic", message)
    }
}