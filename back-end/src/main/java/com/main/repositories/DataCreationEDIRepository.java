package com.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.models.DataCreationEDIFile;

public interface DataCreationEDIRepository extends JpaRepository<DataCreationEDIFile, Long> {

}
