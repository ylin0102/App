package com.harrisburg.app.entity.cassandra;

import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("thread")
public class Thread {

    @PrimaryKeyColumn(name = "thread_id", type = PrimaryKeyType.PARTITIONED)
    @CassandraType(type = CassandraType.Name.UUID)
    private UUID threadId;

    @Column("thread_name")
    private String threadName;

    @Column("last_message")
    private String lastMessage;

    private String avatar;

    @Column("create_at")
    private LocalDateTime createdAt;

    private Set<Integer> participants;
}
