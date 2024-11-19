SELECT PRPR.PRPR_ID as "Provider ID",
PRPR.PRPR_NAME as "Provider FirstName",
PRPR.PRPR_NAME as "Provider LastName",
PRPR.PRPR_NAME as "Provider MiddleInitial",
PRPR.PRPR_NPI as "Provider NPI",
PRPR.PRPR_ENTITY as "Provider Type",
PRPR.MCTN_ID as "Provider Tax ID",
PRAD.PRAD_ADDR1 as "Provider AddrLine1",
PRAD.PRAD_ADDR2 as "Provider AddrLine2",
PRAD.PRAD_CITY as "Provider City",
PRAD.PRAD_STATE as "Provider State",
PRAD.PRAD_ZIP  as "Provider Zip"
FROM FACETS.CMC_PRPR_PROV PRPR JOIN FACETS.CMC_PRAD_ADDRESS PRAD ON PRAD.PRAD_ID = PRPR.PRAD_ID 
WHERE PRPR.PRPR_ID=<ProviderID>