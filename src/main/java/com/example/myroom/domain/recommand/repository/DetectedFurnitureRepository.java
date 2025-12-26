package com.example.myroom.domain.recommand.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.myroom.domain.recommand.model.DetectedFurniture;

@Repository
public interface DetectedFurnitureRepository extends JpaRepository<DetectedFurniture, Long> {
    List<DetectedFurniture> findByRoomImageId(Long roomImageId);
}
