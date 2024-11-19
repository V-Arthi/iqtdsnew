package com.main.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.main.models.ClaimAccuracyConditions;

public interface ClaimAccuracyConditionsRepository extends JpaRepository<ClaimAccuracyConditions, Long> {
	@Query("SELECT m FROM ClaimAccuracyConditions m WHERE m.active=1")
	public List<ClaimAccuracyConditions> findAllActive();
	
	@Query("SELECT CASE WHEN count(m)> 0 THEN true ELSE false END  FROM ClaimAccuracyConditions m WHERE m.condition=:condition and m.active=1")
	public boolean existsByCondition(@Param("condition") String condition);
}
