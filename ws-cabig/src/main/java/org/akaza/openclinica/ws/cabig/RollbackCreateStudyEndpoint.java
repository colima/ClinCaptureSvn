/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2010-2011 Akaza Research

 * Development of this web service or portions thereof has been funded
 * by Federal Funds from the National Cancer Institute, 
 * National Institutes of Health, under Contract No. HHSN261200800001E.
 * In addition to the GNU LGPL license, this code is also available
 * from NCI CBIIT repositories under the terms of the caBIG Software License. 
 * For details see: https://cabig.nci.nih.gov/adopt/caBIGModelLicense
 */
package org.akaza.openclinica.ws.cabig;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.ws.cabig.abst.AbstractCabigDomEndpoint;
import org.akaza.openclinica.ws.logic.CreateStudyService;
import org.springframework.context.MessageSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.sql.DataSource;

public class RollbackCreateStudyEndpoint extends AbstractCabigDomEndpoint {
    public CreateStudyService studyService;
    private StudyGroupDAO studyGroupDao;
    private DatasetDAO datasetDao;
    // getStudyGroupClassDao(), getSubjectGroupMapDao(), getEventDefinitionCrfDao(),
    private StudyGroupClassDAO studyGroupClassDao;
    private SubjectGroupMapDAO subjectGroupMapDao;
    private EventDefinitionCRFDAO eventDefinitionCrfDao;

    public RollbackCreateStudyEndpoint(DataSource dataSource, MessageSource messages, CoreResources coreResources) {

        super(dataSource, messages, coreResources);
        studyService = new CreateStudyService();

    }

    public StudyGroupDAO getStudyGroupDao() {
        studyGroupDao = studyGroupDao != null ? studyGroupDao : new StudyGroupDAO(dataSource);
        return studyGroupDao;
    }

    public DatasetDAO getDatasetDao() {
        datasetDao = datasetDao != null ? datasetDao : new DatasetDAO(dataSource);
        return datasetDao;
    }

    public StudyGroupClassDAO getStudyGroupClassDao() {
        studyGroupClassDao = studyGroupClassDao != null ? studyGroupClassDao : new StudyGroupClassDAO(dataSource);
        return studyGroupClassDao;
    }

    public SubjectGroupMapDAO getSubjectGroupMapDao() {
        subjectGroupMapDao = subjectGroupMapDao != null ? subjectGroupMapDao : new SubjectGroupMapDAO(dataSource);
        return subjectGroupMapDao;
    }

    public EventDefinitionCRFDAO getEventDefinitionCrfDao() {
        eventDefinitionCrfDao = eventDefinitionCrfDao != null ? eventDefinitionCrfDao : new EventDefinitionCRFDAO(dataSource);
        return eventDefinitionCrfDao;
    }

    protected Element invokeInternal(Element requestElement, Document document) throws Exception {
        System.out.println("Request text rollback create study ");
        StudyBean studyBean = new StudyBean();
        studyBean.setIdentifier("null");

        try {
            NodeList nlist = requestElement.getElementsByTagNameNS(CONNECTOR_NAMESPACE_V1, "studyProtocol");
            // this.logNodeList(nlist);
            for (int i = 0; i < nlist.getLength(); i++) {

                Node study = nlist.item(i);
                studyBean = studyService.generateStudyBean(getUserAccount(), study);
                StudyBean testStudyBean = getStudyDao().findByUniqueIdentifier(studyBean.getIdentifier());

                testStudyBean.setOldStatus(Status.AVAILABLE);
                testStudyBean.setStatus(Status.DELETED);
                testStudyBean = (StudyBean) getStudyDao().updateStudyStatus(testStudyBean);
                // rollback all sites
                // testStudyBean = (StudyBean) getStudyDao().updateSitesStatus(testStudyBean);
                // above doesn't work, trying something else
                ArrayList<StudyBean> sites = (ArrayList<StudyBean>) getStudyDao().findAllByParent(testStudyBean.getId());
                for (StudyBean site : sites) {
                    site.setUpdater(getUserAccount());
                    site.setUpdatedDate(new Date(System.currentTimeMillis()));
                    site.setOldStatus(Status.AVAILABLE);
                    site.setStatus(Status.DELETED);
                    site = (StudyBean) getStudyDao().update(site);
                }
                testStudyBean =
                    studyService.changeStatus(Status.AVAILABLE, Status.AUTO_DELETED, testStudyBean, getUserAccount(), getUserAccountDao(),
                            getStudySubjectDao(), getStudyGroupDao(), getStudyGroupClassDao(), getSubjectGroupMapDao(), getEventDefinitionCrfDao(),
                            getStudyEventDefinitionDao(), getStudyEventDao(), getEventCrfDao(), getItemDataDao(), getDatasetDao());
            }
            return mapCreateStudyConfirmation(studyBean.getIdentifier());
        } catch (Exception npe) {
            npe.printStackTrace();
            // TODO figure out exception and send response
            if (npe.getClass().getName().startsWith("org.akaza.openclinica.ws.cabig.exception")) {
                System.out.println("found " + npe.getClass().getName());
                OpenClinicaException ope = (OpenClinicaException) npe;
                HashMap<String, String> validations = new HashMap<String, String>();
                validations.put("Study Creation Request", ope.message);
                return mapStudyErrorConfirmation("", ope, validations);
            } else {
                System.out.println(" did not find openclinica exception, found " + npe.getClass().getName());
                return mapStudyErrorConfirmation(npe.getMessage());
            }
        }
    }

}
