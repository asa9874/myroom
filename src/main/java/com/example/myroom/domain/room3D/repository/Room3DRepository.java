package com.example.myroom.domain.room3D.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.myroom.domain.room3D.model.Room3D;

public interface Room3DRepository extends JpaRepository<Room3D, Long> {

    Optional<Room3D> findByIdAndMemberId(Long id, Long memberId);

    Page<Room3D> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);
}
