package it.polito.waii_24.g20.crm.repositories.customImplementations

import it.polito.waii_24.g20.crm.entities.contact.Contact
import it.polito.waii_24.g20.crm.common.ContactCategory
import it.polito.waii_24.g20.crm.entities.contact.Address
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.Query
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.*

class ContactRepositoryImpl : ContactRepositoryCustom {
    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getFilteredContacts(
        firstName: String?, lastName: String?, category: ContactCategory?, ssn: String?, country: String?, city: String?, pageable: Pageable
    ): Page<Contact> {
        fun tlc(s: String): String = s.lowercase(Locale.getDefault())

        val cb : CriteriaBuilder = em.criteriaBuilder
        val cq : CriteriaQuery<Contact> = cb.createQuery(Contact::class.java)

        val root = cq.from(Contact::class.java)

        cq.select(root)

        var whereStmt = cb.equal(root.get<Boolean>("deleted"), false)

        if (!firstName.isNullOrEmpty()) whereStmt = cb.and(whereStmt, cb.like(cb.lower(root.get("firstName")), "%${tlc(firstName)}%"))
        if (!lastName.isNullOrEmpty()) whereStmt = cb.and(whereStmt, cb.like(cb.lower(root.get("lastName")), "%${tlc(lastName)}%"))
        if (category != null) whereStmt = cb.and(whereStmt, cb.equal(root.get<ContactCategory>("category"), category))
        if (!ssn.isNullOrEmpty()) whereStmt = cb.and(whereStmt, cb.like(root.get("ssn"), "%$ssn%"))
        if (!country.isNullOrEmpty() || !city.isNullOrEmpty()) {
            val addresses = root.get<MutableList<Address>>("addresses")
            val addressJoin = root.join<MutableList<Address>, Address>("addresses")
            if (!country.isNullOrEmpty()) whereStmt = cb.and(whereStmt, cb.like(cb.lower(addressJoin.get("country")), "%${tlc(country)}%"))
            if (!city.isNullOrEmpty()) whereStmt = cb.and(whereStmt, cb.like(cb.lower(addressJoin.get("city")), "%${tlc(city)}%"))
        }

        cq.where(whereStmt)

        val query : Query = em.createQuery(cq)
        query.firstResult = pageable.pageNumber * pageable.pageSize
        query.maxResults = pageable.pageSize

        val result = query.resultList as List<Contact>

        // Query per il conteggio totale dei risultati
        val countCq: CriteriaQuery<Long> = cb.createQuery(Long::class.java)
        val countRoot = countCq.from(Contact::class.java)
        countCq.select(cb.count(countRoot))

        // Riapplica gli stessi criteri di filtro della query originale
        var countWhereStmt = cb.equal(countRoot.get<Boolean>("deleted"), false)


        countCq.where(countWhereStmt)

        val totalResults: Long = em.createQuery(countCq).singleResult

        return PageImpl(result, pageable, totalResults)
    }
}