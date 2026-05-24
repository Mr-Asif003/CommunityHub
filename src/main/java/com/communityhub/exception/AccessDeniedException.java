package com.communityhub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// FIX: UserNotFoundException was being thrown for "not a member" / "cannot edit
//      another user's message" cases, which is semantically wrong and produces
//      misleading 404 responses.  Use this exception for those cases instead.
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }
}