package com.harrisburg.app.repository.cassandra;

import com.harrisburg.app.entity.cassandra.ParticipantHashCode;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.Optional;
import java.util.UUID;

public interface ParticipantHashCodeRepository extends CassandraRepository<ParticipantHashCode, UUID> {
    Optional<ParticipantHashCode> findByParticipantHash(int participantCode);
}
