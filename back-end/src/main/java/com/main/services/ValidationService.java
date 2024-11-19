package com.main.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.models.ClaimValidation;
import com.main.models.TestCondition;
import com.main.repositories.ClaimValidationRepository;
import com.main.repositories.TestConditionRepository;

@RestController
@RequestMapping("/api/validation")
@CrossOrigin(origins = { "http://localhost:3000" })
public class ValidationService {

	@Autowired
	private AllAsync allAsync;

	@Autowired
	private TestConditionRepository testRepo;

	@Autowired
	private ClaimValidationRepository claimValidationRepo;

	@GetMapping("/claims")
	public List<ClaimValidation> getClaimValidationsQueue() {
		return claimValidationRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}

	@PostMapping("/claims")
	public String validate(@RequestBody ClaimValidation data) {
		TestCondition test = testRepo.findById(data.getTestId())
				.orElseThrow(() -> new IllegalStateException("Test with id:'" + data.getTestId() + "' doesnot exists"));
		data.setStatus("Submitted");
		ClaimValidation claim = claimValidationRepo.saveAndFlush(data);
		
		allAsync.claimValidation(claim);

		return String.format(
				"Claim Validation Request '%d' is submitted, You will be able to see the results when status turns to completed!",
				claim.getId());

	}

	@GetMapping("/claims/{id}")
	public ClaimValidation getValidationResult(@PathVariable long id) {

		return claimValidationRepo.findById(id).orElseThrow(
				() -> new IllegalStateException("Claim Validation Result for id '" + id + "' doesnot exists"));
	}

}
