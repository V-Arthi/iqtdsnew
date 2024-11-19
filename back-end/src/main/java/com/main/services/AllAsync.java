package com.main.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.main.cognizant.dbutils.QueryManager;
import com.main.cognizant.dbutils.QueryManager.Query;
import com.main.db.utils.Record;
import com.main.db.utils.Recordset;
import com.main.models.ClaimFilter;
import com.main.models.ClaimValidation;
import com.main.models.ClaimValidationResult;
import com.main.models.TestCondition;
import com.main.repositories.ClaimValidationRepository;
import com.main.repositories.MetaXWalkRepository;
import com.main.repositories.TestConditionRepository;
import com.main.thirdpartyfunctions.Claims;
import com.main.thirdpartyfunctions.FacetsDataSupplier;

@Service
public class AllAsync {

	@Autowired
	private MetaXWalkRepository metaRepo;

	@Autowired
	private ClaimValidationRepository claimValidationRepo;
	
	@Autowired
	private TestConditionRepository testRepo;

	@Transactional
	@Async
	public void claimValidation(ClaimValidation data) {
		
		data.setStatus("Validating");
		claimValidationRepo.save(data);
		
		TestCondition test = testRepo.findById(data.getTestId())
				.orElseThrow(() -> new IllegalStateException("Test with id:'" + data.getTestId() + "' doesnot exists"));
		List<ClaimFilter> claimFilters = test.getClaimFilters();
		
		FacetsDataSupplier facetsDS;
		try {
			List<ClaimFilter> claimFiltersDB = claimFilters.stream()
					.map(cf -> new ClaimFilter(
							metaRepo.findByUserField(cf.getField()).getDbFieldName(),
							cf.getOperator(), cf.getValue()))
					.collect(Collectors.toList());
			
			List<String> userFieldsNames = claimFilters.stream().map(cf -> cf.getField()).collect(Collectors.toList());

			List<String> claimIds = data.getClaimIds();

			QueryManager qryManager = QueryManager.getInstance();
			facetsDS = new FacetsDataSupplier(data.getEnv());
			facetsDS.init();
			List<ClaimValidationResult> results = new ArrayList<ClaimValidationResult>();

			for (String claimId : claimIds) {

				String validationQry = new Claims(test.getClaimType()).getValidationQuery(claimFiltersDB, userFieldsNames, claimId, test);
				Query qry = qryManager.setQuery(String.format("cl_val_%s", claimId), validationQry);

				Recordset rs = facetsDS.getRecordset(qry);

				if (rs == null)
				{
					data.setStatus("ClaimNotFound");
					facetsDS.close();
					claimValidationRepo.saveAndFlush(data);
					return;
				}
					

				for (Record r : rs) {
					ClaimValidationResult claimValidationResultStatusRow = new ClaimValidationResult(
							r.getValue("CLAIM"), "Status", "!=15", r.getValue("Status"), r.getValue("Status_Res"));
					results.add(claimValidationResultStatusRow);

					List<ClaimValidationResult> result = claimFilters.stream()
							.map(cf -> new ClaimValidationResult(r.getValue("CLAIM"), cf.getField(),
									String.format("%s %s", cf.getOperator(), cf.getValue()), r.getValue(cf.getField()),
									r.getValue(String.format("%s_Res", cf.getField()))))
							.collect(Collectors.toList());

					results.addAll(result);

				}

			}
			data.setResult(results);
			data.setStatus("Completed");
			facetsDS.close();
		} catch (Exception e) {
			e.printStackTrace();
			data.setStatus("Error");
		}

		claimValidationRepo.saveAndFlush(data);

	}

}
