package io.muzoo.ssc.project.backend.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.muzoo.ssc.project.backend.Friend;
import jakarta.json.JsonArray;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class FriendDTO {

    private boolean success;

    private JsonArray friends;

    private boolean request;

}
