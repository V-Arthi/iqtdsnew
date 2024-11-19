package com.main.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.main.models.JenkinsRun;

public interface JenkinsRunRepository extends JpaRepository<JenkinsRun, Long> {

	@Query(name = "count_created", value = "SELECT COUNT(r.createdDataID) FROM JenkinsRun r WHERE r.createdDataID!=null")
	public long createdCount();

	@Query("SELECT j FROM JenkinsRun j WHERE j.buildURL!=null AND j.createdDataID=null")
	List<JenkinsRun> findAllNonCreated();
}
