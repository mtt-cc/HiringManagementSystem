package it.polito.waii_24.g20.crm.repositories.customImplementations

import it.polito.waii_24.g20.crm.common.JobOfferStateLabel
import it.polito.waii_24.g20.crm.entities.jobOffer.JobOffer
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class JobOfferRepositoryCustomImpl : JobOfferRepositoryCustom {
    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getFilteredJobOffers(states: List<JobOfferStateLabel>, customerId: Long?, professionalId: Long?, searchKey: String?, pageable: Pageable): Page<JobOffer> {
        val cb : CriteriaBuilder = em.criteriaBuilder
        val cq : CriteriaQuery<JobOffer> = cb.createQuery(JobOffer::class.java)

        val root = cq.from(JobOffer::class.java)

        cq.select(root)

        var whereStmt = cb.equal(cb.literal(1), 1)

        if (states.isNotEmpty()) {
            val statesPredicate = root.get<JobOfferStateLabel>("status").`in`(states)
            whereStmt = cb.and(whereStmt, statesPredicate)
        }

        if (customerId != null) {
            val customerIdPredicate = cb.equal(root.get<Long>("customerId"), customerId)
            whereStmt = cb.and(whereStmt, customerIdPredicate)
        }

        if (professionalId != null) {
            val professionalIdPredicate = cb.equal(root.get<Long>("professionalId"), professionalId)
            whereStmt = cb.and(whereStmt, professionalIdPredicate)
        }

        if (!searchKey.isNullOrEmpty()) {
            val searchKeyPredicate = cb.or(
                cb.like(root.get<String>("notes"), "%$searchKey%"),
                cb.like(root.get<String>("description"), "%$searchKey%"),
                cb.like(root.get<String>("title"), "%$searchKey%")
            )
            whereStmt = cb.and(whereStmt, searchKeyPredicate)
        }

        cq.where(whereStmt)

        val query = em.createQuery(cq)
        query.firstResult = pageable.pageNumber * pageable.pageSize
        query.maxResults = pageable.pageSize

        val result = query.resultList as List<JobOffer>

        val countCq: CriteriaQuery<Long> = cb.createQuery(Long::class.java)
        val countRoot = countCq.from(JobOffer::class.java)
        countCq.select(cb.count(countRoot))

        var countWhereStmt = cb.equal(cb.literal(1), 1)

        if (states.isNotEmpty()) {
            val statesPredicate = countRoot.get<JobOfferStateLabel>("status").`in`(states)
            countWhereStmt = cb.and(countWhereStmt, statesPredicate)
        }

        if (customerId != null) {
            val customerIdPredicate = cb.equal(countRoot.get<Long>("customerId"), customerId)
            countWhereStmt = cb.and(countWhereStmt, customerIdPredicate)
        }

        if (professionalId != null) {
            val professionalIdPredicate = cb.equal(countRoot.get<Long>("professionalId"), professionalId)
            countWhereStmt = cb.and(countWhereStmt, professionalIdPredicate)
        }

        if (!searchKey.isNullOrEmpty()) {
            val searchKeyPredicate = cb.or(
                cb.like(countRoot.get<String>("notes"), "%$searchKey%"),
                cb.like(countRoot.get<String>("description"), "%$searchKey%"),
                cb.like(countRoot.get<String>("title"), "%$searchKey%")
            )
            countWhereStmt = cb.and(countWhereStmt, searchKeyPredicate)
        }

        countCq.where(countWhereStmt)

        val totalResults: Long = em.createQuery(countCq).singleResult

        return PageImpl(result, pageable, totalResults)
    }
}