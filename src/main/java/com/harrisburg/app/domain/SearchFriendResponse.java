package com.harrisburg.app.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchFriendResponse {
    private boolean success;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    private User user;
}
