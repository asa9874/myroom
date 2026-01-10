package com.example.myroom.domain.model3D.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.myroom.domain.model3D.model.Model3D;

public interface Model3DRepository extends JpaRepository<Model3D, Long> {
    Page<Model3D> findByCreatorIdAndNameContaining(Long creatorId, String name, Pageable pageable);
    Page<Model3D> findByCreatorId(Long creatorId, Pageable pageable);

    Page<Model3D> findByIsSharedTrue(Pageable pageable);
    Page<Model3D> findByIsSharedTrueAndNameContaining(String name, Pageable pageable);
}
