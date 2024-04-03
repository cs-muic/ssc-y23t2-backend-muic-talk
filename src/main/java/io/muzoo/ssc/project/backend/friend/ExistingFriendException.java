package io.muzoo.ssc.project.backend.friend;

public class ExistingFriendException extends FriendControllerException {
    public ExistingFriendException(String msg) {
        super(msg);
    }
}
