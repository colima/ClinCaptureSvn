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
package org.akaza.openclinica.control.submit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.clinovo.util.DateUtil;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.AuditDAO;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemGroupDAO;
import org.akaza.openclinica.dao.submit.ItemGroupMetadataDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.service.DiscrepancyNoteUtil;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import com.clinovo.service.DiscrepancyDescriptionService;

/**
 * @author jxu
 * 
 *         View the detail of a discrepancy note on the data entry page
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Component
public class ViewDiscrepancyNoteServlet extends SpringServlet {

	public static final String INTERVIEWER = "interviewer";
	public static final String INTERVIEW_DATE = "interviewDate";
	public static final String LOCATION = "location";
	public static final String DATE_START = "date_start";
	public static final String DATE_END = "date_end";
	public static final String SHOW_STATUS = "showStatus";
	public static final String CAN_CLOSE = "canClose";

	public static final String ENTITY_ID = "id";
	public static final String ENTITY_TYPE = "name";
	public static final String ENTITY_COLUMN = "column";
	public static final String ENTITY_FIELD = "field";
	public static final String DIS_NOTES = "discrepancyNotes";
	public static final String LOCKED_FLAG = "isLocked"; // if an event crf is
	public static final String RES_STATUSES = "resolutionStatuses";
	public static final String RES_STATUSES2 = "resolutionStatuses2";
	public static final String DIS_TYPES = "discrepancyTypes";
	public static final String DIS_TYPES2 = "discrepancyTypes2";
	public static final String WHICH_RES_STATUSES = "whichResStatus";
	public static final String USER_ACCOUNTS = "userAccounts";
	public static final String BOX_DN_MAP = "boxDNMap";
	public static final String AUTOVIEWS = "autoViews";
	public static final String BOX_TO_SHOW = "boxToShow";
	public static final String VIEW_DN_LINK = "viewDNLink";
	public static final String FORM_DISCREPANCY_NOTES_NAME = "fdnotes";
	public static final String CAN_MONITOR = "canMonitor";
	public static final String FROM_BOX = "fromBox";

	private static final int THREE = 3;
	private static final int FOUR = 4;

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (mayViewData(ub, currentRole)) {
			return;
		}

		String exceptionName = getResException().getString("no_permission_to_create_discrepancy_note");
		String noAccessMessage = getResPage().getString("you_may_not_create_discrepancy_note")
				+ getResPage().getString("change_study_contact_sysadmin");

		addPageMessage(noAccessMessage, request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, exceptionName, "1");
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);
		EventCRFBean ecb = null;

		FormProcessor fp = new FormProcessor(request);
		int itemId = fp.getInt(CreateDiscrepancyNoteServlet.ITEM_ID);
		request.setAttribute(CreateDiscrepancyNoteServlet.ITEM_ID, itemId);

		sendDNTypesAndResStatusesLists(currentRole, request);

		// logic from CreateDiscrepancyNoteServlet
		request.setAttribute("unlock", "0");
		String monitor = fp.getString("study_monitor");

		if ("1".equalsIgnoreCase(monitor)) { // change to allow user to
			// enter note for all items,
			// not just blank items
			request.setAttribute(CAN_MONITOR, "1");
			request.setAttribute("study_monitor", monitor);
		} else {
			request.setAttribute(CAN_MONITOR, "0");
		}

		Boolean fromBox = fp.getBoolean(FROM_BOX);
		if (fromBox == null || !fromBox) {
			request.getSession().removeAttribute(BOX_TO_SHOW);
			request.getSession().removeAttribute(BOX_DN_MAP);
			request.getSession().removeAttribute(AUTOVIEWS);
		}

		Boolean refresh = fp.getBoolean("refresh");
		request.setAttribute("refresh", refresh + "");
		String ypos = fp.getString("y");
		if (ypos == null || ypos.length() == 0) {
			ypos = "0";
		}

		request.setAttribute("y", ypos);

		DiscrepancyNoteDAO dndao = getDiscrepancyNoteDAO();
		int entityId = fp.getInt(ENTITY_ID, true);
		String name = fp.getString(ENTITY_TYPE, true);

		String column = fp.getString(ENTITY_COLUMN, true);

		String field = fp.getString(ENTITY_FIELD, true);

		String isLocked = fp.getString(LOCKED_FLAG);

		if (!StringUtil.isBlank(isLocked) && "yes".equalsIgnoreCase(isLocked)) {

			request.setAttribute(LOCKED_FLAG, "yes");
		} else {
			request.setAttribute(LOCKED_FLAG, "no");
		}

		int stSubjectId = fp.getInt(CreateDiscrepancyNoteServlet.ST_SUBJECT_ID, true);
		StudySubjectDAO ssdao = new StudySubjectDAO(getDataSource());
		StudySubjectBean ssub = (StudySubjectBean) ssdao.findByPK(stSubjectId);
		request.setAttribute("noteSubject", ssub);

		ItemBean item = new ItemBean();
		if (itemId > 0) {
			ItemDAO idao = new ItemDAO(getDataSource());
			item = (ItemBean) idao.findByPK(itemId);
			request.setAttribute("item", item);
			request.setAttribute("entityName", item.getName());
		}
		ItemDataBean itemData = new ItemDataBean();
		int preUserId = 0;
		if (!StringUtil.isBlank(name)) {
			if ("itemData".equalsIgnoreCase(name)) {
				ItemDataDAO iddao = new ItemDataDAO(getDataSource());
				itemData = (ItemDataBean) iddao.findByPK(entityId);
				request.setAttribute("entityValue", itemData.getValue());
				request.setAttribute("entityName", item.getName());
				request.setAttribute("strErrMsg", request.getParameter("strErrMsg"));

				EventCRFDAO ecdao = new EventCRFDAO(getDataSource());
				ecb = (EventCRFBean) ecdao.findByPK(itemData.getEventCRFId());
				preUserId = ecb.getOwnerId() > 0 ? ecb.getOwnerId() : 0;
				request.setAttribute("entityCreatedDate", DateUtil.printDate(ecb.getCreatedDate(),
						getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
				request.setAttribute("eventCrfOwnerId", ecb.getOwnerId());

				if (field.isEmpty()) {
					ItemGroupDAO igdao = new ItemGroupDAO(getDataSource());
					ItemGroupMetadataDAO igmdao = new ItemGroupMetadataDAO(getDataSource());
					ItemGroupMetadataBean igmBean = (ItemGroupMetadataBean) igmdao.findByItemAndCrfVersion(
							itemData.getItemId(), ecb.getCRFVersionId());
					ItemGroupBean igBean = (ItemGroupBean) igdao.findByPK(igmBean.getItemGroupId());
					if (igmBean.isRepeatingGroup()) {
						field = igBean.getOid() + "_" + ((itemData.getOrdinal() - 1) + "input")
								+ itemData.getItemId();
					} else {
						field = "input" + itemData.getItemId();
					}
				}

				StudyEventDAO sed = new StudyEventDAO(getDataSource());
				StudyEventBean se = (StudyEventBean) sed.findByPK(ecb.getStudyEventId());

				StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(getDataSource());
				StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) seddao.findByPK(se
						.getStudyEventDefinitionId());

				se.setName(sedb.getName());
				request.setAttribute("studyEvent", se);

				CRFVersionDAO cvdao = new CRFVersionDAO(getDataSource());
				CRFVersionBean cv = (CRFVersionBean) cvdao.findByPK(ecb.getCRFVersionId());

				CRFDAO cdao = new CRFDAO(getDataSource());
				CRFBean crf = (CRFBean) cdao.findByPK(cv.getCrfId());
				request.setAttribute("crf", crf);

			} else if ("studySub".equalsIgnoreCase(name)) {
				SubjectDAO sdao = new SubjectDAO(getDataSource());
				SubjectBean sub = (SubjectBean) sdao.findByPK(ssub.getSubjectId());

				if (!StringUtil.isBlank(column)) {
					if ("enrollment_date".equalsIgnoreCase(column)) {
						if (ssub.getEnrollmentDate() != null) {
							request.setAttribute("entityValue", DateUtil.printDate(ssub.getEnrollmentDate(),
									getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
						}
						request.setAttribute("entityName", getResWord().getString("enrollment_date"));
					} else if ("gender".equalsIgnoreCase(column)) {
						String genderToDisplay = getResWord().getString("not_specified");
						if ('m' == sub.getGender()) {
							genderToDisplay = getResWord().getString("male");
						} else if ('f' == sub.getGender()) {
							genderToDisplay = getResWord().getString("female");
						}
						request.setAttribute("entityValue", genderToDisplay);
						request.setAttribute("entityName", getResWord().getString("gender"));
					} else if ("date_of_birth".equalsIgnoreCase(column)) {
						if (sub.getDateOfBirth() != null) {
							request.setAttribute("entityValue", DateUtil.printDate(sub.getDateOfBirth(),
									DateUtil.DatePattern.DATE, getLocale()));
						}
						request.setAttribute("entityName", getResWord().getString("date_of_birth"));
					} else if ("year_of_birth".equalsIgnoreCase(column)) {
						if (sub.getDateOfBirth() != null) {
							GregorianCalendar cal = new GregorianCalendar();
							cal.setTime(sub.getDateOfBirth());
							request.setAttribute("entityValue", String.valueOf(cal.get(Calendar.YEAR)));
						}
						request.setAttribute("entityName", getResWord().getString("year_of_birth"));
					} else if ("unique_identifier".equalsIgnoreCase(column)) {
						if (sub.getUniqueIdentifier() != null) {
							request.setAttribute("entityValue", sub.getUniqueIdentifier());
						}
						request.setAttribute("entityName", getResWord().getString("unique_identifier"));
					}
				}
				preUserId = ssub.getOwnerId() > 0 ? ssub.getOwnerId() : 0;
				request.setAttribute("entityCreatedDate", DateUtil.printDate(ssub.getCreatedDate(),
						getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));

			} else if ("subject".equalsIgnoreCase(name)) {

				SubjectDAO sdao = getSubjectDAO();
				SubjectBean sub = (SubjectBean) sdao.findByPK(entityId);
				// be caution: here for subject, noteSubject is SubjectBean and
				// label is unique_identifier
				sub.setLabel(sub.getUniqueIdentifier());
				request.setAttribute("noteSubject", sub);

				if (!StringUtil.isBlank(column)) {
					if ("gender".equalsIgnoreCase(column)) {
						String genderToDisplay = getResWord().getString("not_specified");
						if ('m' == sub.getGender()) {
							genderToDisplay = getResWord().getString("male");
						} else if ('f' == sub.getGender()) {
							genderToDisplay = getResWord().getString("female");
						}
						request.setAttribute("entityValue", genderToDisplay);
						request.setAttribute("entityName", getResWord().getString("gender"));
					} else if ("date_of_birth".equalsIgnoreCase(column)) {
						if (sub.getDateOfBirth() != null) {
							request.setAttribute("entityValue", DateUtil.printDate(sub.getDateOfBirth(),
									DateUtil.DatePattern.DATE, getLocale()));
						}
						request.setAttribute("entityName", getResWord().getString("date_of_birth"));
					} else if ("year_of_birth".equalsIgnoreCase(column)) {
						if (sub.getDateOfBirth() != null) {
							GregorianCalendar cal = new GregorianCalendar();
							cal.setTime(sub.getDateOfBirth());
							request.setAttribute("entityValue", String.valueOf(cal.get(Calendar.YEAR)));
						}
						request.setAttribute("entityName", getResWord().getString("year_of_birth"));
					} else if ("unique_identifier".equalsIgnoreCase(column)) {
						request.setAttribute("entityValue", sub.getUniqueIdentifier());
						request.setAttribute("entityName", getResWord().getString("unique_identifier"));
					}
				}
				preUserId = sub.getOwnerId() > 0 ? sub.getOwnerId() : 0;
				request.setAttribute("entityCreatedDate", DateUtil.printDate(sub.getCreatedDate(),
						getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));

			} else if ("studyEvent".equalsIgnoreCase(name)) {

				StudyEventDAO sed = new StudyEventDAO(getDataSource());
				StudyEventBean se = (StudyEventBean) sed.findByPK(entityId);
				StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(getDataSource());
				StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) seddao.findByPK(se
						.getStudyEventDefinitionId());

				se.setName(sedb.getName());
				request.setAttribute("studyEvent", se);

				if (!StringUtil.isBlank(column)) {
					if ("location".equalsIgnoreCase(column)) {
						request.setAttribute("entityValue", se.getLocation());
						request.setAttribute("entityName", getResWord().getString("location"));
					} else if ("date_start".equalsIgnoreCase(column)) {
						if (se.getDateStarted() != null) {
							request.setAttribute("entityValue", DateUtil.printDate(se.getDateStarted(),
									getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
						}
						request.setAttribute("entityName", getResWord().getString("start_date"));

					} else if ("date_end".equalsIgnoreCase(column)) {
						if (se.getDateEnded() != null) {
							request.setAttribute("entityValue", DateUtil.printDate(se.getDateEnded(),
									getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
						}
						request.setAttribute("entityName", getResWord().getString("end_date"));
					}
				}
				preUserId = se.getOwnerId() > 0 ? se.getOwnerId() : 0;
				request.setAttribute("entityCreatedDate", DateUtil.printDate(se.getCreatedDate(),
						getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));

			} else if ("eventCrf".equalsIgnoreCase(name)) {
				EventCRFDAO ecdao = getEventCRFDAO();
				ecb = (EventCRFBean) ecdao.findByPK(entityId);
				if (!StringUtil.isBlank(column)) {
					if ("date_interviewed".equals(column)) {
						if (ecb.getDateInterviewed() != null) {
							request.setAttribute("entityValue", DateUtil.printDate(ecb.getDateInterviewed(),
									getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
						}
						request.setAttribute("entityName", getResWord().getString("date_interviewed"));
					} else if ("interviewer_name".equals(column)) {
						request.setAttribute("entityValue", ecb.getInterviewerName());
						request.setAttribute("entityName", getResWord().getString("interviewer_name"));
					}
				}

				setupStudyEventCRFAttributes(ecb, request);

				preUserId = ecb.getOwnerId() > 0 ? ecb.getOwnerId() : 0;
				request.setAttribute("entityCreatedDate", DateUtil.printDate(ecb.getCreatedDate(),
						getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
			}

		}
		boolean writeToDB = fp.getBoolean(CreateDiscrepancyNoteServlet.WRITE_TO_DB, true);

		HashMap<Integer, Integer> autoviews = (HashMap<Integer, Integer>) request.getSession().getAttribute(AUTOVIEWS);
		autoviews = autoviews == null ? new HashMap<Integer, Integer>() : autoviews;
		HashMap<Integer, DiscrepancyNoteBean> boxDNMap = (HashMap<Integer, DiscrepancyNoteBean>) request.getSession()
				.getAttribute(BOX_DN_MAP);
		if (boxDNMap == null || !boxDNMap.containsKey(0)) {
			boxDNMap = new HashMap<Integer, DiscrepancyNoteBean>();
			// initialize dn for a new thread
			DiscrepancyNoteBean dnb = new DiscrepancyNoteBean();
			if (currentRole.getRole().equals(Role.CLINICAL_RESEARCH_COORDINATOR)
					|| currentRole.getRole().equals(Role.INVESTIGATOR)
					|| currentRole.getRole().equals(Role.STUDY_EVALUATOR)) {
				dnb.setDiscrepancyNoteTypeId(DiscrepancyNoteType.ANNOTATION.getId());
				dnb.setResolutionStatusId(ResolutionStatus.NOT_APPLICABLE.getId());
				autoviews.put(0, 0);
			} else {
				dnb.setDiscrepancyNoteTypeId(DiscrepancyNoteType.QUERY.getId());
				dnb.setAssignedUserId(preUserId);
				autoviews.put(0, 1);
			}
			boxDNMap.put(0, dnb);
		} else if (boxDNMap.containsKey(0)) {
			int dnTypeId = boxDNMap.get(0).getDiscrepancyNoteTypeId();
			autoviews.put(0, (dnTypeId == THREE ? 1 : 0));
		}
		if (boxDNMap.containsKey(0)) {
			int dnTypeId0 = boxDNMap.get(0).getDiscrepancyNoteTypeId();
			if (dnTypeId0 == 2 || dnTypeId0 == FOUR) {
				request.setAttribute("typeID0", dnTypeId0 + "");
			}
		}

		request.setAttribute("study_monitor", monitor);
		request.setAttribute(ENTITY_ID, entityId + "");
		request.setAttribute(ENTITY_TYPE, name);
		request.setAttribute(ENTITY_FIELD, field);
		request.setAttribute(ENTITY_COLUMN, column);

		request.setAttribute(CreateDiscrepancyNoteServlet.WRITE_TO_DB, writeToDB ? "1" : "0");

		List<DiscrepancyNoteBean> notes = (List<DiscrepancyNoteBean>) dndao.findAllByEntityAndColumnAndStudy(
				currentStudy, name, entityId, column);
		notes = filterNotesByUserRole(notes, request);

		if (notes.size() > 0) {
			manageStatuses(request, field);
			CreateDiscrepancyNoteServlet.checkSubjectInCorrectStudy(name, ssub, currentStudy, getDataSource(), logger,
					request);
		}
		// Update the resolution status of parent disc
		// notes based
		// on the status of child notes

		FormDiscrepancyNotes newNotes = (FormDiscrepancyNotes) request.getSession().getAttribute(
				FORM_DISCREPANCY_NOTES_NAME);

		Map<Integer, DiscrepancyNoteBean> noteTree = new LinkedHashMap<Integer, DiscrepancyNoteBean>();

		if (newNotes != null && !newNotes.getNotes(field).isEmpty()) {
			List<DiscrepancyNoteBean> newFieldNotes = newNotes.getNotes(field);

			for (DiscrepancyNoteBean note : newFieldNotes) {
				note.setLastUpdator(ub);
				note.setLastDateUpdated(new Date());
				note.setDisType(DiscrepancyNoteType.get(note.getDiscrepancyNoteTypeId()));
				note.setResStatus(ResolutionStatus.get(note.getResolutionStatusId()));
				note.setSaved(false);
				if (itemId > 0) {
					note.setEntityName(item.getName());
					note.setEntityValue(itemData.getValue());
				}
				note.setSubjectName(ssub.getName());
				note.setEntityType(name);

				int pId = note.getParentDnId();
				if (pId == 0) { // we can only keep one unsaved note because
					// note.id == 0
					noteTree.put(note.getId(), note);
				}
				if (note.getDiscrepancyNoteTypeId() == DiscrepancyNoteType.REASON_FOR_CHANGE.getId()
						|| note.getDiscrepancyNoteTypeId() == DiscrepancyNoteType.FAILEDVAL.getId()) {
					CreateDiscrepancyNoteServlet.manageReasonForChangeState(request.getSession(), field);
				}
			}
			for (DiscrepancyNoteBean note : newFieldNotes) {
				int pId = note.getParentDnId();
				if (pId > 0) {
					note.setSaved(false);
					note.setLastUpdator(ub);
					note.setLastDateUpdated(new Date());

					note.setEntityName(item.getName());
					note.setSubjectName(ssub.getName());
					note.setEntityType(name);

					note.setDisType(DiscrepancyNoteType.get(note.getDiscrepancyNoteTypeId()));
					note.setResStatus(ResolutionStatus.get(note.getResolutionStatusId()));
					DiscrepancyNoteBean parent = noteTree.get(pId);
					if (parent != null) {
						parent.getChildren().add(note);
					}
				}
			}
		}

		UserAccountDAO udao = getUserAccountDAO();
		HashMap<Integer, String> fvcInitAssigns = new HashMap<Integer, String>();
		for (DiscrepancyNoteBean note : notes) {
			note.setColumn(column);
			note.setEntityId(entityId);
			note.setEntityType(name);
			note.setField(field);
			Date lastUpdatedDate = note.getCreatedDate();
			UserAccountBean lastUpdator = (UserAccountBean) udao.findByPK(note.getOwnerId());
			note.setLastUpdator(lastUpdator);
			note.setLastDateUpdated(lastUpdatedDate);
			int pId = note.getParentDnId();
			note.setDisType(DiscrepancyNoteType.get(note.getDiscrepancyNoteTypeId()));
			note.setResStatus(ResolutionStatus.get(note.getResolutionStatusId()));
			if (pId == 0) {
				noteTree.put(note.getId(), note);
			}
		}

		for (DiscrepancyNoteBean note : notes) {
			int pId = note.getParentDnId();

			if (itemId > 0) {
				note.setEntityName(item.getName());
				note.setEntityValue(itemData.getValue());
			}
			note.setSubjectName(ssub.getName());
			note.setEntityType(name);

			Date lastUpdatedDate = note.getCreatedDate();
			UserAccountBean lastUpdator = (UserAccountBean) udao.findByPK(note.getOwnerId());
			note.setLastUpdator(lastUpdator);
			note.setLastDateUpdated(lastUpdatedDate);
			note.setDisType(DiscrepancyNoteType.get(note.getDiscrepancyNoteTypeId()));
			note.setResStatus(ResolutionStatus.get(note.getResolutionStatusId()));
			if (pId > 0) {
				DiscrepancyNoteBean parent = noteTree.get(pId);
				if (parent != null) {
					parent.getChildren().add(note);
					if (!note.getCreatedDate().before(parent.getLastDateUpdated())) {
						parent.setLastDateUpdated(note.getCreatedDate());
					}

					if (note.getDiscrepancyNoteTypeId() == DiscrepancyNoteType.FAILEDVAL.getId()
							&& note.getAssignedUserId() > 0) {
						int ownerId = note.getOwnerId();
						if (fvcInitAssigns.containsKey(pId)) {
							String f = fvcInitAssigns.get(pId);
							String fn = note.getId() + "." + ownerId;
							if (fn.compareTo(f) < 0) {
								fvcInitAssigns.put(pId, fn);
							}
						} else {
							fvcInitAssigns.put(pId, note.getId() + "." + ownerId);
						}
					}
				}
			}
		}

		Set parents = noteTree.keySet();
		for (Object parent : parents) {
			Integer key = (Integer) parent;
			DiscrepancyNoteBean note = noteTree.get(key);
			note.setNumChildren(note.getChildren().size());
			note.setEntityType(name);

			if (!boxDNMap.containsKey(key)) {
				DiscrepancyNoteBean dn = new DiscrepancyNoteBean();
				dn.setId(key);
				int dnTypeId = note.getDiscrepancyNoteTypeId();
				dn.setDiscrepancyNoteTypeId(dnTypeId);
				if (dnTypeId == THREE) { // Query
					dn.setAssignedUserId(note.getOwnerId());
				} else if (dnTypeId == 1) { // FVC
					if (fvcInitAssigns.containsKey(key)) {
						String[] s = fvcInitAssigns.get(key).split("\\.");
						int i = Integer.parseInt(s.length == 2 ? s[1].trim() : "0");
						dn.setAssignedUserId(i);
					}
				}
				Role r = currentRole.getRole();
				if (r.equals(Role.CLINICAL_RESEARCH_COORDINATOR) || r.equals(Role.INVESTIGATOR)
						|| r.equals(Role.STUDY_EVALUATOR)) {
					if (dn.getDiscrepancyNoteTypeId() == DiscrepancyNoteType.QUERY.getId()
							&& note.getResStatus().getId() == ResolutionStatus.UPDATED.getId()) {
						dn.setResolutionStatusId(ResolutionStatus.UPDATED.getId());
					} else {
						dn.setResolutionStatusId(ResolutionStatus.RESOLVED.getId());
					}
					if (dn.getAssignedUserId() > 0) {
						autoviews.put(key, 1);
					} else {
						autoviews.put(key, 0);
					}
				} else {
					if (note.getResStatus().getId() == ResolutionStatus.RESOLVED.getId()) {
						dn.setResolutionStatusId(ResolutionStatus.CLOSED.getId());
					} else if (note.getResStatus().getId() == ResolutionStatus.CLOSED.getId()) {
						dn.setResolutionStatusId(ResolutionStatus.UPDATED.getId());
					} else if (Role.isMonitor(r)) {
						dn.setResolutionStatusId(ResolutionStatus.UPDATED.getId());
					} else if (dn.getDiscrepancyNoteTypeId() == 1) {
						dn.setResolutionStatusId(ResolutionStatus.RESOLVED.getId());
					} else {
						dn.setResolutionStatusId(ResolutionStatus.UPDATED.getId());
					}
					autoviews.put(key, 1);
					if (!(dn.getAssignedUserId() > 0)) {
						dn.setAssignedUserId(preUserId);
					}
				}

				boxDNMap.put(key, dn);
			}
		}
		request.getSession().setAttribute(BOX_DN_MAP, boxDNMap);
		request.getSession().setAttribute(AUTOVIEWS, autoviews);
		// noteTree is a Hashmap mapping note id to a parent note, with all the
		// child notes
		// stored in the children List.
		// Make sure the parent note has an updated resolution status
		// and
		// updated date
		fixStatusUpdatedDate(noteTree);
		request.setAttribute(DIS_NOTES, noteTree);

		ArrayList<StudyUserRoleBean> userAccounts = DiscrepancyNoteUtil.generateUserAccounts(ssub.getId(),
				currentStudy, udao, getStudyDAO(), ecb, getEventDefinitionCRFDAO());

		request.setAttribute(USER_ACCOUNTS, userAccounts);
		request.setAttribute(VIEW_DN_LINK, this.getPageServletFileName(request));

		prepareRepeatingInfo(name, entityId, request);

		if (!INTERVIEWER.equals(field) && !INTERVIEW_DATE.equals(field) && !LOCATION.equals(field)
				&& !DATE_START.equals(field) && !DATE_END.equals(field)) {

			AuditDAO adao = new AuditDAO(getDataSource());
			if (name.equalsIgnoreCase("studysub")) {
				name = "study_subject";
			} else if (name.equalsIgnoreCase("eventcrf")) {
				name = "event_crf";
			} else if (name.equalsIgnoreCase("studyevent")) {
				name = "study_event";
			} else if (name.equalsIgnoreCase("itemdata")) {
				name = "item_data";
			}
			ArrayList itemAuditEvents = adao.findItemAuditEvents(entityId, name);
			request.setAttribute("itemAudits", itemAuditEvents);
		}

		if (!"XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
			forwardPage(Page.VIEW_DISCREPANCY_NOTE, request, response);
		} else {
			request.setAttribute("responseMessage", getResPage().getString("error_in_data"));
			forwardPage(Page.ADD_ONE_DISCREPANCY_NOTE_DIV, request, response);
		}
	}

	private void setupStudyEventCRFAttributes(EventCRFBean eventCRFBean, HttpServletRequest request) {
		StudyEventDAO sed = new StudyEventDAO(getDataSource());
		StudyEventBean studyEventBean = (StudyEventBean) sed.findByPK(eventCRFBean.getStudyEventId());

		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(getDataSource());
		StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) seddao.findByPK(studyEventBean
				.getStudyEventDefinitionId());

		studyEventBean.setName(sedb.getName());
		request.setAttribute("studyEvent", studyEventBean);

		CRFVersionDAO cvdao = new CRFVersionDAO(getDataSource());
		CRFVersionBean cv = (CRFVersionBean) cvdao.findByPK(eventCRFBean.getCRFVersionId());

		CRFDAO cdao = new CRFDAO(getDataSource());
		CRFBean crf = (CRFBean) cdao.findByPK(cv.getCrfId());
		request.setAttribute("crf", crf);
	}

	/**
	 * Update a parent DiscrepancyNoteBean's resolution status and updated date to that of the latest child
	 * DiscrepancyNoteBean.
	 * 
	 * @param noteTree
	 *            A HashMap of an Integer representing the DiscrepancyNoteBean, pointing to a parent
	 *            DiscrepancyNoteBean.
	 */
	private void fixStatusUpdatedDate(Map<Integer, DiscrepancyNoteBean> noteTree) {
		if (noteTree == null || noteTree.isEmpty()) {
			return;
		}
		// foreach parent stored in the Map
		ArrayList<DiscrepancyNoteBean> children;
		for (DiscrepancyNoteBean parent : noteTree.values()) {
			// The parent bean will contain in its children ArrayList property
			// the "automatic" child that is generated in the
			// database at creation, plus any "real" child notes.
			// first sort the beans so we can grab the last child
			Collections.sort(parent.getChildren());
			children = parent.getChildren();
			if (children.size() > 0) {
				DiscrepancyNoteBean lastChild = children.get(children.size() - 1);
				if (lastChild != null) {
					Date lastUpdatedDate = lastChild.getCreatedDate();
					UserAccountDAO userDAO = getUserAccountDAO();
					UserAccountBean lastUpdator = (UserAccountBean) userDAO.findByPK(lastChild.getOwnerId());
					parent.setLastUpdator(lastUpdator);
					parent.setLastDateUpdated(lastUpdatedDate);
				}
			}
		}

		// Sorting parent notes according to the last child being updated. The parent who has the most recently updated
		// child gets into the top.
		List<DiscrepancyNoteBean> parentNotes = new ArrayList<DiscrepancyNoteBean>(noteTree.values());
		Collections.sort(parentNotes, new Comparator<DiscrepancyNoteBean>() {
			public int compare(DiscrepancyNoteBean dn1, DiscrepancyNoteBean dn2) {
				ArrayList<DiscrepancyNoteBean> cn1 = dn1.getChildren();
				DiscrepancyNoteBean child1 = cn1.size() > 0 ? cn1.get(cn1.size() - 1) : dn1;
				ArrayList<DiscrepancyNoteBean> cn2 = dn2.getChildren();
				DiscrepancyNoteBean child2 = cn2.size() > 0 ? cn2.get(cn2.size() - 1) : dn2;
				return child1.getId() > child2.getId() ? -1 : 1;
			}
		});
		noteTree.clear();
		for (DiscrepancyNoteBean dn : parentNotes) {
			noteTree.put(dn.getId(), dn);
		}
	}
	
	private void manageStatuses(HttpServletRequest request, String field) {
		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);
		DiscrepancyDescriptionService dDescriptionService = (DiscrepancyDescriptionService) SpringServletAccess
				.getApplicationContext(getServletContext()).getBean("discrepancyDescriptionService");

		Map<String, String> additionalParameters = CreateDiscrepancyNoteServlet.getMapWithParameters(field, request);

		SessionManager sm = getSessionManager(request);
		boolean isInError = !additionalParameters.isEmpty() && "1".equals(additionalParameters.get("isInError"));
		boolean isRFC = !additionalParameters.isEmpty()
				&& CreateDiscrepancyNoteServlet.calculateIsRFC(additionalParameters, request, sm);
		String originJSP = request.getParameter("originJSP") == null ? "" : request.getParameter("originJSP");
		request.setAttribute("originJSP", originJSP);
		request.setAttribute("isRFC", isRFC);
		request.setAttribute("isInError", isInError);

		request.setAttribute("dDescriptionsMap", dDescriptionService.getAssignedToStudySortedDescriptions(currentStudy));

		if (currentRole.getRole().equals(Role.CLINICAL_RESEARCH_COORDINATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR)
				|| currentRole.getRole().equals(Role.STUDY_EVALUATOR)) {
			request.setAttribute(SHOW_STATUS, false);
			request.setAttribute(CAN_CLOSE, false);
			request.setAttribute(DIS_TYPES, Arrays.asList(DiscrepancyNoteType.ANNOTATION));
			request.setAttribute(RES_STATUSES, Arrays.asList(ResolutionStatus.UPDATED, ResolutionStatus.NOT_APPLICABLE));
			request.setAttribute(DIS_TYPES2, Arrays.asList(DiscrepancyNoteType.ANNOTATION));
			request.setAttribute(RES_STATUSES2,
					Arrays.asList(ResolutionStatus.UPDATED, ResolutionStatus.NOT_APPLICABLE));
		} else {
			request.setAttribute(SHOW_STATUS, true);
			request.setAttribute(CAN_CLOSE, true);

			request.setAttribute(RES_STATUSES, Arrays.asList(ResolutionStatus.UPDATED, ResolutionStatus.CLOSED));
			request.setAttribute(DIS_TYPES, DiscrepancyNoteType.simpleList);
			request.setAttribute(DIS_TYPES2, DiscrepancyNoteType.simpleList);
			if (isRFC) {
				request.setAttribute(DIS_TYPES, Arrays.asList(DiscrepancyNoteType.ANNOTATION));
				request.setAttribute(DIS_TYPES2, Arrays.asList(DiscrepancyNoteType.ANNOTATION));
			} else {
				request.setAttribute(DIS_TYPES, DiscrepancyNoteType.simpleList);
				request.setAttribute(DIS_TYPES2, DiscrepancyNoteType.simpleList);
			}
			request.setAttribute(RES_STATUSES2, ResolutionStatus.SIMPLE_LIST);
		}
	}

	private void sendDNTypesAndResStatusesLists(StudyUserRoleBean currentRole, HttpServletRequest request) {
		request.setAttribute(DIS_TYPES, DiscrepancyNoteType.list);
		if (currentRole.getRole().equals(Role.CLINICAL_RESEARCH_COORDINATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR)
				|| currentRole.getRole().equals(Role.STUDY_EVALUATOR)) {
			ArrayList<ResolutionStatus> resStatuses = new ArrayList();
			resStatuses.add(ResolutionStatus.UPDATED);
			request.setAttribute(RES_STATUSES, resStatuses);
			request.setAttribute(WHICH_RES_STATUSES, "22");
			ArrayList<ResolutionStatus> resStatuses2 = new ArrayList<ResolutionStatus>();
			resStatuses2.add(ResolutionStatus.OPEN);
			request.setAttribute(RES_STATUSES2, resStatuses2);
			ArrayList types2 = DiscrepancyNoteType.toArrayList();
			types2.remove(DiscrepancyNoteType.QUERY);
			request.setAttribute(DIS_TYPES2, types2);
		} else if (Role.isMonitor(currentRole.getRole())) {
			ArrayList<ResolutionStatus> resStatuses = new ArrayList();
			resStatuses.add(ResolutionStatus.OPEN);
			resStatuses.add(ResolutionStatus.UPDATED);
			resStatuses.add(ResolutionStatus.CLOSED);
			request.setAttribute(RES_STATUSES, resStatuses);
			request.setAttribute(WHICH_RES_STATUSES, "1");
			ArrayList<DiscrepancyNoteType> types2 = new ArrayList<DiscrepancyNoteType>();
			types2.add(DiscrepancyNoteType.QUERY);
			request.setAttribute(DIS_TYPES2, types2);
		} else {
			request.setAttribute(RES_STATUSES, ResolutionStatus.SIMPLE_LIST);
			// it's for parentDNId is null or 0 and FVC
			request.setAttribute(WHICH_RES_STATUSES, "2");
			ArrayList<ResolutionStatus> resStatuses2 = new ArrayList<ResolutionStatus>();
			resStatuses2.add(ResolutionStatus.OPEN);
			request.setAttribute(RES_STATUSES2, resStatuses2);
		}
	}

	private void prepareRepeatingInfo(String name, int entityId, HttpServletRequest request) {
		Map<String, String> repeatingInfoMap = DiscrepancyNoteUtil.prepareRepeatingInfoMap(name, entityId,
				getItemDataDAO(), getEventCRFDAO(), getStudyEventDAO(), getItemGroupMetadataDAO(),
				getStudyEventDefinitionDAO());
		request.setAttribute("itemDataOrdinal", repeatingInfoMap.get("itemDataOrdinal"));
		request.setAttribute("studyEventOrdinal", repeatingInfoMap.get("studyEventOrdinal"));
	}
}
