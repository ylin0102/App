package com.harrisburg.app.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetMessageFromThreadResponse {
    private boolean success;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    private List<ThreadMessage> messages;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String updatedAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String threadName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID threadId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String updatedDate;
}
