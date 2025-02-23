package com.CollegeManager.CollegeManagerServer.repository;

import com.CollegeManager.CollegeManagerServer.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BatchRepository extends JpaRepository<Batch, Long> {
    List<Batch> findByDepartmentId(Long departmentId);
}