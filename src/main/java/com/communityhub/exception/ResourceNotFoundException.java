package com.communityhub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// FIX: "Chat room not found" and "Message not found" are not UserNotFoundExceptions.
//      Use this exception for any missing document regardless of its type.
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}