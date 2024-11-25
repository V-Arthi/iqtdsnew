SELECT DISTINCT 
CLCL.CLCL_ID AS "ID", 
CDML.CDML_SEQ_NO AS "Line #",
CLCL.CLCL_CL_TYPE AS "Type", 
CLCL.CLCL_CL_SUB_TYPE AS "Sub Type", 
CLCL.CLCL_RECD_DT AS "Received Date", CLCL.PDPD_ID As "Product ID", 
CLCL.CLCL_NTWK_IND AS "Network Type", CDML.PSCD_ID AS "Place Of Service",
CDML.IPCD_ID AS "Procedure Code", CLCL.CLCL_TOT_CHG AS "Total Charge", 
CDML.CDML_ALLOW AS "Allowed Amount", CLCL.CLCL_PA_PAID_AMT AS "Paid Amount"
FROM FACETS.CMC_CLCL_CLAIM CLCL
JOIN FACETS.CMC_CDML_CL_LINE CDML ON CDML.CLCL_ID=CLCL.CLCL_ID
JOIN FACETS.CMC_CLHP_HOSP CLHP ON CLHP.CLCL_ID=CLCL.CLCL_ID
JOIN FACETS.CMC_SBSB_SUBSC SBSB ON SBSB.SBSB_CK = CLCL.SBSB_CK
JOIN FACETS.CMC_MEME_MEMBER MEME ON MEME.MEME_CK=CLCL.MEME_CK
JOIN FACETS.CMC_SGSG_SUB_GROUP SGSG ON SGSG.SGSG_CK = CLCL.SGSG_CK
JOIN FACETS.CMC_GRGR_GROUP GRGR ON GRGR.GRGR_CK=CLCL.GRGR_CK
LEFT JOIN FACETS.CMC_CLCB_CL_COB CLCB 	ON CLCL.CLCL_ID=CLCB.CLCL_ID
LEFT JOIN FACETS.CMC_CDSD_SUPP_DATA CDSD ON CLCL.CLCL_ID=CDSD.CLCL_ID
LEFT JOIN FACETS.CMC_CLED_EDI_DATA CLED ON CLCL.CLCL_ID=CLED.CLCL_ID
LEFT JOIN FACETS.CMC_IPCD_PROC_CD IPCD ON CDML.IPCD_ID=IPCD.IPCD_ID
JOIN FACETS.CMC_PRPR_PROV PRPR ON CLCL.PRPR_ID= PRPR.PRPR_ID
LEFT JOIN FACETS.NWX_WWMS_WARNMSG WWMS ON CLCL.CLCL_ID = WWMS.WWMS_MESSAGE_ID
LEFT JOIN FACETS.CMC_CLHP_HOSP CLHP ON CLCL.CLCL_ID = CLHP.CLCL_ID
LEFT JOIN FACETS.CMC_CDOR_LI_OVR CDOR ON CLCL.CLCL_ID = CDOR.CLCL_ID
LEFT JOIN FACETS.CMC_CLOR_CL_OVR CLOR ON CLCL.CLCL_ID = CLOR.CLCL_ID
LEFT JOIN FACETS.CMC_CLAP_ALT_PAYEE CLAP ON CLCL.CLCL_ID = CLAP.CLCL_ID
LEFT JOIN FACETS.CMC_CDMD_LI_DISALL CDMD ON CLCL.CLCL_ID = CDMD.CLCL_ID
LEFT JOIN FACETS.CMC_CDSM_LI_MSUPP CDSM ON CLCL.CLCL_ID = CDSM.CLCL_ID
LEFT JOIN FACETS.CMC_CLCK_CLM_CHECK CLCK ON CLCL.CLCL_ID = CLCK.CLCL_ID
LEFT JOIN FACETS.CMC_MATX_ACCUM_TXN MATX ON CLCL.MEME_CK = MATX.MEME_CK
LEFT JOIN FACETS.CMC_FATX_ACCUM_TXN FATX ON CLCL.MEME_CK = FATX.MEME_CK
LEFT JOIN FACETS.CMC_LOBD_LINE_BUS LOBD ON CDML.LOBD_ID = LOBD.LOBD_ID
LEFT JOIN FACETS_CUSTOM.EMBT_CLCL_PROV_MATCH_LOG PROV_MATCH ON CLCL.CLCL_ID=PROV_MATCH.CLCL_ID
LEFT JOIN FACETS_CUSTOM.EMBT_CLCL_CLAIM_ARCH CLAIM_ARCH ON CLCL.CLCL_ID=CLAIM_ARCH.CLCL_ID
LEFT JOIN FACETS_XC.CMC_CLNT_NOTES CLNT_NOTES ON CLCL.CLCL_ID=CLNT_NOTES.CLCL_ID
LEFT JOIN FACETS.CMC_CDEC_LI_ENC CDEC ON CLCL.CLCL_ID = CDEC.CLCL_ID
LEFT JOIN FACETS_XC.CMC_CLED_EDI_DATA XC_CLED ON CLCL.CLCL_ID=XC_CLED.CLCL_IDs
LEFT JOIN FACETS_CUSTOM.EMBT_CLED_EDI_DATA_ARCH CUSTOM_CLED ON CLCL.CLCL_ID=CUSTOM_CLED.CLCL_ID