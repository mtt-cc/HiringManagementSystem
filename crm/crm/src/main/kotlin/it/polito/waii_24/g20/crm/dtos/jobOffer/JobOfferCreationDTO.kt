package it.polito.waii_24.g20.crm.dtos.jobOffer

data class JobOfferCreationDTO(
    val title: String,
    val description: String,
    val customerId: Long,
    val skills: List<String>,
    val duration: Int,
    val budget: Double
)
