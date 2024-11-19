package com.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.models.Execution;

public interface ExecutionRepository extends JpaRepository<Execution, Long> {

}
