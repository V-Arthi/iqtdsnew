package com.main.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.main.models.JobInternal;

public interface InternalJobRepository extends JpaRepository<JobInternal, Long> {

	@Override
	@Query(value = "SELECT * FROM `jobs` j ORDER BY `id` desc", nativeQuery = true)
	List<JobInternal> findAll();
}
