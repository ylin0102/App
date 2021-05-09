package com.harrisburg.app.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThreadMessage {
    private Integer userId;
    private String displayName;
    private String content;
    private LocalDateTime createdAt;
    private UUID threadId;
}
