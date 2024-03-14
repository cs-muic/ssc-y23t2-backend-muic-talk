package io.muzoo.ssc.webapp.service;

public class UsernameNotUniqueException extends UserServiceException {

    public UsernameNotUniqueException(String message) {
        super(message);
    }
}


