package com.harrisburg.app.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Integer id;
    private String username;
    private String firstName;
    private String lastName;
}
