package io.muzoo.ssc.project.backend.friend;

import jakarta.json.JsonArray;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FriendDTO {

    private boolean success;

    private JsonArray friends;

    private boolean request;

}
