package com.harrisburg.app.domain;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThreadDto {
    private UUID threadId;
    private String threadName;
    private String lastMessage;
    private String updatedAt;
    private String updatedDate;
    private int unreadCount;
}
