package io.muzoo.ssc.project.backend.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.muzoo.ssc.project.backend.Friend;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FriendDTO {

    private boolean success;

    private List<Friend> friends;

    private boolean request;

}
