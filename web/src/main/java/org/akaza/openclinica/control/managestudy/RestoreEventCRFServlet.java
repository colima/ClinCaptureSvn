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
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;

/**
 * Processes request of 'restore an event CRF from a event'
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({ "serial", "unchecked" })
@Component
public class RestoreEventCRFServlet extends Controller {

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {

		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin() || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		UserAccountBean currentUser = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		FormProcessor fp = new FormProcessor(request);
		int eventCRFId = fp.getInt("id");// eventCRFId
		int studySubId = fp.getInt("studySubId");// studySubjectId
		checkStudyLocked("ViewStudySubject?id" + studySubId, respage.getString("current_study_locked"), request,
				response);
		StudyEventDAO sedao = getStudyEventDAO();
		StudySubjectDAO subdao = getStudySubjectDAO();
		EventCRFDAO ecdao = getEventCRFDAO();
		StudyDAO sdao = getStudyDAO();

		if (eventCRFId == 0) {
			addPageMessage(respage.getString("please_choose_an_event_CRF_to_restore"), request);
			request.setAttribute("id", Integer.toString(studySubId));
			forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET, request, response);
		} else {
			EventCRFBean eventCRF = (EventCRFBean) ecdao.findByPK(eventCRFId);

			StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);
			// YW 11-07-2007, an event CRF could not be restored if its study
			// subject has been removed
			if (studySub.getStatus().isDeleted()) {
				addPageMessage(new StringBuilder("").append(resword.getString("event_CRF")).append(resterm.getString("could_not_be"))
						.append(resterm.getString("restored")).append(".").append(respage.getString("study_subject_has_been_deleted"))
						.toString(), request);
				request.setAttribute("id", Integer.toString(studySubId));
				forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET, request, response);
			}
			// YW
			request.setAttribute("studySub", studySub);

			// construct info needed on view event crf page
			CRFDAO cdao = getCRFDAO();
			CRFVersionDAO cvdao = getCRFVersionDAO();

			int crfVersionId = eventCRF.getCRFVersionId();
			CRFBean cb = cdao.findByVersionId(crfVersionId);
			eventCRF.setCrf(cb);

			CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(crfVersionId);
			eventCRF.setCrfVersion(cvb);

			// then get the definition so we can call
			// DisplayEventCRFBean.setFlags
			int studyEventId = eventCRF.getStudyEventId();

			StudyEventBean event = (StudyEventBean) sedao.findByPK(studyEventId);

			int studyEventDefinitionId = sedao.getDefinitionIdFromStudyEventId(studyEventId);
			StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(studyEventDefinitionId);
			event.setStudyEventDefinition(sed);
			request.setAttribute("event", event);

			EventDefinitionCRFDAO edcdao = getEventDefinitionCRFDAO();

			StudyBean study = (StudyBean) sdao.findByPK(studySub.getStudyId());
			EventDefinitionCRFBean edc = edcdao.findByStudyEventDefinitionIdAndCRFId(study, studyEventDefinitionId,
					cb.getId());

			DisplayEventCRFBean dec = new DisplayEventCRFBean();
			dec.setEventCRF(eventCRF);
			dec.setFlags(eventCRF, currentUser, currentRole, edc.isDoubleEntry());

			// find all item data
			ItemDataDAO iddao = getItemDataDAO();

			ArrayList<ItemDataBean> itemData = iddao.findAllByEventCRFId(eventCRF.getId());

			request.setAttribute("items", itemData);

			String action = request.getParameter("action");
			if ("confirm".equalsIgnoreCase(action)) {
				if (!eventCRF.getStatus().isDeleted()) {
					addPageMessage(respage.getString("this_event_CRF_avilable_for_study") + " "
									+ respage.getString("please_contact_sysadmin_for_more_information"), request);
					request.setAttribute("id", Integer.toString(studySubId));
					forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET, request, response);
					return;
				}

				request.setAttribute("displayEventCRF", dec);

				forwardPage(Page.RESTORE_EVENT_CRF, request, response);
			} else {
				logger.info("submit to restore the event CRF from study");

				getEventCRFService().restoreEventCRF(eventCRF, currentUser);

				boolean hasStarted = hasStarted(event, ecdao);

				event.setSubjectEventStatus(!hasStarted ? SubjectEventStatus.SCHEDULED
						: SubjectEventStatus.DATA_ENTRY_STARTED);
				event.setStatus(Status.AVAILABLE);
				event.setUpdater(currentUser);
				event.setUpdatedDate(new Date());
				sedao.update(event);

				String emailBody = new StringBuilder("").append(respage.getString("the_event_CRF")).append(cb.getName())
						.append(" ").append(respage.getString("has_been_restored_to_the_event")).append(" ")
						.append(event.getStudyEventDefinition().getName()).append(".").toString();

				addPageMessage(emailBody, request);
				sendEmail(emailBody, request);
				request.setAttribute("id", Integer.toString(studySubId));
				forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET, request, response);
			}
		}
	}

	private boolean hasStarted(StudyEventBean event, EventCRFDAO ecdao) {

		boolean hasStarted = false;
		ArrayList<EventCRFBean> eCRFs = (ArrayList<EventCRFBean>) ecdao.findAllByStudyEvent(event);
		for (EventCRFBean eCRF : eCRFs) {
			hasStarted = hasStarted || !eCRF.isNotStarted();
		}
		return hasStarted;
	}

	/**
	 * Send email to director and administrator
	 * 
	 * @param emailBody
	 *            String
	 */
	private void sendEmail(String emailBody, HttpServletRequest request) throws Exception {

		UserAccountBean ub = getUserAccountBean(request);

		logger.info("Sending email...");
		sendEmail(ub.getEmail().trim(), respage.getString("restore_event_CRF_to_event"), emailBody, false, request);
		// to admin
		sendEmail(EmailEngine.getAdminEmail(), respage.getString("restore_event_CRF_to_event"), emailBody, false,
				request);
		logger.info("Sending email done..");
	}

}
