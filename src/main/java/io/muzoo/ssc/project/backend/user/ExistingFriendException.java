package io.muzoo.ssc.project.backend.user;

public class ExistingFriendException extends FriendControllerException{
    public ExistingFriendException(String msg) {
        super(msg);
    }
}
