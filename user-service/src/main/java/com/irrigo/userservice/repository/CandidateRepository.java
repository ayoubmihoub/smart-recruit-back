package com.irrigo.userservice.repository;

import com.irrigo.userservice.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    Optional<Candidate> findByUserId(Long userId);
}