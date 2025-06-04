/*
package it.polito.waii_24.g20.analytics.controllers

import it.polito.waii_24.g20.analytics.model.Event
import it.polito.waii_24.g20.analytics.services.implementation.EventServiceImpl
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/events")
class EventController(private val eventServiceImpl: EventServiceImpl) {

    @GetMapping("")
    fun getEvents(): List<Event> {
        return eventServiceImpl.getLastTenEvents()
    }
}*/
