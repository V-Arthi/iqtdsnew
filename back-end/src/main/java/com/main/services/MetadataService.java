package com.main.services;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.models.ClaimAccuracyConditions;
import com.main.models.EDIMapping;
import com.main.models.EDIMappingX12Standard;
import com.main.repositories.ClaimAccuracyConditionsRepository;
import com.main.repositories.EDIMappingRepository;
import com.main.repositories.EDIMappingX12StandardRepository;
import com.main.repositories.LoBRepository;
import com.main.repositories.SubLoBRepository;

@RestController
@RequestMapping("/api/config")
@CrossOrigin(origins = { "http://localhost:3000" })
public class MetadataService {
//	@Autowired
//	private EDIMappingRepository ediMappingRepo;

	@Autowired
	private EDIMappingX12StandardRepository ediMappingRepo;
	
	@Autowired
	private LoBRepository lobRepo;

	@Autowired
	private SubLoBRepository sublob_Repo;
	
	@Autowired
	private ClaimAccuracyConditionsRepository claimAccuracyRepo;

	@GetMapping("/sublob_list")
	public List<String> getAllSubLoB() {
		return sublob_Repo.findAll().stream().map(l -> l.getSubLoB()).collect(Collectors.toList());
	}

	@GetMapping("/lob_list")
	private List<String> getAllLoB() {
		return lobRepo.findAll().stream().map(l -> l.getLob()).collect(Collectors.toList());
	}

//	@GetMapping("/edi-mapping/all")
//	public List<EDIMapping> getAllMapping() {
//		return ediMappingRepo.findAll();
//	}
	@GetMapping("/edi-mapping/all")
	public List<EDIMappingX12Standard> getAllMapping() {
		return ediMappingRepo.findAll();
	}
	
//	@GetMapping("/edi-mapping/mockup-eligible")
//	public List<EDIMapping> getMockupEligibleMappings() {
//		return ediMappingRepo.findAllMockUpEligible();
//	}
	@GetMapping("/edi-mapping/mockup-eligible/{claimType}")
	public List<EDIMappingX12Standard> getMockupEligibleMappings(@PathVariable String claimType) {
		List<EDIMappingX12Standard> columns = ediMappingRepo.findAllMockUpEligibleByClaimType("claim");
		columns.addAll(ediMappingRepo.findAllMockUpEligibleByClaimType(claimType));
		return columns;
	}
	@GetMapping("/edi-mapping/mockup-eligible")
	public List<EDIMappingX12Standard> getMockupEligibleMappings() {
		return ediMappingRepo.findAllMockUpEligible();
	}
	public EDIMappingX12Standard getMappingForField(String fieldName) {
		if (ediMappingRepo.existsById(fieldName)) {
			return ediMappingRepo.getById(fieldName);
		}
		return null;
	}

	@PutMapping("/edi-mapping/update/{fieldName}")
	public ResponseEntity<String> updateMapping(@PathVariable String fieldName, @RequestBody EDIMappingX12Standard ediMapping) {
		if (ediMappingRepo.existsById(fieldName)) {
			ediMappingRepo.save(ediMapping);
			return ResponseEntity.accepted().body(String.format("field '%s' updated", fieldName));
		}

		return ResponseEntity.badRequest()
				.body(String.format("field '%s' not found, please add as new mapping", fieldName));
	}

	@DeleteMapping("/edi-mapping/delete/{fieldName}")
	public ResponseEntity<String> removeMapping(@PathVariable String fieldName) {
		if (ediMappingRepo.existsById(fieldName)) {
			ediMappingRepo.deleteById(fieldName);
			return ResponseEntity.accepted().body(String.format("field '%s' is removed", fieldName));
		}

		return ResponseEntity.badRequest()
				.body(String.format("field '%s' is not exists; so could not be removed", fieldName));
	}

	@PostMapping("/edi-mapping/add")
	public ResponseEntity<String> addMapping(@RequestBody EDIMappingX12Standard ediMapping) {
		if (ediMappingRepo.existsById(ediMapping.getFieldName())) {
			return ResponseEntity.badRequest()
					.body(String.format("Mapping already exists for field '%s'", ediMapping.getFieldName()));
		}

		ediMappingRepo.save(ediMapping);
		return ResponseEntity.accepted().body(String.format("added '%s'", ediMapping.toString()));

	}

	@GetMapping("/edi-mapping/all/provider")
	public List<EDIMappingX12Standard> getAllProviderMappingFields() {
		return ediMappingRepo.findAll().stream().filter(emap -> StringUtils.startsWith(emap.getFieldName(), "Provider"))
				.collect(Collectors.toList());
	}

	@GetMapping("/edi-mapping/all/member")
	public List<EDIMappingX12Standard> getAllMemberMappingFields() {
		return ediMappingRepo.findAll().stream().filter(emap -> StringUtils.startsWith(emap.getFieldName(), "Member"))
				.collect(Collectors.toList());
	}

	@GetMapping("/edi-mapping/{fieldName}")
	public ResponseEntity<EDIMappingX12Standard> getmapping(@PathVariable String fieldName) {
		if (ediMappingRepo.existsById(fieldName)) {
			return ResponseEntity.accepted().body(ediMappingRepo.findById(fieldName).get());
		}
		return ResponseEntity.badRequest().body(null);
	}
	
	@GetMapping("/claimaccuracycondition/all")
	public List<ClaimAccuracyConditions> getAllClaimAccuracyCondition() {
		return claimAccuracyRepo.findAll();
	}
	
	@PostMapping("/claimaccuracycondition/add")
	public ResponseEntity<String> addClaimAccuracyCondition(@RequestBody ClaimAccuracyConditions claimAccuracyConditions) {
		if (claimAccuracyRepo.existsByCondition(claimAccuracyConditions.getCondition())) {
			return ResponseEntity.badRequest()
					.body(String.format("Claim Accuracy Condition already exists for condition '%s'", claimAccuracyConditions.getCondition()));
		}

		claimAccuracyRepo.save(claimAccuracyConditions);
		return ResponseEntity.accepted().body(String.format("added '%s'", claimAccuracyConditions.toString()));
	}
	
	@PutMapping("/claimaccuracycondition/update/{condition}")
	public ResponseEntity<String> updateClaimAccuracyCondition(@PathVariable String condition, @RequestBody ClaimAccuracyConditions claimAccuracyConditions) {
		if (claimAccuracyRepo.existsByCondition(condition)) {
			claimAccuracyRepo.save(claimAccuracyConditions);
			return ResponseEntity.accepted().body(String.format("condition '%s' updated", condition));
		}

		return ResponseEntity.badRequest()
				.body(String.format("condition '%s' not found, please add as new condition", condition));
	}
	
	@DeleteMapping("/claimaccuracycondition/delete/{condition}")
	public ResponseEntity<String> removeClaimAccuracyCondition(@PathVariable String condition) {
		
		return ResponseEntity.badRequest()
				.body(String.format("Deleting condition '%s' is not allowed", condition));
	}
}
