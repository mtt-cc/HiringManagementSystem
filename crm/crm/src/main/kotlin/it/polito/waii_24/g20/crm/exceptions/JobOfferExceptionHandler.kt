package it.polito.waii_24.g20.crm.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class JobOfferExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(IllegalStateTransitionException::class)
    fun handleIllegalStateTransitionException(exception: IllegalStateTransitionException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.message!!)
}