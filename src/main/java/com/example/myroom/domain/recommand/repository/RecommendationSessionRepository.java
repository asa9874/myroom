package com.example.myroom.domain.recommand.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.myroom.domain.recommand.model.RecommendationSession;

@Repository
public interface RecommendationSessionRepository extends JpaRepository<RecommendationSession, Long> {
    Optional<RecommendationSession> findByRoomImageId(Long roomImageId);
}
