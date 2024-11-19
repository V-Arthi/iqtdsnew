package com.main.services;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.cognizant.dbutils.DatabaseManager;
import com.main.cognizant.dbutils.QueryManager;
import com.main.cognizant.dbutils.QueryManager.Query;
import com.main.db.utils.Recordset;
import com.main.models.ClaimFilter;
import com.main.models.ClaimResult;
import com.main.models.Execution;
import com.main.models.JobInternal;
import com.main.models.MemberFilter;
import com.main.models.MemberResult;
import com.main.models.ProviderFilter;
import com.main.models.ProviderResult;
import com.main.models.TestCondition;
import com.main.pojo.ExecutionRequest;
import com.main.pojo.JobLitePOJO;
import com.main.pojo.JobPOJO;
import com.main.repositories.ExecutionRepository;
import com.main.repositories.InternalJobRepository;
import com.main.repositories.MetaXWalkRepository;
import com.main.repositories.TestConditionRepository;
import com.main.thirdpartyfunctions.Claims;
import com.main.thirdpartyfunctions.FacetsDataSupplier;
import com.main.thirdpartyfunctions.Members;
import com.main.thirdpartyfunctions.Providers;

@RestController
@RequestMapping("/api/monitor/")
@CrossOrigin(origins = { "http://localhost:3000" })
public class JobService {

	private static final Logger log = LoggerFactory.getLogger(JobService.class);

	DatabaseManager dbManage = DatabaseManager.getInstance();
	QueryManager querymanager = QueryManager.getInstance();

	@Autowired
	private InternalJobRepository jobRepository;

	@Autowired
	private ExecutionRepository execRepo;

	@Autowired
	private MetaXWalkRepository xwalkRepo;

	@Autowired
	private TestConditionRepository testConditionRepository;

	@GetMapping("/jobs-full-data")
	public List<JobInternal> getJobs() {
		return jobRepository.findAll();
	}

	@GetMapping("/jobs")
	public List<JobPOJO> getJobsShort() {
		return jobRepository.findAll().stream().map(j -> new JobPOJO(j.getId(), j.getStatus(), j.getCreatedOn()))
				.collect(Collectors.toList());

	}

	@PostMapping("/jobs/add")
	public ResponseEntity<?> addJob(@RequestBody List<ExecutionRequest> runs) {

		List<Execution> executions = runs.stream()
				.map(r -> new Execution(testConditionRepository.findById(r.getTestId()).isPresent()
						? testConditionRepository.findById(r.getTestId()).get()
						: null, r.getEnv(), r.getRecordLength()))
				.filter(e -> e.getTestCondition() != null).collect(Collectors.toList());

		JobInternal job = jobRepository.save(new JobInternal(executions));

		return ResponseEntity.accepted().body(job.getId());
	}

	@GetMapping("/jobs/{id}")
	public JobInternal getJob(@PathVariable long id) {
		return jobRepository.findById(id).isPresent() ? jobRepository.findById(id).get() : null;
	}

	@DeleteMapping("/jobs/remove/{id}")
	public ResponseEntity<?> removeJob(@PathVariable long id) {
		Optional<JobInternal> job = jobRepository.findById(id);

		if (!job.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job with Id " + id + " doesnot exists");
		}

		jobRepository.deleteById(id);

		return ResponseEntity.accepted().body("Job with id :" + id + " is removed");
	}

	@PostMapping("/jobs/run/{id}")
	public void runJob(@PathVariable long id) {

		Optional<JobInternal> jobOpt = jobRepository.findById(id);

		if (!jobOpt.isPresent())
			return;

		JobInternal job = jobOpt.get();

		job.setStatus("In Progress");
		job.getRuns().forEach(e -> e.setStatus("Not Started"));
		jobRepository.saveAndFlush(job);

		// System.out.println("************************" + job.getId() + " is
		// running************************");

		log.info(StringUtils.center("JOB :" + job.getId() + " STARTED", 100, "#"));
		String catchedError = "";

		for (int i = 0; i < job.getRuns().size(); i++) {
			Execution exec = job.getRuns().get(i);
			FacetsDataSupplier facetsDS = null;
			try {

				facetsDS = new FacetsDataSupplier(exec.getEnv());
				facetsDS.init();
				facetsDS.initHG();

				exec.setStatus("In Progress");
				execRepo.saveAndFlush(exec);

				String subIds = null;

				QueryClaimResult(exec, facetsDS);

				exec.setStatus("Claims Done");
				execRepo.saveAndFlush(exec);

				/*
				 * Even if user did not specify any member filters query will run to find the
				 * active member on sysdate
				 */
				subIds = QueryMemberResult(exec, facetsDS);
				exec.setStatus("Members Done");
				execRepo.saveAndFlush(exec);

				/*
				 * Even if user did not specify any member filters query will run to find the
				 * active provider on sysdate
				 */
				queryProviderResult(exec, subIds, facetsDS);
				exec.setStatus("Providers Done");
				execRepo.saveAndFlush(exec);

				exec.setStatus("Completed");

			} catch (Exception e) {
				String message = facetsDS.getDbExceptionMessage();
				if(message == null) {
					message = e.getMessage();
				}
				catchedError += "\n" + message;
				e.printStackTrace();
				System.out.println("run error message for " + exec.getId() + " is completed");
				exec.setStatus("Failed");
				execRepo.saveAndFlush(exec);
			}
			finally {
				if (facetsDS != null)
					facetsDS.close();	
			}
			 			
		}

		String jobStatus = job.getRuns().stream().anyMatch(r -> r.getStatus().equalsIgnoreCase("failed"))
				? ("Completed With Errors: " + catchedError)
				: "Completed";
		if(jobStatus.length() > 255) {
			jobStatus = jobStatus.substring(0, 254);
		}
		job.setStatus(jobStatus);
		jobRepository.saveAndFlush(job);

		log.info(StringUtils.center("JOB :" + job.getId() + " COMPLETED", 100, "#"));

	}

