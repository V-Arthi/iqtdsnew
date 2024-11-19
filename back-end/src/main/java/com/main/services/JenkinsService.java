package com.main.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.models.Execution;
import com.main.models.JenkinsRun;
import com.main.pojo.GenerateOnlineClaimPOJO;
import com.main.repositories.ExecutionRepository;
import com.main.repositories.JenkinsRunRepository;
import com.main.thirdpartyfunctions.Jenkins;

@RestController
@RequestMapping("/api/jenkins")
@CrossOrigin(origins = { "http://localhost:3000" })
public class JenkinsService {

	private static final Logger log = LoggerFactory.getLogger(JenkinsService.class);

	@Autowired
	private ExecutionRepository execRepo;

	@Autowired
	private JenkinsRunRepository jenkinsRunRepo;

	@PostMapping("/create-claim-online/")
	public ResponseEntity<?> createOnlineClaim(@RequestBody GenerateOnlineClaimPOJO data) {
		HashMap<String, String> response = new HashMap<String, String>();
		response.put("run", data.getRunID() + "");
		Jenkins jenkins = new Jenkins();

		try {
			System.out.println(data.toString());
			jenkins.triggerOnlineClaimCreation(data.getTargetEnv().toUpperCase(),data.getClaimID(), data.getClaimFilters(), data.getEnv().toUpperCase())
					.checkQueue();

			if (execRepo.existsById(data.getRunID())) {
				Execution run = execRepo.findById(data.getRunID()).get();
				JenkinsRun jenkinsRun = new JenkinsRun(Long.parseLong(jenkins.getBuildNumber()), jenkins.getBuildURL(),
						data.getClaimID(), data.getTargetEnv());
				List<JenkinsRun> existingRuns = run.getJenkinsRuns();
				List<JenkinsRun> newRuns = new ArrayList<JenkinsRun>();
				newRuns.addAll(existingRuns);
				newRuns.add(jenkinsRun);
				run.setJenkinsRuns(newRuns);
				execRepo.saveAndFlush(run);
				List<JenkinsRun> added = run.getJenkinsRuns();
				added.removeAll(existingRuns);

				return ResponseEntity.accepted().body(added.get(0));

			}

			return ResponseEntity.badRequest().body("Invalid Run ID");
			
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getLocalizedMessage());
		}

	}

	@PostMapping("/job/logs")
	public ResponseEntity<JenkinsRun> getJobLogs(@RequestBody JenkinsRun job) {

		try {

			if (job.getCreatedDataID() == null) {
				HashMap<String, String> jobLog = new Jenkins().getJobLogs(job.getBuildURL());

				job.setLogs(jobLog.get("logs"));
				job.setCreatedDataID(jobLog.get("claimID"));
				jenkinsRunRepo.saveAndFlush(job);
			}
			return ResponseEntity.accepted().body(job);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(null);
		}
	}

	@Scheduled(cron = "* * * * * *")
	public void fillJobIDs() {
		for (JenkinsRun j : jenkinsRunRepo.findAllNonCreated()) {
			try {
				HashMap<String, String> jobLog = new Jenkins().getJobLogs(j.getBuildURL());
				
				if (jobLog.get("StatusCode").equals("404")) {
					j.setCreatedDataID("ERROR:404");
				}else if (!jobLog.get("FinalStatus").equals(StringUtils.EMPTY) && jobLog.get("claimID").equals(StringUtils.EMPTY)) {
					j.setCreatedDataID("ERRORCLAIMNOTFOUND:"+jobLog.get("FinalStatus"));
				}else {
					if (jobLog.get("claimID").equals(StringUtils.EMPTY)) {
						j.setCreatedDataID(null);
					}else {
						j.setCreatedDataID(jobLog.get("claimID"));
					}
					
				}
				j.setLogs(jobLog.get("logs"));
				jenkinsRunRepo.saveAndFlush(j);
				System.out.println(j.getBuildURL());
			} catch (Exception e) {
				e.printStackTrace();
				log.error("error finding claim id for url: " + j.getBuildURL());
			}
		}
	}

}
