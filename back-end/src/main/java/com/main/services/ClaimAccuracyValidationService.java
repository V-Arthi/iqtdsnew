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

import com.main.models.ClaimAccuracy;
import com.main.repositories.ClaimAccuracyRepository;

@RestController
@RequestMapping("/api/claimaccuracyvalidator")
@CrossOrigin(origins = { "http://localhost:3000" })
public class ClaimAccuracyValidationService {

	@Autowired
	private ClaimAccuracyAsync allAsync;

	@Autowired
	private ClaimAccuracyRepository claimAccuracyRepo;

	@GetMapping("/claims")
	public List<ClaimAccuracy> getClaimValidationsQueue() {
		return claimAccuracyRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}

	@PostMapping("/claims")
	public String validate(@RequestBody ClaimAccuracy data) {
		
		data.setStatus("Submitted");
		ClaimAccuracy claim = claimAccuracyRepo.saveAndFlush(data);

		allAsync.ClaimAccuracy(claim);

		return String.format(
				"Claim Validation Request '%d' is submitted, You will be able to see the results when status turns to completed!",
				claim.getId());

	}

	@GetMapping("/claims/{id}")
	public ClaimAccuracy getValidationResult(@PathVariable long id) {

		return claimAccuracyRepo.findById(id).orElseThrow(
				() -> new IllegalStateException("Claim Validation Result for id '" + id + "' doesnot exists"));
	}

}
