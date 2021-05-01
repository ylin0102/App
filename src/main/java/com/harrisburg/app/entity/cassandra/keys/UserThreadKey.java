package com.harrisburg.app.entity.cassandra.keys;

import lombok.*;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@PrimaryKeyClass
public class UserThreadKey {

    @PrimaryKeyColumn(name = "user_id", type = PrimaryKeyType.PARTITIONED)
    private int userId;

    @PrimaryKeyColumn(name = "updated_at", ordinal = 0, ordering = Ordering.DESCENDING)
    private LocalDateTime updatedAt;
}
