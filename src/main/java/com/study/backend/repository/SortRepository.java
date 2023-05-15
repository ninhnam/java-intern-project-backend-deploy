package com.study.backend.repository;

import com.study.backend.entity.Product;
import com.study.backend.entity.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SortRepository extends JpaRepository<Sort, Long> {
    List<Sort> findByUserId(Long userId);
}