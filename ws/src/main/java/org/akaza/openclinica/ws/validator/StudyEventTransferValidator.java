/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

package org.akaza.openclinica.ws.validator;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.ws.bean.StudyEventTransferBean;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@SuppressWarnings({"rawtypes"})
public class StudyEventTransferValidator implements Validator {

	DataSource dataSource;
	StudyDAO studyDAO;
	StudySubjectDAO studySubjectDAO;
	StudyEventDefinitionDAO studyEventDefinitionDAO;
	BaseVSValidatorImplementation helper;

	public StudyEventTransferValidator(DataSource dataSource) {
		this.dataSource = dataSource;
		helper = new BaseVSValidatorImplementation();
	}

	public boolean supports(Class clazz) {
		return StudyEventTransferBean.class.equals(clazz);
	}

	public void validate(Object obj, Errors e) {
		StudyEventTransferBean studyEventTransferBean = (StudyEventTransferBean) obj;

		// Non Business Validation
		if (studyEventTransferBean.getStudyUniqueId() == null || studyEventTransferBean.getStudyUniqueId().length() < 1) {
			e.reject("studyEventTransferValidator.invalid_study_identifier");
			return;
		}

		// Business Validation
		Status[] included_status = new Status[] { Status.AVAILABLE, Status.PENDING };
		StudyBean study = helper.verifyStudy(getStudyDAO(), studyEventTransferBean.getStudyUniqueId(), included_status,
				e);
		if (study == null) {
			return;
		}
		studyEventTransferBean.setStudy(study);
		StudyBean site = null;
		int site_id = -1;
		if (studyEventTransferBean.getSiteUniqueId() != null) {
			site = helper.verifySite(getStudyDAO(), studyEventTransferBean.getStudyUniqueId(),
					studyEventTransferBean.getSiteUniqueId(), included_status, e);
			if (site == null) {
				return;
			}
			site_id = site.getId();
			studyEventTransferBean.setStudy(site);
		}

		boolean isRoleVerified = helper.verifyRole(studyEventTransferBean.getUser(), study.getId(), site_id,
				Role.STUDY_MONITOR, e);
		if (!isRoleVerified)
			return;

		// Non Business Validation
		if (studyEventTransferBean.getSubjectLabel() == null) {
			e.reject("studyEventTransferValidator.studySubjectId_required");
			return;
		}

		StudySubjectBean studySubject = getStudySubjectDAO().findByLabelAndStudy(
				studyEventTransferBean.getSubjectLabel(), studyEventTransferBean.getStudy());
		// it is not null but label null
		if (studySubject == null || studySubject.getOid() == null) {
			e.reject("studyEventTransferValidator.study_subject_does_not_exist",
					new Object[] { studyEventTransferBean.getSubjectLabel(),
							studyEventTransferBean.getStudy().getName() }, "StudySubject label you specified "
							+ studyEventTransferBean.getSubjectLabel() + " does not correspond to a study "
							+ studyEventTransferBean.getStudy().getName());
			return;
		}

		// Non Business Validation
		if (studyEventTransferBean.getEventDefinitionOID() == null
				|| studyEventTransferBean.getEventDefinitionOID().length() < 1) {
			e.reject("studyEventTransferValidator.eventDefinitionOID_required");
			return;
		}

		if (studyEventTransferBean.getStartDateTime() == null) {
			e.reject("studyEventTransferValidator.startDateTime_required");
			return;
		}
		if (studyEventTransferBean.getLocation() == null || studyEventTransferBean.getLocation().length() < 1) {
			e.reject("studyEventTransferValidator.location_required");
			return;
		}

		if (studyEventTransferBean.getEndDateTime() != null && studyEventTransferBean.getStartDateTime() != null) {
			if (studyEventTransferBean.getEndDateTime().compareTo(studyEventTransferBean.getStartDateTime()) == -1) {
				e.reject(
						"studyEventTransferValidator.start_date_after_end_date",
						new Object[] { studyEventTransferBean.getStartDateTime(),
								studyEventTransferBean.getEndDateTime() },
						"Start date " + studyEventTransferBean.getStartDateTime() + "  after end date ("
								+ studyEventTransferBean.getEndDateTime() + ").");

				return;
			}
		}
		int parentStudyId = study.getParentStudyId();
		StudyEventDefinitionBean studyEventDefinition = getStudyEventDefinitionDAO().findByOidAndStudy(
				studyEventTransferBean.getEventDefinitionOID(), study.getId(), parentStudyId);
		if (studyEventDefinition == null) {
			e.reject("studyEventTransferValidator.invalid_eventDefinitionOID",
					new Object[] { studyEventTransferBean.getEventDefinitionOID() },
					"EventDefinitionOID you specified " + studyEventTransferBean.getEventDefinitionOID()
							+ " is not valid.");
			return;
		}

	}

	public StudyDAO getStudyDAO() {
		return this.studyDAO != null ? studyDAO : new StudyDAO(dataSource);
	}

	public StudySubjectDAO getStudySubjectDAO() {
		return this.studySubjectDAO != null ? studySubjectDAO : new StudySubjectDAO(dataSource);
	}

	public StudyEventDefinitionDAO getStudyEventDefinitionDAO() {
		return this.studyEventDefinitionDAO != null ? studyEventDefinitionDAO : new StudyEventDefinitionDAO(dataSource);
	}

}
