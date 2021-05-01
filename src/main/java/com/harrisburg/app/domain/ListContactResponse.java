package com.harrisburg.app.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListContactResponse {

    private boolean success;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    private List<User> contacts;
}
