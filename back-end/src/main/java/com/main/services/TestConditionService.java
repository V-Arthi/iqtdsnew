package com.main.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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

import com.main.models.TestCondition;
import com.main.pojo.TestConditionPOJO;
import com.main.repositories.TestConditionRepository;

@RestController
@RequestMapping("/api/")
@CrossOrigin(origins = { "http://localhost:3000" })
public class TestConditionService {

	@Autowired
	private TestConditionRepository testConditionRepository;

	@GetMapping("/conditions")
	public List<TestCondition> getTestConditions() {
		return testConditionRepository.findAll(Sort.by(Sort.Direction.DESC, "testId"));
	}

	@GetMapping("/conditions-lite")
	public List<TestConditionPOJO> getTests() {
		return testConditionRepository.findAll(Sort.by(Sort.Direction.DESC, "testId")).stream()
				.map(t -> new TestConditionPOJO(t.getTestId(), t.getTestName())).collect(Collectors.toList());
	}

	@GetMapping("/condition/{id}")
	public ResponseEntity<TestCondition> getTestCondition(@PathVariable long id) {
		return testConditionRepository.existsById(id)
				? ResponseEntity.accepted().body(testConditionRepository.findById(id).get())
				: ResponseEntity.badRequest().body(null);
	}

	@PostMapping("/condition/add")
	public ResponseEntity<String> addTestCondition(@RequestBody TestCondition testCondition) {
		if (testConditionRepository.existsById(testCondition.getTestId())) {
			return ResponseEntity.badRequest().body("Test Condition with ID :'" + testCondition.getTestId()
					+ "' is already exists, try different ID value");
		}
		testConditionRepository.saveAndFlush(testCondition);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body("Test Condition '" + testCondition.getTestId() + "' is added");
	}

	@PutMapping("/condition/update/{testId}")
	public ResponseEntity<String> updateTestCondition(@RequestBody TestCondition testCondition,
			@PathVariable long testId) {
		if (testConditionRepository.existsById(testId)) {
			testCondition.setTestId(testId);
			testConditionRepository.saveAndFlush(testCondition);
			return ResponseEntity.accepted().body("Test Condition '" + testId + "' is udpated");
		} else {
			return ResponseEntity.badRequest().body("Test Condition '" + testId + "' is not found");
		}

	}

	@DeleteMapping("/condition/delete/{testId}")
	public ResponseEntity<String> deleteTestCondition(@PathVariable long testId) {
		if (testConditionRepository.existsById(testId)) {
			testConditionRepository.deleteById(testId);
			return ResponseEntity.accepted().body("Test Condition with ID :'" + testId + "' is deleted");
		} else {
			return ResponseEntity.badRequest().body("Test Condition with ID :'" + testId + "' is not found");
		}
	}

}
