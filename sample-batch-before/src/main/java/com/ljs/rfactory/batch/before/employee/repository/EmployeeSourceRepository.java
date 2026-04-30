package com.ljs.rfactory.batch.before.employee.repository;

import com.ljs.rfactory.batch.before.employee.entity.EmployeeSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeSourceRepository extends JpaRepository<EmployeeSource, Long> {

    Page<EmployeeSource> findByStatus(String status, Pageable pageable);
}
