package io.muzoo.ssc.project.backend.user;

public class UsernameNotUniqueException extends UserControllerException {

    public UsernameNotUniqueException(String message) {
        super(message);
    }
}
