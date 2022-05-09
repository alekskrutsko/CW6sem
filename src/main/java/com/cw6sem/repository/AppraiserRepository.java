package com.cw6sem.repository;

import com.cw6sem.entity.Appraiser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AppraiserRepository extends JpaRepository<Appraiser, Long> {
}
