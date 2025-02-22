package com.LearnFree.LearnFreeServer.repository;

import com.LearnFree.LearnFreeServer.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Optional<Subject> findByCode(String code);
}