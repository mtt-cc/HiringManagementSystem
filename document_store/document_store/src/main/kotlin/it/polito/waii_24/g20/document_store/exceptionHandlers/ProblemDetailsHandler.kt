package it.polito.waii_24.g20.document_store.exceptionHandlers

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ProblemDetailsHandler: ResponseEntityExceptionHandler() {

    @ExceptionHandler(DocumentNotFoundException::class)
    fun handleDocumentNotFound(e: DocumentNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message !!)

    @ExceptionHandler(DuplicateDocumentException::class)
    fun handleDuplicateDocument(e: DuplicateDocumentException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.message !!)

    @ExceptionHandler(DocumentServiceInternalErrorException::class)
    fun handleDocumentServiceInternalError(e: DocumentServiceInternalErrorException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.message !!)

    @ExceptionHandler(InvalidDocumentMetadata::class)
    fun handleInvalidDocumentMetadata(e: InvalidDocumentMetadata) =
//        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message !!)
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.stackTraceToString() )
}

/**
 * Exception thrown when a document is not found.
 * @param message The message to be shown.
 */
class DocumentNotFoundException(message: String = "The document does not exist.") : Exception(message)

/**
 * Exception thrown when a document is duplicated.
 * @param message The message to be shown.
 */
class DuplicateDocumentException(message: String = "The document already exists.") : Exception(message)

/**
 * Exception thrown when an internal error occurs in the document service.
 * @param message The message to be shown.
 */
class DocumentServiceInternalErrorException(message: String = "Internal error in the document service.") : Exception(message)

/**
 * Exception thrown when the document metadata is invalid.
 * @param message The message to be shown.
 */
class InvalidDocumentMetadata(message: String = "Invalid document metadata.") : Exception(message)