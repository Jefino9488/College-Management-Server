package com.LearnFree.LearnFreeServer.repository;

import com.LearnFree.LearnFreeServer.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollegeRepository extends JpaRepository<College, Long> {
}