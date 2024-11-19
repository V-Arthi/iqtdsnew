package com.main.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.main.models.MetaXWalk;

public interface MetaXWalkRepository extends JpaRepository<MetaXWalk, String> {
	
	@Query(value = "SELECT user_field_name FROM metaxwalk", nativeQuery = true)
	List<String> findAllUserFieldName();

	@Query(value = "SELECT user_field_name FROM metaxwalk WHERE meta_type=?1", nativeQuery = true)
	List<String> findAllForEntity(String entityName);

	@Query("SELECT mx FROM MetaXWalk mx WHERE mx.userDefinedFieldName=:userField AND mx.metaType=:metaType")
	MetaXWalk findByUserFieldAndEntity(@Param("userField") String userField, @Param("metaType") String metaType);
	
	@Query("SELECT mx FROM MetaXWalk mx WHERE mx.userDefinedFieldName=:userField")
	MetaXWalk findByUserField(@Param("userField") String userField);

}
