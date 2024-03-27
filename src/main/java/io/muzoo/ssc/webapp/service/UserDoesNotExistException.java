package io.muzoo.ssc.webapp.service;

public class UserDoesNotExistException extends UserServiceException {

    public UserDoesNotExistException(String message) {
        super(message);
    }
}


