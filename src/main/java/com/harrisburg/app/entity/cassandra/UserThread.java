package com.harrisburg.app.entity.cassandra;

import com.harrisburg.app.entity.cassandra.keys.UserThreadKey;
import lombok.*;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("user_thread")
public class UserThread {

    @PrimaryKey
    private UserThreadKey key;

    @Column("thread_id")
    @CassandraType(type = CassandraType.Name.UUID)
    private UUID threadId;

    @Column("thread_name")
    private String threadName;

    @Column("last_message")
    private String lastMessage;

    private String avatar;

    @Column("unread_count")
    private int unreadCount;
}
