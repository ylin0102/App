package com.harrisburg.app.repository;

import com.harrisburg.app.entity.ContactRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRelationRepository extends JpaRepository<ContactRelation, Integer> {
    List<ContactRelation> findByUserId(Integer userId);
    Optional<ContactRelation> findByUserIdAndContactId(Integer userId, Integer contactId);
}
