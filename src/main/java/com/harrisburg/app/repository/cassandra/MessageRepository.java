package com.harrisburg.app.repository.cassandra;

import com.harrisburg.app.entity.cassandra.Message;
import com.harrisburg.app.entity.cassandra.keys.MessageKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends CassandraRepository<Message, MessageKey> {
    List<Message> findByKeyThreadId(UUID threadId);
}
