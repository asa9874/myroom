package com.example.myroom.domain.model3D.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.myroom.domain.model3D.model.FurnitureCategory;
import com.example.myroom.domain.model3D.model.Model3D;

public interface Model3DRepository extends JpaRepository<Model3D, Long> {
    Page<Model3D> findByCreatorIdAndNameContaining(Long creatorId, String name, Pageable pageable);
    Page<Model3D> findByCreatorId(Long creatorId, Pageable pageable);
    long countByCreatorId(Long creatorId);

    Page<Model3D> findByCreatorIdAndFurnitureType(Long creatorId, FurnitureCategory furnitureType, Pageable pageable);
    Page<Model3D> findByCreatorIdAndFurnitureTypeAndNameContaining(Long creatorId, FurnitureCategory furnitureType, String name, Pageable pageable);

    Page<Model3D> findByIsSharedTrue(Pageable pageable);
    Page<Model3D> findByIsSharedTrueAndNameContaining(String name, Pageable pageable);

    Page<Model3D> findByIsSharedTrueAndStatus(String status, Pageable pageable);
    Page<Model3D> findByIsSharedTrueAndNameContainingAndStatus(String name, String status, Pageable pageable);
    Page<Model3D> findByIsSharedTrueAndFurnitureTypeAndStatus(FurnitureCategory furnitureType, String status, Pageable pageable);
    Page<Model3D> findByIsSharedTrueAndFurnitureTypeAndNameContainingAndStatus(FurnitureCategory furnitureType, String name, String status, Pageable pageable);

    List<Model3D> findByIsVectorDbTrainedFalse();
    
    List<Model3D> findByCreatorIdAndStatus(Long creatorId, String status);
}
