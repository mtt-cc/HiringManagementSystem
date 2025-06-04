/*
package it.polito.waii_24.g20.analytics.services.implementation

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import it.polito.waii_24.g20.analytics.model.Event
import it.polito.waii_24.g20.analytics.repositories.*
import it.polito.waii_24.g20.analytics.services.interfaces.EventService
import java.time.LocalDateTime
import java.util.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Service
class EventServiceImpl(private val eventRepository: EventRepository) : EventService {
    private val logger: Logger = LoggerFactory.getLogger(EventServiceImpl::class.java)

    @Transactional
    override fun saveEvent(data: String): Event {
        val newEvent = Event(
            id = UUID.randomUUID().toString(), // Generate a random UUID for the ID
            data = data, // Use the provided message data
            timestamp = LocalDateTime.now() // Set the current timestamp
        )
        // Save the message to the database
        return eventRepository.save(newEvent)
    }

    override  fun getLastTenEvents(): List<Event> {
        return eventRepository.findTop10ByOrderByTimestampDesc()
    }
}*/
