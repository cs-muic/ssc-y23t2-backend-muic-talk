package io.muzoo.ssc.project.backend.chat;

import jakarta.json.JsonArray;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatDTO {
    private boolean success;
    private JsonArray chat;
}
