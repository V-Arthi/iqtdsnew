package com.main.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.io.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.imsweb.x12.Loop;
import com.imsweb.x12.reader.X12Reader;
import com.imsweb.x12.reader.X12Reader.FileType;
import com.main.libs.EDIExtraction;
import com.main.libs.MockEDI;
import com.main.libs.Util;
import com.main.models.DataCreationEDIFile;
import com.main.models.EDIMappingX12Standard;
import com.main.models.Execution;
import com.main.models.HIPAAExtract;
import com.main.models.Overrides;
import com.main.models.RunExtractOverrideMapping;
import com.main.pojo.ExtractEDIPOJO;
import com.main.pojo.GenerateEDIPOJO;
import com.main.repositories.EDIMappingX12StandardRepository;
import com.main.repositories.ExecutionRepository;
import com.main.repositories.HIPAAExtractRepository;
import com.main.repositories.RunExtractMappingRepository;
import com.main.thirdpartyfunctions.FacetsDataSupplier;

@RestController
@RequestMapping("/api/data/")
@CrossOrigin(origins = { "http://localhost:3000" })
public class DataMockupService {
	@Autowired
	private EDIMappingX12StandardRepository mapRepo;
	
//	@Autowired
//	private EDIMappingRepository mapRepo;

	@Autowired
	private ExecutionRepository execRepo;

	@Autowired
	private HIPAAExtractRepository hipaaExtractRepo;

	@Autowired
	private RunExtractMappingRepository runExtMapRepo;

	
	@PostMapping("/create-edi")
	public DataCreationEDIFile generateMockedUpFile(@RequestBody GenerateEDIPOJO data) throws IOException {
		
		if (!runExtMapRepo.existsById(data.getRunExtractMapID()))
			return null;

		RunExtractOverrideMapping extMap = runExtMapRepo.findById(data.getRunExtractMapID()).get();
		System.out.println(extMap.getExtract().getId());
		String env = extMap.getExtract().getEnv();

		List<EDIMappingX12Standard> memberMappings = new ArrayList<EDIMappingX12Standard>();
		List<EDIMappingX12Standard> providerMappings = new ArrayList<EDIMappingX12Standard>();

		memberMappings.addAll(mapRepo.findAll().stream()
				.filter(emap -> StringUtils.startsWith(emap.getFieldName(), "Member")).collect(Collectors.toList()));
		providerMappings.addAll(mapRepo.findAll().stream()
				.filter(emap -> StringUtils.startsWith(emap.getFieldName(), "Provider")).collect(Collectors.toList()));

		String patAccNum = "iQTDS" + Util.getDateTimeInFormat("MMddyyyyhhmmss");

		MockEDI m = new MockEDI(extMap.getExtract().getExtractData());
		m.setOverrides(extMap.getOverrides());
		String filename=extMap.getExtract().getFileName();
		X12Reader reader = null;
		Loop loop = null;
		InputStream in=new ByteArrayInputStream(extMap.getExtract().getExtractData().getBytes());
		try {
			if(filename.endsWith("P"))
			reader= new X12Reader(FileType.ANSI837_5010_X222,in);
			else if(filename.endsWith("I"))
			reader=new X12Reader(FileType.ANSI837_5010_X223,in);
			else if(filename.endsWith("D"))
			reader=new X12Reader(FileType.ANSI837_5010_X224,in);
			else
				System.out.println("File type not in I,P,D");
				
			loop =reader.getLoop();
		}
		catch(Exception e) {
			
		}
		if (reader.getErrors().size() == 0) {
			System.out.println("Done reading the X12");
		}else {
			for (String error : reader.getErrors()) {
				System.out.println(error);
			}
		}
		
	if (data.getMemberID() != null)
		m.doMemberUpdate(data.getMemberID(), env, memberMappings,loop);

	if (data.getProviderID() != null)
		m.doProviderUpdate(data.getProviderID(), env, providerMappings,loop);

		m.doPatientAcctNumChange(patAccNum, mapRepo.getOne("Patient Control Number"),loop)
				.doRecievedDateUpdate(Util.getDateTimeInFormat("yyyyMMdd"), mapRepo.getOne("Transaction Set Creation Date"),loop);
		
		try {
			m.runOverrides(loop);
			
		}catch(Exception e) {
			
		}

		

		DataCreationEDIFile d = new DataCreationEDIFile();
		System.out.println(m.getEdiX12data());
		d.setFileContent(m.getEdiX12data());
		//d.setFileContent(m.getEdiData());
		d.setUniqueIdentifier(patAccNum);
		d.setMockupLogs(m.getLogs());
		
		try {
			String overrides = extMap.getOverrides().stream()
					.map(o -> String.format("%s|%s|%s", o.getEdiMap().getFieldName(), o.getTxnNumber(), o.getValue()))
					.collect(Collectors.joining(","));
			d.setOverrides(overrides);
		}catch(NullPointerException e) {
			//TODO - Its of with no overrides
		}
		
		d.setFileName(m.getFileName());
		d.setMemberID(data.getMemberID());
		d.setProviderID(data.getProviderID());

		List<DataCreationEDIFile> history = new ArrayList<DataCreationEDIFile>();
//		history.addAll(extMap.getEdiFiles());
		history.add(d);
		extMap.setEdiFiles(history);
		runExtMapRepo.saveAndFlush(extMap);
		
		
		return d;
	}
	
