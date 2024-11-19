package com.main.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.main.libs.EDIExtraction;
import com.main.models.ClaimResult;
import com.main.models.DataCreationEDIFile;
import com.main.models.Execution;
import com.main.models.HIPAAExtract;
import com.main.models.JobInternal;
import com.main.models.RunExtractOverrideMapping;
import com.main.models.TestcaseExecution;
import com.main.pojo.GenerateEDIPOJO;
import com.main.repositories.ExecutionRepository;
import com.main.repositories.HIPAAExtractRepository;
import com.main.repositories.InternalJobRepository;
import com.main.repositories.TestcaseExecutionRepository;
import com.main.thirdpartyfunctions.FacetsDataSupplier;

@Service
public class TestCaseAsync {

	private static final Logger log = LoggerFactory.getLogger(TestCaseAsync.class);
	
	@Autowired
	private TestcaseExecutionRepository testcaseexecutionRepo;
	@Autowired
	private InternalJobRepository jobRepository;
	@Autowired
	private JobService jobService;
	@Autowired
	private HIPAAExtractRepository hipaaExtractRepo;
	@Autowired
	private ExecutionRepository execRepo;
	@Autowired
	private DataMockupService dataMockupService;

	@Transactional
	@Async
	public void TriggerExecution(TestcaseExecution tc) throws IOException {
		tc.setStatus("Job InProgress");
		testcaseexecutionRepo.saveAndFlush(tc);
		jobService.runJob(Long.parseLong(tc.getJobID())); 
		tc.setStatus("Job Completed");
		testcaseexecutionRepo.saveAndFlush(tc);
		tc.setStatus("EDI Extractions InProgress");
		testcaseexecutionRepo.saveAndFlush(tc);
		String paacc = extractedi(tc);
		tc.setPaaccno(paacc);
		tc.setStatus("EDI Extractions Completed");
		testcaseexecutionRepo.saveAndFlush(tc);
		//TODO
//		validationService.validate(new ClaimValidation());
	}

	private String extractedi(TestcaseExecution tc) throws IOException {
		System.out.println(tc.getJobID());
		ArrayList<String> ediids = new ArrayList<String>();
		
		Optional<JobInternal> jobOpt = jobRepository.findById(Long.parseLong(tc.getJobID()));
		if (jobOpt.isPresent()) {
			JobInternal job = jobOpt.get();
			List<Execution> runs = new ArrayList<>(job.getRuns());
			for (Execution run: runs) {
				/*
				 * get already extracted data to avoid extracting again and preserve old
				 * override data
				 */
				List<RunExtractOverrideMapping> existing = new ArrayList<RunExtractOverrideMapping>();
//				existing.addAll(run.getRunExtMap());
				/*
				 * get list data_ids from passed data
				 */
				List<String> ids = new ArrayList<String>();
				for (ClaimResult claimresult: run.getClaimResults()) {
					System.out.println(claimresult.getData().get("ID"));
					ids.add(claimresult.getData().get("ID"));
				}
				
				/*
				 * get list of id which is already extracted & mapped
				 */
				List<String> alreadyMapped = new ArrayList<String>();
				alreadyMapped.addAll(existing.stream().map(rm -> rm.getExtract().getDataId()).collect(Collectors.toList()));
				
				/*
				 * remove already mapped id's from passed list to avoid extraction process
				 */
				List<String> notMappedIds = new ArrayList<String>();
				notMappedIds.addAll(ids);
				notMappedIds.removeAll(alreadyMapped);

				FacetsDataSupplier facetsDS = new FacetsDataSupplier(tc.getEnv());

				List<HIPAAExtract> extracted = new ArrayList<HIPAAExtract>();
				System.out.println("started");
				
				for (String id : notMappedIds) {

					/*
					 * check if extract for id already exists extract store if exists retrieve it
					 * than extract it again and store to list
					 */

					List<HIPAAExtract> existingData = hipaaExtractRepo.findDataForIDAndEnv(id, tc.getEnv());

					System.out.println("Extraction for :" + id);

					if (existingData.size() > 0) {
						extracted.addAll(existingData);
						System.out.println("Extraction for :" + id + " already exists");
						continue;
					}

					/*
					 * if extract for id na in extract store try extract it and store it to store
					 * for future retrieval
					 */

					facetsDS.init();
					facetsDS.initHG();

					EDIExtraction ext = new EDIExtraction(id, facetsDS);
					String logs = ext.getLogs().stream().map(l -> l.getMessage()).collect(Collectors.joining("|"));
					HIPAAExtract newExtract = new HIPAAExtract(id, tc.getEnv(), "claim", logs, ext.getclaimX12(), ext.getFileName());
					extracted.add(newExtract);

					hipaaExtractRepo.saveAndFlush(newExtract);

					facetsDS.close();
				}
				System.out.println("completed");
				
				/*
				 * mutate the extracted data attach able object to a run object
				 */
				existing.addAll(extracted.stream().map(ext -> new RunExtractOverrideMapping(ext)).collect(Collectors.toList()));

				/*
				 * add the new extracted data map to a run and save it to db
				 */
				run.setRunExtMap(existing);

				execRepo.saveAndFlush(run);
				
				for (RunExtractOverrideMapping runextract: existing) {
					System.out.println("RunExtractOverrideMapping: "+runextract.getId());
					if (runextract.getExtract().getExtractData()!=null) {
						DataCreationEDIFile EDIFile = dataMockupService.generateMockedUpFile(new GenerateEDIPOJO(runextract.getId(), null, null));
						System.out.println("EDIID:"+EDIFile.getFileName());
						File folder = new File("\\\\isilon\\ehqaautomation\\iqtds\\"+runextract.getExtract().getEnv());
						folder.mkdirs();
						File file = new File(folder, EDIFile.getFileName());
						try {
							file.createNewFile();
							FileWriter writer = new FileWriter(file);
							writer.write(EDIFile.getFileContent());
							writer.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						ediids.add(EDIFile.getUniqueIdentifier());
					}
				}
			}
		}
		return StringUtils.join(ediids.stream().distinct().collect(Collectors.toList()), ", ");
	}

//	@Scheduled(cron = "* * * * * *")
//	public void verifyFacetsLoad() {
//		System.out.println("Orchestrate running");
//		List<TestcaseExecution> tclist = testcaseexecutionRepo.findAllFacetsLoadPending();
//		QueryManager qryManager = QueryManager.getInstance();
//		for (TestcaseExecution tc: tclist) {
//			FacetsDataSupplier facetsDS = new FacetsDataSupplier(tc.getEnv());
//			facetsDS.init();
//			for (String paacct: tc.getPaaccno().split(",")) {
//				Recordset rs = facetsDS.getRecordset(qryManager.setQuery("ClaimID", "SELECT CLCL_ID FROM FACETS.CMC_CLCL_CLAIM WHERE CLCL_PA_ACCT_NO = '"+ paacct.trim()+"'"));
//				if (rs!=null) {
//					
//				}
//			}
//			facetsDS.close();
//		}
//	}
}
