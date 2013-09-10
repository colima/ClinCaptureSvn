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

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.login;

import com.clinovo.util.ValidatorHelper;

import java.util.ArrayList;
import java.util.Locale;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.admin.EventStatusStatisticsTableFactory;
import org.akaza.openclinica.control.admin.SiteStatisticsTableFactory;
import org.akaza.openclinica.control.admin.StudyStatisticsTableFactory;
import org.akaza.openclinica.control.admin.StudySubjectStatusStatisticsTableFactory;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.control.submit.ListStudySubjectTableFactory;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.dynamicevent.DynamicEventDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.table.sdv.SDVUtil;

/**
 * Processes the request of changing current study
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class ChangeStudyServlet extends SecureController {

	Locale locale;
	private StudyEventDefinitionDAO studyEventDefinitionDAO;
	private SubjectDAO subjectDAO;
	private StudySubjectDAO studySubjectDAO;
	private StudyEventDAO studyEventDAO;
	private StudyGroupClassDAO studyGroupClassDAO;
	private SubjectGroupMapDAO subjectGroupMapDAO;
	private StudyDAO studyDAO;
	private EventCRFDAO eventCRFDAO;
	private EventDefinitionCRFDAO eventDefintionCRFDAO;
	private StudyGroupDAO studyGroupDAO;
	private DynamicEventDao dynamicEventDao;
	private DiscrepancyNoteDAO discrepancyNoteDAO;

	@Override
	public void mayProceed() throws InsufficientPermissionException {

		locale = request.getLocale();
	}

	@Override
	public void processRequest() throws Exception {
        String action = request.getParameter("action");// action sent by user
		UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
		StudyDAO sdao = new StudyDAO(sm.getDataSource());

		ArrayList studies = udao.findStudyByUser(ub, (ArrayList) sdao.findAll());
        request.setAttribute("roleMap", Role.roleMap);
		if (request.getAttribute("label") != null) {
			String label = (String) request.getAttribute("label");
			if (label.length() > 0) {
				request.setAttribute("label", label);
			}
		}

		ArrayList validStudies = new ArrayList();
		for (int i = 0; i < studies.size(); i++) {
			StudyUserRoleBean sr = (StudyUserRoleBean) studies.get(i);
			StudyBean study = (StudyBean) sdao.findByPK(sr.getStudyId());
			// FIXME too many queries to the DB
			if (study != null && study.getStatus().equals(Status.PENDING)) {
				sr.setStatus(study.getStatus());
			}
			validStudies.add(sr);
		}

		if (StringUtil.isBlank(action)) {
			request.setAttribute("studies", validStudies);

			forwardPage(Page.CHANGE_STUDY);
		} else {

			if ("confirm".equalsIgnoreCase(action)) {
				logger.info("confirm");
				confirmChangeStudy(studies);

			} else if ("submit".equalsIgnoreCase(action)) {
				logger.info("submit");
				changeStudy();
			}
		}

	}

	private void confirmChangeStudy(ArrayList studies) throws Exception {
		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		FormProcessor fp = new FormProcessor(request);
		v.addValidation("studyId", Validator.IS_AN_INTEGER);

		errors = v.validate();

		if (!errors.isEmpty()) {
			request.setAttribute("studies", studies);
			forwardPage(Page.CHANGE_STUDY);
		} else {
			int studyId = fp.getInt("studyId");
			logger.info("new study id:" + studyId);
			for (int i = 0; i < studies.size(); i++) {
				StudyUserRoleBean studyWithRole = (StudyUserRoleBean) studies.get(i);
				if (studyWithRole.getStudyId() == studyId) {
					request.setAttribute("studyId", new Integer(studyId));
					session.setAttribute("studyWithRole", studyWithRole);
					request.setAttribute("currentStudy", currentStudy);
					forwardPage(Page.CHANGE_STUDY_CONFIRM);
					return;
				}
			}
			addPageMessage(restext.getString("no_study_selected"));

			forwardPage(Page.CHANGE_STUDY);
		}
	}

	private void changeStudy() throws Exception {
		
		FormProcessor fp = new FormProcessor(request);
		int studyId = fp.getInt("studyId");
		int prevStudyId = currentStudy.getId();

        ub.updateSysAdminRole(studyId, prevStudyId);

		StudyDAO sdao = new StudyDAO(sm.getDataSource());
		StudyBean current = (StudyBean) sdao.findByPK(studyId);

		StudyParameterValueDAO spvdao = new StudyParameterValueDAO(sm.getDataSource());

		ArrayList studyParameters = spvdao.findParamConfigByStudy(current);
		current.setStudyParameters(studyParameters);
		int parentStudyId = currentStudy.getParentStudyId() > 0 ? currentStudy.getParentStudyId() : currentStudy
				.getId();
		StudyParameterValueBean parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "subjectIdGeneration");
		current.getStudyParameterConfig().setSubjectIdGeneration(parentSPV.getValue());
		String idSetting = current.getStudyParameterConfig().getSubjectIdGeneration();
		if (idSetting.equals("auto editable") || idSetting.equals("auto non-editable")) {
			int nextLabel = this.getStudySubjectDAO().findTheGreatestLabel() + 1;
			request.setAttribute("label", new Integer(nextLabel).toString());
		}

		StudyConfigService scs = new StudyConfigService(sm.getDataSource());
		if (current.getParentStudyId() <= 0) {// top study
			scs.setParametersForStudy(current);

		} else {
			// YW <<
			if (current.getParentStudyId() > 0) {
				current.setParentStudyName(((StudyBean) sdao.findByPK(current.getParentStudyId())).getName());

			}
			// YW 06-12-2007>>
			scs.setParametersForSite(current);

		}
		if (current.getStatus().equals(Status.DELETED) || current.getStatus().equals(Status.AUTO_DELETED)) {
			session.removeAttribute("studyWithRole");
			addPageMessage(restext.getString("study_choosed_removed_restore_first"));
		} else {
			session.setAttribute("study", current);
			currentStudy = current;
			// change user's active study id
			UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
			ub.setActiveStudyId(current.getId());
			ub.setUpdater(ub);
			ub.setUpdatedDate(new java.util.Date());
			udao.update(ub);

			currentRole = (StudyUserRoleBean) session.getAttribute("studyWithRole");
			session.setAttribute("userRole", currentRole);
			session.removeAttribute("studyWithRole");
			addPageMessage(restext.getString("current_study_changed_succesfully"));
		}
		ub.incNumVisitsToMainMenu();
		// YW 2-18-2008, if study has been really changed <<
		if (prevStudyId != studyId) {
			session.removeAttribute("eventsForCreateDataset");
			session.setAttribute("tableFacadeRestore", "false");
		}
		request.setAttribute("studyJustChanged", "yes");
		// YW >>

		// Integer assignedDiscrepancies = getDiscrepancyNoteDAO().countAllItemDataByStudyAndUser(currentStudy, ub);
		Integer assignedDiscrepancies = getDiscrepancyNoteDAO().getViewNotesCountWithFilter(
				" AND dn.assigned_user_id =" + ub.getId()
						+ " AND (dn.resolution_status_id=1 OR dn.resolution_status_id=2 OR dn.resolution_status_id=3)",
				currentStudy);
		request.setAttribute("assignedDiscrepancies", assignedDiscrepancies == null ? 0 : assignedDiscrepancies);

		if (currentRole.isInvestigator() || currentRole.isClinicalResearchCoordinator()) {
			setupListStudySubjectTable();
		}
		if (currentRole.isMonitor()) {
			setupSubjectSDVTable();
		} else if (currentRole.isStudyAdministrator() || currentRole.isStudyDirector()) {
			if (currentStudy.getStatus().isPending()) {
				response.sendRedirect(request.getContextPath() + Page.MANAGE_STUDY_MODULE);
				return;
			}
			setupStudySiteStatisticsTable();
			setupSubjectEventStatusStatisticsTable();
			setupStudySubjectStatusStatisticsTable();
			if (currentStudy.getParentStudyId() == 0) {
				setupStudyStatisticsTable();
			}

		}

		// forwardPage(Page.MENU);
		response.sendRedirect("/" + getContextPath() + Page.MENU_SERVLET.getFileName());
	}

	private void setupSubjectSDVTable() {

		request.setAttribute("studyId", currentStudy.getId());
		String sdvMatrix = getSDVUtil().renderEventCRFTableWithLimit(request, currentStudy.getId(), "");
		request.setAttribute("sdvMatrix", sdvMatrix);
	}

	private void setupStudySubjectStatusStatisticsTable() {

		StudySubjectStatusStatisticsTableFactory factory = new StudySubjectStatusStatisticsTableFactory();
		factory.setStudySubjectDao(getStudySubjectDAO());
		factory.setCurrentStudy(currentStudy);
		factory.setStudyDao(getStudyDAO());
		String studySubjectStatusStatistics = factory.createTable(request, response).render();
		request.setAttribute("studySubjectStatusStatistics", studySubjectStatusStatistics);
	}

	private void setupSubjectEventStatusStatisticsTable() {

		EventStatusStatisticsTableFactory factory = new EventStatusStatisticsTableFactory();
		factory.setStudySubjectDao(getStudySubjectDAO());
		factory.setCurrentStudy(currentStudy);
		factory.setStudyEventDao(getStudyEventDAO());
		factory.setStudyDao(getStudyDAO());
		String subjectEventStatusStatistics = factory.createTable(request, response).render();
		request.setAttribute("subjectEventStatusStatistics", subjectEventStatusStatistics);
	}

	private void setupStudySiteStatisticsTable() {

		SiteStatisticsTableFactory factory = new SiteStatisticsTableFactory();
		factory.setStudySubjectDao(getStudySubjectDAO());
		factory.setCurrentStudy(currentStudy);
		factory.setStudyDao(getStudyDAO());
		String studySiteStatistics = factory.createTable(request, response).render();
		request.setAttribute("studySiteStatistics", studySiteStatistics);

	}

	private void setupStudyStatisticsTable() {

		StudyStatisticsTableFactory factory = new StudyStatisticsTableFactory();
		factory.setStudySubjectDao(getStudySubjectDAO());
		factory.setCurrentStudy(currentStudy);
		factory.setStudyDao(getStudyDAO());
		String studyStatistics = factory.createTable(request, response).render();
		request.setAttribute("studyStatistics", studyStatistics);

	}

	private void setupListStudySubjectTable() {

		ListStudySubjectTableFactory factory = new ListStudySubjectTableFactory(true);
		factory.setStudyEventDefinitionDao(getStudyEventDefinitionDao());
		factory.setSubjectDAO(getSubjectDAO());
		factory.setStudySubjectDAO(getStudySubjectDAO());
		factory.setStudyEventDAO(getStudyEventDAO());
		factory.setStudyBean(currentStudy);
		factory.setStudyGroupClassDAO(getStudyGroupClassDAO());
		factory.setSubjectGroupMapDAO(getSubjectGroupMapDAO());
		factory.setStudyDAO(getStudyDAO());
		factory.setCurrentRole(currentRole);
		factory.setCurrentUser(ub);
		factory.setEventCRFDAO(getEventCRFDAO());
		factory.setEventDefintionCRFDAO(getEventDefinitionCRFDAO());
		factory.setDiscrepancyNoteDAO(getDiscrepancyNoteDAO());
		factory.setStudyGroupDAO(getStudyGroupDAO());
		factory.setDynamicEventDao(getDynamicEventDao());
		String findSubjectsHtml = factory.createTable(request, response).render();
		request.setAttribute("findSubjectsHtml", findSubjectsHtml);
	}
	
	public DynamicEventDao getDynamicEventDao() {
		dynamicEventDao = this.dynamicEventDao == null ? new DynamicEventDao(sm.getDataSource()) : dynamicEventDao;
		return dynamicEventDao;
	}

	public StudyEventDefinitionDAO getStudyEventDefinitionDao() {
		studyEventDefinitionDAO = studyEventDefinitionDAO == null ? new StudyEventDefinitionDAO(sm.getDataSource())
				: studyEventDefinitionDAO;
		return studyEventDefinitionDAO;
	}

	public SubjectDAO getSubjectDAO() {
		subjectDAO = this.subjectDAO == null ? new SubjectDAO(sm.getDataSource()) : subjectDAO;
		return subjectDAO;
	}

	public StudySubjectDAO getStudySubjectDAO() {
		studySubjectDAO = this.studySubjectDAO == null ? new StudySubjectDAO(sm.getDataSource()) : studySubjectDAO;
		return studySubjectDAO;
	}

	public StudyGroupClassDAO getStudyGroupClassDAO() {
		studyGroupClassDAO = this.studyGroupClassDAO == null ? new StudyGroupClassDAO(sm.getDataSource())
				: studyGroupClassDAO;
		return studyGroupClassDAO;
	}

	public SubjectGroupMapDAO getSubjectGroupMapDAO() {
		subjectGroupMapDAO = this.subjectGroupMapDAO == null ? new SubjectGroupMapDAO(sm.getDataSource())
				: subjectGroupMapDAO;
		return subjectGroupMapDAO;
	}

	public StudyEventDAO getStudyEventDAO() {
		studyEventDAO = this.studyEventDAO == null ? new StudyEventDAO(sm.getDataSource()) : studyEventDAO;
		return studyEventDAO;
	}

	public StudyDAO getStudyDAO() {
		studyDAO = this.studyDAO == null ? new StudyDAO(sm.getDataSource()) : studyDAO;
		return studyDAO;
	}

	public EventCRFDAO getEventCRFDAO() {
		eventCRFDAO = this.eventCRFDAO == null ? new EventCRFDAO(sm.getDataSource()) : eventCRFDAO;
		return eventCRFDAO;
	}

	public EventDefinitionCRFDAO getEventDefinitionCRFDAO() {
		eventDefintionCRFDAO = this.eventDefintionCRFDAO == null ? new EventDefinitionCRFDAO(sm.getDataSource())
				: eventDefintionCRFDAO;
		return eventDefintionCRFDAO;
	}

	public StudyGroupDAO getStudyGroupDAO() {
		studyGroupDAO = this.studyGroupDAO == null ? new StudyGroupDAO(sm.getDataSource()) : studyGroupDAO;
		return studyGroupDAO;
	}

	public DiscrepancyNoteDAO getDiscrepancyNoteDAO() {
		discrepancyNoteDAO = this.discrepancyNoteDAO == null ? new DiscrepancyNoteDAO(sm.getDataSource())
				: discrepancyNoteDAO;
		return discrepancyNoteDAO;
	}

	public SDVUtil getSDVUtil() {
		return (SDVUtil) SpringServletAccess.getApplicationContext(context).getBean("sdvUtil");
	}

}
