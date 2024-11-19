package com.main.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.cognizant.dbutils.DatabaseManager;
import com.main.cognizant.dbutils.QueryManager;
import com.main.repositories.MetaXWalkRepository;

@RestController
@RequestMapping("/api/facets/")
@CrossOrigin(origins = { "http://localhost:3000" })
public class FacetsDBService {

	@Autowired
	private MetaXWalkRepository xwalkRepo;

	DatabaseManager dbManage = DatabaseManager.getInstance();
	QueryManager querymanager = QueryManager.getInstance();
	
	@GetMapping("/all")
	public List<String> getAllColumns(){
		return xwalkRepo.findAllUserFieldName();
	}
	
	@GetMapping("/claims/meta")
	public List<String> getClaimsColumns() {
		return xwalkRepo.findAllForEntity("claim");
	}

	@GetMapping("/members/meta")
	public List<String> getMembersColumns() {
		return xwalkRepo.findAllForEntity("member");
	}

	@GetMapping("/providers/meta")
	public List<String> getProviderColumns() {
		return xwalkRepo.findAllForEntity("provider");
	}

	@GetMapping("/Hospital/meta")
	public List<String> getHospitalClaimsColumns() {
		List<String> columns = xwalkRepo.findAllForEntity("claim");
		columns.addAll(xwalkRepo.findAllForEntity("hospitalclaim"));
		return columns;
	}
	
	@GetMapping("/Medical/meta")
	public List<String> getMedicalClaimsColumns() {
		List<String> columns = xwalkRepo.findAllForEntity("claim");
		columns.addAll(xwalkRepo.findAllForEntity("medicalclaim"));
		return columns;
	}
	
	@GetMapping("/Dental/meta")
	public List<String> getDentalClaimsColumns() {
		List<String> columns = xwalkRepo.findAllForEntity("claim");
		columns.addAll(xwalkRepo.findAllForEntity("dentalclaim"));
		return columns;
	}

}
