package app.car.cap07.interfaces.incoming.errorhandling

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class DefaultErrorHandler(
    val messageSource: MessageSource
) {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException): ErrorResponse {
        val messages = ex.bindingResult.fieldErrors.map {
            getMessage(it)
        }
        return ErrorResponse(messages)
    }

    fun getMessage(fieldError: FieldError) : ErrorData {
            return ErrorData(messageSource.getMessage(fieldError, LocaleContextHolder.getLocale()))
    }
}