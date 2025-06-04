package it.polito.waii_24.g20.com_manager.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class EmailExceptionHandler: ResponseEntityExceptionHandler() {

    @ExceptionHandler
    fun handleInvalidEmailAddressException(e: InvalidEmailAddressException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)

    @ExceptionHandler
    fun handleImpossibleToSendEmailException(e: ImpossibleToSendEmailException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.message!!)

    @ExceptionHandler
    fun handleImpossibleToUploadAttachmentException(e: ImpossibleToUploadAttachmentException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.message!!)

}

class InvalidEmailAddressException(message: String) : Exception(message)
class ImpossibleToSendEmailException(message: String) : Exception(message)
class ImpossibleToUploadAttachmentException(message: String) : Exception(message)