package com.harrisburg.app.repository.cassandra;

import com.harrisburg.app.entity.cassandra.UserThread;
import com.harrisburg.app.entity.cassandra.keys.UserThreadKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface UserThreadRepository extends CassandraRepository<UserThread, UserThreadKey> {
    List<UserThread> findByKeyUserId(int userId);
}
