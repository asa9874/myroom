package com.example.myroom.domain.recommand.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.myroom.domain.recommand.model.RecommandHistory;

public interface RecommandHistoryRepository extends JpaRepository<RecommandHistory, Long> {
    Page<RecommandHistory> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);
}
