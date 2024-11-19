package com.main.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.models.Execution;
import com.main.repositories.ExecutionRepository;

@RestController
@RequestMapping("/api/monitor/")
@CrossOrigin(origins = { "http://localhost:3000" })
public class ExecutionService {
	@Autowired
	private ExecutionRepository execRepo;

	@GetMapping("/runs")
	public List<Execution> getAllRuns() {
		return execRepo.findAll();
	}

	@GetMapping("/runs/{id}")
	public Optional<Execution> getRun(@PathVariable long id) {
		return execRepo.findById(id);
	}

}
