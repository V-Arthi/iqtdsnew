package com.main.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.main.models.HIPAAExtract;

public interface HIPAAExtractRepository extends JpaRepository<HIPAAExtract, Long> {

	@Query(value = "SELECT e FROM HIPAAExtract e WHERE e.dataId=:id and e.env=:env")
	public List<HIPAAExtract> findDataForIDAndEnv(@Param("id") String id, @Param("env") String env);

}
