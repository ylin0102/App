package com.harrisburg.app.repository.cassandra;

import com.harrisburg.app.entity.cassandra.Thread;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

public interface ThreadRepository extends CassandraRepository<Thread, UUID> {

}