	@PostMapping("/extract-edi")
	public List<RunExtractOverrideMapping> getEDIExtract(@RequestBody List<ExtractEDIPOJO> data) {

		System.out.println(data.toString());

		if (data.size() < 1)
			return null;
		String env = data.get(0).getEnv();

		/*
		 * find attached run from passed runID
		 */
		Execution run = execRepo.findById(data.get(0).getRunId()).get();

		/*
		 * get already extracted data to avoid extracting again and preserve old
		 * override data
		 */
		List<RunExtractOverrideMapping> existing = new ArrayList<RunExtractOverrideMapping>();
		existing.addAll(run.getRunExtMap());

		/*
		 * get list data_ids from passed data
		 */
		List<String> ids = data.stream().map(d -> d.getId()).collect(Collectors.toList());

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

		FacetsDataSupplier facetsDS = new FacetsDataSupplier(env);

		List<HIPAAExtract> extracted = new ArrayList<HIPAAExtract>();
		System.out.println("started");

		for (String id : notMappedIds) {

			/*
			 * check if extract for id already exists extract store if exists retrieve it
			 * than extract it again and store to list
			 */

			List<HIPAAExtract> existingData = hipaaExtractRepo.findDataForIDAndEnv(id, env);

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
			HIPAAExtract newExtract = new HIPAAExtract(id, env, "claim", logs, ext.getclaimX12(), ext.getFileName());
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

		/*
		 * return list of extract for the passed ids
		 */
		return run.getRunExtMap().stream().filter(rm -> ids.contains(rm.getExtract().getDataId()))
				.collect(Collectors.toList());

	}

	@PostMapping("/run-extract/{run_extract_id}/attach-overrides")
	public ResponseEntity<String> attachOverridesToExtract(@PathVariable long run_extract_id,
			@RequestBody List<Overrides> overrides) {
		if (runExtMapRepo.existsById(run_extract_id)) {
			RunExtractOverrideMapping re = runExtMapRepo.findById(run_extract_id).get();
			re.setOverrides(overrides);
			runExtMapRepo.saveAndFlush(re);
			return ResponseEntity.accepted().body("overrides are attached");
		}
		return ResponseEntity.badRequest().body("record with run extract '" + run_extract_id + "' is not found");
	}

//	@PostMapping("/create-edi")
//	public DataCreationEDIFile generateMockedUpFile(@RequestBody GenerateEDIPOJO data) {
//
//		if (!runExtMapRepo.existsById(data.getRunExtractMapID()))
//			return null;
//
//		RunExtractOverrideMapping extMap = runExtMapRepo.findById(data.getRunExtractMapID()).get();
//		System.out.println(extMap.getExtract().getId());
//		
//		String env = extMap.getExtract().getEnv();
//
//		List<EDIMapping> memberMappings = new ArrayList<EDIMapping>();
//		List<EDIMapping> providerMappings = new ArrayList<EDIMapping>();
//
//		memberMappings.addAll(mapRepo.findAll().stream()
//				.filter(emap -> StringUtils.startsWith(emap.getFieldName(), "Member")).collect(Collectors.toList()));
//		providerMappings.addAll(mapRepo.findAll().stream()
//				.filter(emap -> StringUtils.startsWith(emap.getFieldName(), "Provider")).collect(Collectors.toList()));
//
//		String patAccNum = "iQTDS" + Util.getDateTimeInFormat("MMddyyyyhhmmss");
//
//		MockEDI m = new MockEDI(extMap.getExtract().getExtractData());
//		m.setOverrides(extMap.getOverrides());
//
//		if (data.getMemberID() != null)
//			m.doMemberUpdate(data.getMemberID(), env, memberMappings);
//
//		if (data.getProviderID() != null)
//			m.doProviderUpdate(data.getProviderID(), env, providerMappings);
//
//		m.doPatientAcctNumChange(patAccNum, mapRepo.getOne("Patient Account Number"))
//				.doRecievedDateUpdate(Util.getDateTimeInFormat("yyyyMMdd"), mapRepo.getOne("Received Date"));
//		
//		try {
//			m.runOverrides();
//		}catch(NullPointerException e) {
//			//TODO - Its of with no overrides
//		}
//
//		
//
//		DataCreationEDIFile d = new DataCreationEDIFile();
//
//		d.setFileContent(m.getEdiData());
//		d.setUniqueIdentifier(patAccNum);
//		d.setMockupLogs(m.getLogs());
//		
//		try {
//			String overrides = extMap.getOverrides().stream()
//					.map(o -> String.format("%s|%s|%s", o.getEdiMap().getFieldName(), o.getTxnNumber(), o.getValue()))
//					.collect(Collectors.joining(","));
//			d.setOverrides(overrides);
//		}catch(NullPointerException e) {
//			//TODO - Its of with no overrides
//		}
//		
//		d.setFileName(m.getFileName());
//		d.setMemberID(data.getMemberID());
//		d.setProviderID(data.getProviderID());
//
//		List<DataCreationEDIFile> history = new ArrayList<DataCreationEDIFile>();
////		history.addAll(extMap.getEdiFiles());
//		history.add(d);
//		extMap.setEdiFiles(history);
//		runExtMapRepo.saveAndFlush(extMap);
//
//		return d;
//	}

}
