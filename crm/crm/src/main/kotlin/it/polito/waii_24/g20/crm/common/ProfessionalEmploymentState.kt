package it.polito.waii_24.g20.crm.common

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.util.*

enum class ProfessionalEmploymentState {
    EMPLOYED,
    UNEMPLOYED,
    NOT_AVAILABLE;

    override fun toString(): String {
        return when (this) {
            EMPLOYED -> "employed"
            UNEMPLOYED -> "unemployed"
            NOT_AVAILABLE -> "not_available"
        }
    }

    companion object {
        fun fromString(value: String): ProfessionalEmploymentState {
            return when (value.lowercase(Locale.getDefault())) {
                "employed" -> EMPLOYED
                "unemployed" -> UNEMPLOYED
                "not_available" -> NOT_AVAILABLE
                else -> throw IllegalArgumentException("Invalid value for ProfessionalEmploymentState")
            }
        }
    }
}

class ProfessionalEmploymentStateDeserializer : JsonDeserializer<ProfessionalEmploymentState>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ProfessionalEmploymentState {
        return ProfessionalEmploymentState.fromString(p.text)
    }
}