package com.example.myroom.domain.recommand.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.myroom.domain.recommand.model.RecommendedModel;

@Repository
public interface RecommendedModelRepository extends JpaRepository<RecommendedModel, Long> {
    List<RecommendedModel> findByRecommendationSessionId(Long recommendationSessionId);
}
