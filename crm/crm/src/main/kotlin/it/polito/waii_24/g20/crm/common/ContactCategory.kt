package it.polito.waii_24.g20.crm.common

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.util.*

enum class ContactCategory {
    CUSTOMER,
    PROFESSIONAL,
    UNKNOWN;

    override fun toString(): String {
        return when (this) {
            CUSTOMER -> "customer"
            PROFESSIONAL -> "professional"
            UNKNOWN -> "unknown"
        }
    }

    companion object {
        fun fromString(value: String): ContactCategory {
            return when (value.lowercase(Locale.getDefault())) {
                "customer" -> CUSTOMER
                "professional" -> PROFESSIONAL
                "unknown" -> UNKNOWN
                else -> throw IllegalArgumentException("Invalid value $value for ContactCategory")
            }
        }
    }
}

class ContactCategoryDeserializer : JsonDeserializer<ContactCategory>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ContactCategory {
        return ContactCategory.fromString(p.text)
    }
}