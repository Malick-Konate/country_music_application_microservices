package com.konate.music_application.catalogsubdomain.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.converter.HttpMessageNotReadableException;
import static org.springframework.http.HttpStatus.*;


@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public HttpErrorInfo handleNotFoundException(WebRequest request, Exception ex) {
        return createHttpErrorInfo(NOT_FOUND, request, ex);
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public HttpErrorInfo handleResourceNotFoundException(WebRequest request, Exception ex) {
        return createHttpErrorInfo(NOT_FOUND, request, ex);
    }


    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(InvalidInputException.class)
    public HttpErrorInfo handleInvalidInputException(WebRequest request, Exception ex) {
        return createHttpErrorInfo(UNPROCESSABLE_ENTITY, request, ex);
    }


    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(InconsistentAlbumException.class)
    public HttpErrorInfo handleInconsistentAlbumException(WebRequest request, Exception ex) {
        return createHttpErrorInfo(UNPROCESSABLE_ENTITY, request, ex);
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public HttpErrorInfo handleGeneralException(WebRequest request, Exception ex) {
        return createHttpErrorInfo(INTERNAL_SERVER_ERROR, request, ex);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public HttpErrorInfo handleHttpMessageNotReadableException(WebRequest request, Exception ex) {
        return createHttpErrorInfo(BAD_REQUEST, request, ex);
    }

    private HttpErrorInfo createHttpErrorInfo(HttpStatus httpStatus, WebRequest request, Exception ex) {
        final String path = request.getDescription(false);
        final String message = ex.getMessage();


        return new HttpErrorInfo(httpStatus, path, message);
    }
}