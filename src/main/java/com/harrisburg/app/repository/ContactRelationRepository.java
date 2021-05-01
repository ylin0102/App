package com.harrisburg.app.repository;

import com.harrisburg.app.entity.ContactRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRelationRepository extends JpaRepository<ContactRelation, Integer> {
    List<ContactRelation> findByUserId(Integer userId);
}
