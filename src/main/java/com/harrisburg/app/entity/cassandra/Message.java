package com.harrisburg.app.entity.cassandra;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.harrisburg.app.entity.cassandra.keys.MessageKey;
import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("message")
public class Message {

    @PrimaryKey
    private MessageKey key;

    @Column("user_id")
    private int userId;

    @Column("user_name")
    private String displayName;

    private String content;

    @Column("create_at")
    private LocalDateTime createdAt;
}
