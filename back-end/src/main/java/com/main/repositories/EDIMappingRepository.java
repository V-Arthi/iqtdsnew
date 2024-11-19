package com.main.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.main.models.EDIMapping;

public interface EDIMappingRepository extends JpaRepository<EDIMapping, String> {

	@Query("SELECT m FROM EDIMapping m WHERE m.valueOverridable=1")
	public List<EDIMapping> findAllMockUpEligible();
}
