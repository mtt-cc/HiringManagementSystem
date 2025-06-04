package it.polito.waii_24.g20.crm.controllers

import it.polito.waii_24.g20.crm.common.ContactCategory
import it.polito.waii_24.g20.crm.dtos.contact.ContactDTO
import it.polito.waii_24.g20.crm.dtos.contact.ContactHeaderDTO
import it.polito.waii_24.g20.crm.services.interfaces.ContactService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/contacts")
class ContactController(private val contactService: ContactService) {

    private val logger : Logger = LoggerFactory.getLogger(ContactController::class.java)

    @GetMapping("")
    fun getContacts(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, name = "first_name") firstName: String?,
        @RequestParam(required = false, name = "last_name") lastName: String?,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) ssn: String?,
        @RequestParam(required = false) country: String?,
        @RequestParam(required = false) city: String?
    ): Page<ContactDTO> {
        try {
            // KAFKA
            contactService.sendKafkaMessage( "GET CONTACTS" )

            if (page < 0 || size <= 0) {
                throw IllegalArgumentException("Page and size must be positive")
            }

            return contactService.getContacts(
                page = page,
                size = size,
                firstName = firstName,
                lastName = lastName,
                category = category?.let { ContactCategory.fromString(it) },
                ssn = ssn,
                country = country,
                city = city,
                contactOnly = true
            )
        }
        catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    @GetMapping("/{id}")
    fun getContact(
        @PathVariable id: Long
    ): ContactDTO {
        try {
            // KAFKA
            contactService.sendKafkaMessage( "GET CONTACT: $id" )

            return contactService.getContact(id)
        }
        catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    @PostMapping("")
    fun createContact(
        @RequestBody(required = true) contact: ContactDTO
    ): Long {
        try {
            // KAFKA
            contactService.sendKafkaMessage( "POST CONTACT" )

            logger.info("Creating contact: $contact")
            val c = contactService.createContact(contact)
            logger.info("Contact created: $c")
            return c.id
        }
        catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    @PutMapping("/{id}")
    fun updateContact(
        @PathVariable id: Long,
        @RequestBody(required = true) contact: ContactDTO
    ) {
        try {
            // KAFKA
            contactService.sendKafkaMessage( "PUT CONTACT: $id" )

            logger.info("Updating contact with id $id: $contact")

            if (id != contact.id) {
                throw IllegalArgumentException("The id in the path and in the body must be the same")
            }

            val c = contactService.updateContact(id, contact)
            logger.info("Contact updated: $c")
        }
        catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    @DeleteMapping("/{id}")
    fun deleteContact(
        @PathVariable id: Long
    ) {
        try {
            // KAFKA
            contactService.sendKafkaMessage( "DELETE CONTACT: $id" )

            logger.info("Deleting contact with id $id")
            contactService.deleteContact(id)
            logger.info("Contact deleted")
        }
        catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    @GetMapping("/professional-headers")
    fun getProfessionalHeaders(): List<ContactHeaderDTO> {
        try {
            return contactService.getProfessionalHeaders()
        }
        catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    @GetMapping("/customer-headers")
    fun getCustomerHeaders(): List<ContactHeaderDTO> {
        try {
            return contactService.getCustomerHeaders()
        }
        catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }

    @GetMapping("/professionals")
    fun getProfessionals(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, name = "skills_set") skillsSet: List<String>? = null,
        @RequestParam(required = false, name = "job_offer_id") jobOfferId: Long?,
        @RequestParam(required = false, name = "available_only") availableOnly: Boolean?,
    ) : Page<ContactDTO> {
        try {
            if (page < 0 || size <= 0) {
                throw IllegalArgumentException("Page and size must be positive")
            }

            return contactService.getProfessionals(
                page = page,
                size = size,
                skillsSet = skillsSet ?: emptyList(),
                jobOfferId = jobOfferId ?: 1,
                availableOnly = availableOnly ?: false
            )
        }
        catch (e: Exception) {
            logger.info(e.message)
            throw e
        } catch (e: Exception) {
            logger.info(e.message)
            throw e
        }
    }
}