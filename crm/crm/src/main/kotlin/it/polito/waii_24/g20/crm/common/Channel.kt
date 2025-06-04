package it.polito.waii_24.g20.crm.common

/**
 * Enum class representing the possible channel
 * The channels are:
 * - Email
 * - PhoneNumber
 */
enum class Channel {
    Email,
    PhoneNumber;

    companion object {
        fun toEnum(channel: String): Channel {
            return when(channel) {
                "Email" -> Email
                "PhoneNumber" -> PhoneNumber
                else -> throw IllegalArgumentException("Invalid channel")
            }
        }
    }

    /**
     * Override of the toString method.
     * @return the string representation of the channel.
     */
    override fun toString(): String {
        return when(this) {
            Email -> "Email"
            PhoneNumber -> "PhoneNumber"
        }
    }
}