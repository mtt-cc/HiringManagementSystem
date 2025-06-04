package it.polito.waii_24.g20.document_store

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DocumentStoreApplication

fun main(args: Array<String>) {
    runApplication<DocumentStoreApplication>(*args)
}