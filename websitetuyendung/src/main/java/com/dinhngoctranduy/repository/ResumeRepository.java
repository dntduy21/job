package com.dinhngoctranduy.repository;

import com.dinhngoctranduy.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long>,
        JpaSpecificationExecutor<Resume> {

}
