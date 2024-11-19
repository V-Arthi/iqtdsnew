package com.main.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.pojo.DashboardPOJO;
import com.main.repositories.DataCreationEDIRepository;
import com.main.repositories.EDIMappingX12StandardRepository;
import com.main.repositories.ExecutionRepository;
import com.main.repositories.HIPAAExtractRepository;
import com.main.repositories.InternalJobRepository;
import com.main.repositories.JenkinsRunRepository;
import com.main.repositories.TestConditionRepository;

@RestController
@RequestMapping("/api/dashboard/")
@CrossOrigin(origins = { "http://localhost:3000" })
public class DashboardService {

	@Autowired
	private TestConditionRepository testRepo;

	@Autowired
	private ExecutionRepository execRepo;

	@Autowired
	private InternalJobRepository jobRepo;

	@Autowired
	private DataCreationEDIRepository ediFileRepo;

	@Autowired
	private JenkinsRunRepository onlineRepo;

//	@Autowired
//	private EDIMappingRepository mappingRepo;
	
	@Autowired
	private EDIMappingX12StandardRepository mappingRepo;
	
	@Autowired
	private HIPAAExtractRepository extractRepo;

	@GetMapping("/snapshot")
	public ResponseEntity<DashboardPOJO> getDashboardSnapshot() {
		try {
			DashboardPOJO dash = new DashboardPOJO();

			dash.setConditions(testRepo.count());
			dash.setRuns(execRepo.count());
			dash.setJobs(jobRepo.count());
			dash.setEdiFiles(ediFileRepo.count());
			dash.setOnlineClaims(onlineRepo.createdCount());
			dash.setMappings(mappingRepo.count());
			dash.setExtracts(extractRepo.count());

			return ResponseEntity.accepted().body(dash);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(null);
		}

	}

}
