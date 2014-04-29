package org.akaza.openclinica.web.job;

import com.clinovo.coding.Search;
import com.clinovo.coding.model.Classification;
import com.clinovo.coding.model.ClassificationElement;
import com.clinovo.coding.source.impl.BioPortalSearchInterface;
import com.clinovo.model.*;
import com.clinovo.service.CodedItemService;
import com.clinovo.service.DictionaryService;
import com.clinovo.service.TermService;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CodingSpringJob extends QuartzJobBean {

    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

    private CodedItemService codedItemService;
    private DictionaryService dictionaryService;
    private TermService termService;
    private StudyParameterValueDAO studyParameterValueDAO;
    private DataSource datasource;
    private Search search = new Search();

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        JobDetailImpl jobDetail = (JobDetailImpl) context.getJobDetail();
        JobDataMap dataMap = jobDetail.getJobDataMap();

        String verbatimTerm = dataMap.getString(CodingTriggerService.VERBATIM_TERM);
        boolean isAlias = dataMap.getBooleanFromString(CodingTriggerService.IS_ALIAS);
        String categoryList = dataMap.getString(CodingTriggerService.CATEGORY_LIST);
        String codeSearchTerm = dataMap.getString((CodingTriggerService.CODE_SEARCH_TERM));
        String bioontologyUrl = dataMap.getString(CodingTriggerService.BIOONTOLOGY_URL);
        String bioontologyApiKey = dataMap.getString(CodingTriggerService.BIOONTOLOGY_API_KEY);
        int codedItemId = Integer.valueOf(dataMap.getString(CodingTriggerService.CODED_ITEM_ID));

        try {

            ApplicationContext appContext = (ApplicationContext) context.getScheduler().getContext().get("applicationContext");

            datasource = (DataSource) appContext.getBean("dataSource");
            termService = (TermService) appContext.getBean("termService");
            studyParameterValueDAO = new StudyParameterValueDAO(datasource);
            dictionaryService = (DictionaryService) appContext.getBean("dictionaryService");
            codedItemService = (CodedItemService) appContext.getBean("codedItemServiceImpl");

            CodedItem codedItem = codedItemService.findCodedItem(codedItemId);
            Classification classificationResult = getClassificationFromCategoryString(categoryList);
            search.setSearchInterface(new BioPortalSearchInterface());

			if (codedItem.getDictionary().equals("WHOD")) {

				String termUniqKey = classificationResult.getHttpPath();
				termUniqKey = termUniqKey.substring(termUniqKey.indexOf("@"), termUniqKey.length());

				for (ClassificationElement whodClassElement : classificationResult.getClassificationElement()) {
					whodClassElement.setCodeName(whodClassElement.getCodeName().replaceAll(" &amp; ", " and ").replaceAll(" ", "_") + termUniqKey);
				}

				int componentElementIndex = classificationResult.getClassificationElement().size() - 4;
				String componentField = classificationResult.getClassificationElement().get(componentElementIndex).getCodeName() + "_com";
				classificationResult.getClassificationElement().get(componentElementIndex).setCodeName(componentField);
			}

			//get codes for all verb terms & save it in classification
            search.getClassificationWithCodes(classificationResult, codedItem.getDictionary().replace("_", " "), bioontologyUrl, bioontologyApiKey);
            //replace all terms & codes from classification to coded elements
            generateCodedItemFields(codedItem.getCodedItemElements(), classificationResult.getClassificationElement());

            //if isAlias is true, create term using completed classification
            if (isAlias) {

                StudyParameterValueBean configuredDictionary = studyParameterValueDAO.findByHandleAndStudy(codedItem.getStudyId(), "autoCodeDictionaryName");
                Dictionary dictionary = dictionaryService.findDictionary(configuredDictionary.getValue());

                Term term = new Term();

                term.setDictionary(dictionary);
                term.setLocalAlias(verbatimTerm.toLowerCase());
                term.setPreferredName(codeSearchTerm.toLowerCase());
                term.setHttpPath(classificationResult.getHttpPath());
                term.setExternalDictionaryName(codedItem.getDictionary());
                term.setTermElementList(generateTermElementList(classificationResult.getClassificationElement()));

                termService.saveTerm(term);
            }

            codedItem.setStatus((String.valueOf(Status.CodeStatus.CODED)));
            codedItem.setHttpPath(classificationResult.getHttpPath());

            codedItemService.saveCodedItem(codedItem);

        } catch (Exception e) {

            logger.error(e.getMessage());

            JobExecutionException qe = new JobExecutionException(e);
            qe.refireImmediately();
            throw qe;
        }
    }

    private Classification getClassificationFromCategoryString(String categoryList) {

        Classification classification = new Classification();
        List<String> list = new ArrayList<String>(Arrays.asList(categoryList.split("\\|")));

        for (int i = 0; i < list.size(); i++) {

			if (list.get(i).equals("HTTP")) {
				classification.setHttpPath(list.get(i + 1));
				i++;
			} else if (!list.get(i).isEmpty()) {

				ClassificationElement classificationElement = new ClassificationElement();
				classificationElement.setElementName(list.get(i));
				classificationElement.setCodeName(list.get(i + 1));
				classification.addClassificationElement(classificationElement);
				i++;
			}
		}

		if (classification.getHttpPath().indexOf("whod") > 0) {

			String whodKey = classification.getHttpPath().substring(classification.getHttpPath().indexOf("@"), classification.getHttpPath().length());

			for (ClassificationElement classificationElement : classification.getClassificationElement()) {
				classificationElement.setCodeName(classificationElement.getCodeName().replaceAll(" ", "_").replaceAll(" & ", "_and_") + whodKey);
			}
		}

        return classification;
    }

    private List<TermElement> generateTermElementList(List<ClassificationElement> classificationElementList) {

        List<TermElement> termElementList = new ArrayList<TermElement>();

        for(ClassificationElement classElement : classificationElementList) {

            TermElement newTermElement = new TermElement(classElement.getCodeName(), classElement.getCodeValue(), classElement.getElementName());
            termElementList.add(newTermElement);
        }

        return termElementList;
    }

	private void generateCodedItemFields(List<CodedItemElement> codedItemElements, List<ClassificationElement> classificationElements) {

		for (CodedItemElement codedItemElement : codedItemElements) {

			for (ClassificationElement classificationElement : classificationElements) {

				String name = codedItemElement.getItemName();

				if (name.equals(classificationElement.getElementName())) {

					codedItemElement.setItemCode(classificationElement.getCodeName());

				} else if (name.equals(classificationElement.getElementName() + "C")) {

					codedItemElement.setItemCode(classificationElement.getCodeValue());

				} else if (name.equals("MPSEQ") && classificationElement.getElementName().equals("CMP")) {

					codedItemElement.setItemCode(classificationElement.getCodeValue());
				}
			}
		}
	}
}
