package com.harrisburg.app.entity.cassandra;

import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("participant_hash_code")
public class ParticipantHashCode {

    @PrimaryKeyColumn(name = "participant_hash", type = PrimaryKeyType.PARTITIONED)
    private int participantHash;

    @Column("thread_id")
    @CassandraType(type = CassandraType.Name.UUID)
    private UUID threadId;
}
