package com.example.myroom.domain.recommand.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.myroom.domain.recommand.model.RecommandResult;

public interface RecommandResultRepository extends JpaRepository<RecommandResult, Long> {
    @Modifying
    @Query(value = "DELETE FROM recommand_result_model3d WHERE model3d_id = :model3dId", nativeQuery = true)
    void deleteModel3DLinks(@Param("model3dId") Long model3dId);
}
