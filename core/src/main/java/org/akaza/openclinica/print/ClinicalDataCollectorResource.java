package org.akaza.openclinica.print;

import java.util.LinkedHashMap;
import java.util.Locale;

import org.akaza.openclinica.bean.odmbeans.OdmClinicalDataBean;
import org.akaza.openclinica.service.extract.GenerateClinicalDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClinicalDataCollectorResource {

    @Autowired
    private GenerateClinicalDataService generateClinicalDataService;

    public LinkedHashMap<String, OdmClinicalDataBean> generateClinicalData(String studyOID, String studySubjOID, String studyEventOID, String formVersionOID, boolean includeDNs, boolean includeAudits, Locale locale, int userId) {
        return getGenerateClinicalDataService().getClinicalData(studyOID, studySubjOID, studyEventOID, formVersionOID, includeDNs, includeAudits, locale, userId);
    }

    public GenerateClinicalDataService getGenerateClinicalDataService() {
        return generateClinicalDataService;
    }

    public void setGenerateClinicalDataService(GenerateClinicalDataService generateClinicalDataService) {
        this.generateClinicalDataService = generateClinicalDataService;
    }
}