	private String getFilterFieldFromXWalk(String userFieldName) {
		return xwalkRepo.existsById(userFieldName) ? xwalkRepo.findById(userFieldName).get().getDbFieldName() : userFieldName;
	}

	private String getClaimQuery(TestCondition test, int recordLength) {

		List<ClaimFilter> claimFilters = test.getClaimFilters().stream()
				.map(f -> new ClaimFilter(getFilterFieldFromXWalk(f.getField()), f.getOperator(), f.getValue()))
				.collect(Collectors.toList());

		String claimsQry = StringUtils.EMPTY;
				
		if (test.getClaimType().equals("Dental")) {
			claimsQry =new Claims(test.getClaimType()).attachDentalWhereConditions(claimFilters, recordLength,
					test.getClaimInputType().equalsIgnoreCase("edi"));
		}else if (test.getClaimType().equals("Hospital")) {
			claimsQry =new Claims(test.getClaimType()).attachWhereConditions(claimFilters, recordLength,
					test.getClaimInputType().equalsIgnoreCase("edi"));
		}else {
			claimsQry =new Claims().attachWhereConditions(claimFilters, recordLength,
					test.getClaimInputType().equalsIgnoreCase("edi"));
		}
		
		System.out.println("Claims Query");
		System.out.println(claimsQry);
		return claimsQry;
	}

	private String getMemberQuery(TestCondition test, int recordLength) {
		List<MemberFilter> memberFilters = test.getMemberFilters().stream()
				.map(f -> new MemberFilter(getFilterFieldFromXWalk(f.getField()), f.getOperator(), f.getValue()))
				.collect(Collectors.toList());

		String memberQry = new Members().attachWhereCondition(memberFilters, recordLength);
		System.out.println("Member Query");
		System.out.println(memberQry);
		return memberQry;
	}

	private void QueryClaimResult(Execution exec, FacetsDataSupplier facetsDS) throws IOException {

		TestCondition test = exec.getTestCondition();

		String claimsQry = getClaimQuery(test, exec.getRecordLength());

		Query clQry = querymanager.setQuery(test.getTestId() + "_claims", claimsQry);
		Recordset claimrecords = facetsDS.getRecordset(clQry);

		if (claimrecords != null)
			exec.setClaimResults(
					claimrecords.getAsString().stream().map(e -> new ClaimResult(e)).collect(Collectors.toList()));
	}

	private String QueryMemberResult(Execution exec, FacetsDataSupplier facetsDS) {

		TestCondition test = exec.getTestCondition();

		String memberQry = getMemberQuery(test, exec.getRecordLength());

		Query memQry = querymanager.setQuery(test.getTestId() + "_members", memberQry);
		Recordset memberRecords = facetsDS.getRecordset(memQry);

		if (memberRecords != null) {
			exec.setMemberResults(
					memberRecords.getAsString().stream().map(e -> new MemberResult(e)).collect(Collectors.toList()));

			String subIds = memberRecords.getUniqueValuesAsList("Subscriber ID").stream()
					.collect(Collectors.joining(","));
			return subIds.replaceAll(",", "','");
		}

		return null;

	}

	private void queryProviderResult(Execution exec, String subIds, FacetsDataSupplier facetsDS) {
		TestCondition test = exec.getTestCondition();

		if (test.getProviderType().equalsIgnoreCase("in") && subIds == null)
			return;

		List<ProviderFilter> providerFilters = test.getProviderFilters().stream()
				.map(f -> new ProviderFilter(getFilterFieldFromXWalk(f.getField()), f.getOperator(), f.getValue()))
				.collect(Collectors.toList());

		String providerQry = new Providers().attachWhereCondition(providerFilters, exec.getRecordLength(),
				test.getProviderType());
		Query provQry = querymanager.setQuery(test.getTestId() + "_provider", providerQry);
		System.out.println("Provider Query");
		System.out.println(providerQry);
		
		if (subIds != null)
			provQry.bind("Subscriber ID", subIds);

		Recordset providerRecords = facetsDS.getRecordset(provQry);
		if (providerRecords != null)
			exec.setProviderResults(providerRecords.getAsString().stream().map(e -> new ProviderResult(e))
					.collect(Collectors.toList()));

	}
	
	
	@GetMapping("/jobs-lite")
	public List<JobLitePOJO> getJobsLite() {
		return jobRepository.findAll().stream().map(j -> new JobLitePOJO(j.getId(), j.getStatus(), j.getCreatedOn(), 
				j.getRuns().stream()
				.map(r -> r.getEnv())
				.collect(Collectors.toList())
				,
				j.getRuns().stream()
				.filter(r-> r.getTestCondition()!=null)
				.map(r -> String.valueOf(r.getTestCondition().getTestId()))
				.collect(Collectors.toList())
				))
		.collect(Collectors.toList());
	}
}
