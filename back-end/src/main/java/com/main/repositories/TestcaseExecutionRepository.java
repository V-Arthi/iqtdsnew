package com.main.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.main.models.TestcaseExecution;

public interface TestcaseExecutionRepository extends JpaRepository<TestcaseExecution, Long> {

	@Query("SELECT t FROM TestcaseExecution t WHERE t.status !='Done'")
	List<TestcaseExecution> findAllInProgress();
	
	@Query("SELECT t FROM TestcaseExecution t WHERE t.status !='EDIExtractions Completed'")
	List<TestcaseExecution> findAllFacetsLoadPending();
}
