package it.polito.waii_24.g20.crm_old.unit.contacts.testUtils

import it.polito.waii_24.g20.crm_old.entities.Address
import it.polito.waii_24.g20.crm_old.entities.Contact
import it.polito.waii_24.g20.crm_old.entities.Email
import it.polito.waii_24.g20.crm_old.entities.Telephone
import it.polito.waii_24.g20.crm_old.unit.common.randomCategory
import it.polito.waii_24.g20.crm_old.unit.common.randomString

fun generateAddress(seed: Long): Address {
    return Address().apply {
        this.id = seed
        this.city = "City $seed"
        this.number = seed.toString()
        this.street = "Street $seed"
        this.country = "Italy"
        this.postalCode = seed.toString().padStart(5,'1')
    }
}

fun generateEmail(seed: Long): Email {
    return Email().apply {
        this.id = seed
        this.email = "email.$seed@mail.com"
    }
}

fun generateTelephone(seed: Long): Telephone {
    return Telephone().apply {
        this.id = seed
        this.telephone = "+39 {${seed.toString().padStart(10, '3')}}"
    }
}

fun generateContact(id: Long, emails: MutableList<Email>, telephones: MutableList<Telephone>, addresses: MutableList<Address>, ssn: String?): Contact {
    return Contact().apply {
        this.id = id
        this.firstName = randomString(10)
        this.lastName = randomString(15)
        this.ssn = ssn
        this.category = randomCategory()
        this.emails = emails
        this.phoneNumbers = telephones
        this.addresses = addresses
    }
}

fun generateContactState1() : List<Contact> {
    val e1 = generateEmail(1)
    val e2 = generateEmail(2)
    val e3 = generateEmail(3)

    val t1 = generateTelephone(1)
    val t2 = generateTelephone(2)

    val a1 = generateAddress(1)
    val a2 = generateAddress(2)

    val c1 = generateContact(
        1,
        mutableListOf(e1,e2),
        mutableListOf(),
        mutableListOf(a1),
        "ssn1"
    )

    val c2 = generateContact(
        2,
        mutableListOf(e3),
        mutableListOf(t1),
        mutableListOf(),
        null
    )

    val c3 = generateContact(
        3,
        mutableListOf(e1,e3),
        mutableListOf(t1, t2),
        mutableListOf(a2),
        "ssn1"
    )

    val c4 = generateContact(
        4,
        mutableListOf(e1,e2),
        mutableListOf(),
        mutableListOf(a1),
        null
    )

    return listOf(c1,c2,c3,c4)
}

fun generateContactState2(
    fn: String? = null,
    ln: String? = null,
    ssn: String? = null
    ) : List<Contact> {
    val e1 = generateEmail(1)
    val e2 = generateEmail(2)
    val e3 = generateEmail(3)

    val t1 = generateTelephone(1)
    val t2 = generateTelephone(2)

    val a1 = generateAddress(1)
    val a2 = generateAddress(2)

    val c1 = generateContact(
        1,
        mutableListOf(e1,e2),
        mutableListOf(),
        mutableListOf(a1),
        "ssn1"
    )

    val c2 = generateContact(
        2,
        mutableListOf(e3),
        mutableListOf(t1),
        mutableListOf(),
        null
    )

    val c3 = generateContact(
        3,
        mutableListOf(e1,e3),
        mutableListOf(t1, t2),
        mutableListOf(a2),
        "ssn1"
    )

    val c4 = generateContact(
        4,
        mutableListOf(e1,e2),
        mutableListOf(),
        mutableListOf(a1),
        null
    )

    val c5 = generateContact(
        5,
        mutableListOf(e1,e2),
        mutableListOf(),
        mutableListOf(a1),
        null
    )

    if (fn != null) c1.firstName = fn
    if (ln != null) c1.lastName = ln
    if (ssn != null) c1.ssn = ssn

    if (fn != null) c5.firstName = fn
    if (ln != null) c5.lastName = ln
    if (ssn != null) c5.ssn = ssn

    if (fn != null) c4.firstName = fn
    if (ln != null) c3.lastName = ln
    if (ssn != null) c2.ssn = ssn

    return listOf(c1,c2,c3,c4,c5)
}

fun generateContactStateForContactMethodDelete() : List<Contact> {
    val e1 = generateEmail(0)
    val e2 = generateEmail(1)
    val e3 = generateEmail(2)

    val t1 = generateTelephone(0)
    val t2 = generateTelephone(1)

    val a1 = generateAddress(0)
    val a2 = generateAddress(1)

    val c1 = generateContact(
        0,
        mutableListOf(e1,e2),
        mutableListOf(),
        mutableListOf(a1),
        "ssn1"
    )

    val c2 = generateContact(
        1,
        mutableListOf(e3),
        mutableListOf(t1),
        mutableListOf(),
        null
    )

    val c3 = generateContact(
        2,
        mutableListOf(e1,e3),
        mutableListOf(t1, t2),
        mutableListOf(a1, a2),
        "ssn1"
    )

    val c4 = generateContact(
        3,
        mutableListOf(e1,e2),
        mutableListOf(),
        mutableListOf(a1),
        null
    )

    return listOf(c1,c2,c3,c4)
}