package io.muzoo.ssc.project.backend.user;

public class UserDoesNotExistException extends UserControllerException {

    public UserDoesNotExistException(String message) {
        super(message);
    }
}
