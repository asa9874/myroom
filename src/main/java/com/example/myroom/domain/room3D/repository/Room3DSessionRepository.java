package com.example.myroom.domain.room3D.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.myroom.domain.room3D.model.Room3DSession;

public interface Room3DSessionRepository extends JpaRepository<Room3DSession, Long> {
    Page<Room3DSession> findByMemberId(Long memberId, Pageable pageable);
}
