package it.polito.waii_24.g20.crm

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(info = Info(title = "Contact Management System API", description = "API to manage contacts and messages", version = "1.0"))
class CrmApplication

fun main(args: Array<String>) {
	runApplication<CrmApplication>(*args)
}
