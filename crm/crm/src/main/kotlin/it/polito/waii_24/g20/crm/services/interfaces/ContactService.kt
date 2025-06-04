package it.polito.waii_24.g20.crm.services.interfaces

import it.polito.waii_24.g20.crm.common.ContactCategory
import it.polito.waii_24.g20.crm.dtos.contact.*
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
interface ContactService {

    /**
     * This method is used to get a page of contacts, filtered by the given parameters.
     * @param page The page number
     * @param size The size of the page
     * @param firstName The first name of the contact
     * @param lastName The last name of the contact
     * @param category The category of the contact
     * @param ssn The social security number of the contact
     * @param country The country of the contact
     * @param city The city of the contact
     * @param contactOnly A boolean that is true related professional details and related customer details will be omitted
     * @return A page of contacts
     */
    fun getContacts(
        page: Int,
        size: Int,
        firstName: String?,
        lastName: String?,
        category: ContactCategory?,
        ssn: String?,
        country: String?,
        city: String?,
        contactOnly: Boolean
    ) : Page<ContactDTO>

    /**
     * This method is used to create a new contact. The id is ignored for any entity passed as a parameter.
     *
     * @param contact The contact to be created
     * @return The created contact
     */
    fun createContact(
        contact: ContactDTO
    ) : ContactDTO

    /**
     * This method is used to get a contact by its id.
     *
     * @param id The id of the contact to be retrieved
     * @return The contact with the given id
     */
    fun getContact(
        id: Long
    ) : ContactDTO

    /**
     * This method is used to update a contact. The id is used to identify the contact to be updated.
     *
     * @param id The id of the contact to be updated
     * @param contact The contact to be updated
     * @return The updated contact
     */
    fun updateContact(
        id: Long,
        contact: ContactDTO
    ) : ContactDTO

    /**
     * This method is used to delete a contact by its id.
     *
     * @param id The id of the contact to be deleted
     */
    fun deleteContact(
        id: Long
    )

    /**
     * This method is used to get the list of headers for professional contacts. Useful for autocomplete.
     *
     * @return The list of headers for professional contacts
     */
    fun getProfessionalHeaders() : List<ContactHeaderDTO>

    /**
     * This method is used to get the list of headers for customer contacts. Useful for autocomplete.
     *
     * @return The list of headers for customer contacts
     */
    fun getCustomerHeaders() : List<ContactHeaderDTO>

    fun getProfessionals(page: Int, size: Int, skillsSet: List<String>, jobOfferId: Long, availableOnly: Boolean): Page<ContactDTO>

    fun sendKafkaMessage(msg: String)
}