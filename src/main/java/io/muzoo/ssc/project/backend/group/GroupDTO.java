package io.muzoo.ssc.project.backend.group;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.json.JsonArray;import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GroupDTO {
    private boolean success;
    private JsonArray groups;
}