package com.cw6sem.repository;

import com.cw6sem.entity.ObjectToAppraise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ObjectToAppraiseRepository extends JpaRepository<ObjectToAppraise, Long> {
    @Query("select a from ObjectToAppraise a where a.objectType.type = ?1 and a.description = ?2")
    ObjectToAppraise findByTypeIdAndDescription(String type, String description);
    @Query("select a from ObjectToAppraise a where a.objectType.id = ?1")
    List<ObjectToAppraise> getByTypeId(Long id);
}
