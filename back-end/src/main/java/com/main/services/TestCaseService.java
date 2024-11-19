package com.main.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.models.Execution;
import com.main.models.JobInternal;
import com.main.models.Testcase;
import com.main.models.TestcaseExecution;
import com.main.pojo.TestcaseExecutionPojo;
import com.main.repositories.InternalJobRepository;
import com.main.repositories.TestConditionRepository;
import com.main.repositories.TestcaseExecutionRepository;
import com.main.repositories.TestcaseRepository;

@RestController
@RequestMapping("/api/testcase")
@CrossOrigin(origins = { "http://localhost:3000" })
public class TestCaseService {
	
	@Autowired
	private TestcaseRepository testcaseRepo;
	@Autowired
	private TestcaseExecutionRepository testcaseexecutionRepo;
	@Autowired
	private TestCaseAsync allAsync;
	@Autowired
	private TestConditionRepository testConditionRepository;
	@Autowired
	private InternalJobRepository jobRepository;

	@GetMapping("/all")
	public List<Testcase> getTestcases() {
		return testcaseRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}

	@PostMapping("/add")
	public String add(@RequestBody Testcase data) {
		Testcase testcase = testcaseRepo.saveAndFlush(data);
		return String.format(
				"Testcase '%d' is submitted!",
				testcase.getid());
	}
	
	@PostMapping("/execute")
	public String execute(@RequestBody TestcaseExecutionPojo data) throws IOException {
		ArrayList<String> tcid = new ArrayList<String>(); 
		for (Long test: data.getTests()) {
			TestcaseExecution tc = testcaseexecutionRepo.saveAndFlush(new TestcaseExecution(String.valueOf(test), data.getEnv(), "NotStarted", data.getUser()));
			tcid.add(String.valueOf(tc.getId()));
			List<Execution> executions = new ArrayList<Execution>();
			Testcase tcf = testcaseRepo.findById(Long.parseLong(tc.getTests().trim())).get();
			executions.add(new Execution(testConditionRepository.findById(tcf.getidentificationid()).get(), tc.getEnv(), 10));
			JobInternal job = jobRepository.saveAndFlush(new JobInternal(executions));
			System.out.println("JobID: "+job.getId());
			tc.setJobID(String.valueOf(job.getId()));
			tc.setStatus("Job Added");
			testcaseexecutionRepo.saveAndFlush(tc);
			allAsync.TriggerExecution(tc);
		}
		return String.format(
				"Testcase '%s' is submitted!",
				StringUtils.join(tcid, ", "));
	}
	
	@GetMapping("/executetest")
	public String Test() throws IOException {
		Optional<TestcaseExecution> tc = testcaseexecutionRepo.findById(Long.parseLong("1"));
		allAsync.TriggerExecution(tc.get());
		return "OK";
	}
	
	@GetMapping("/allexecution")
	public List<TestcaseExecution> getTestcasesExecution() {
		return testcaseexecutionRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}
}
