package com.cw6sem.repository;

import com.cw6sem.entity.ObjectType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ObjectTypeRepository extends JpaRepository<ObjectType, Long> {
    ObjectType findObjectTypeByType(String type);
    Optional<ObjectType> getObjectTypeByType(String type);
}
