
package com.main.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.main.models.EDIMapping;
import com.main.models.EDIMappingX12Standard;

public interface EDIMappingX12StandardRepository extends JpaRepository<EDIMappingX12Standard, String> {

	@Query("SELECT m FROM EDIMappingX12Standard m WHERE m.valueOverridable=1")
	public List<EDIMappingX12Standard> findAllMockUpEligible();

	@Query("SELECT m FROM EDIMappingX12Standard m WHERE m.valueOverridable=1 AND m.claimType=?1 ")
	public List<EDIMappingX12Standard> findAllMockUpEligibleByClaimType(String claimType);
	
}
