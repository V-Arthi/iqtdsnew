package com.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.models.Testcase;

public interface TestcaseRepository extends JpaRepository<Testcase, Long> {

}
