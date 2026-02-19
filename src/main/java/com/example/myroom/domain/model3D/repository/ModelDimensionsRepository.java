package com.example.myroom.domain.model3D.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.myroom.domain.model3D.model.ModelDimensions;

@Repository
public interface ModelDimensionsRepository extends JpaRepository<ModelDimensions, Long> {
    Optional<ModelDimensions> findByModel3DId(Long model3dId);
    boolean existsByModel3DId(Long model3dId);
}
