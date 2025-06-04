package it.polito.waii_24.g20.document_store.dtos

/**
 * Data Transfer Object representing the content of a document.
 *
 * @param id[Long] the id of the document
 * @param content[ByteArray] the content of the document
 */
data class DocumentContentDTO(
    val id: Long,
    val content: ByteArray,
) {
    /**
     * Overridden equals method for the DocumentContentDTO class.
     *
     * @param other[Any] the other object to compare
     * @return[Boolean] true if the two objects are equal, false otherwise
     *
     * @todo check if it is needed
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DocumentContentDTO

        if (id != other.id) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    /**
     * Overridden hashCode method for the DocumentContentDTO class.
     *
     * @return[Int] the hash code of the object
     *
     * @todo check if it is needed
     */
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }
}
