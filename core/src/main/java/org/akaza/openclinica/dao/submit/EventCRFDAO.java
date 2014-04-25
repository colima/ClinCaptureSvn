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

package org.akaza.openclinica.dao.submit;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.EventCRFSDVFilter;
import org.akaza.openclinica.dao.EventCRFSDVSort;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * <p/>
 * EventCRFDAO.java, data access object for an instance of an event being filled out on a subject. Was originally
 * individual_instrument table in OpenClinica v.1.
 * 
 * @author thickerson
 *         <p/>
 *         TODO test create and update first thing
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class EventCRFDAO extends AuditableEntityDAO {

	private void setQueryNames() {
		this.findByPKAndStudyName = "findByPKAndStudy";
		this.getCurrentPKName = "getCurrentPK";
	}

	public EventCRFDAO(DataSource ds) {
		super(ds);
		setQueryNames();
	}

	public EventCRFDAO(DataSource ds, Connection con) {
		super(ds, con);
		setQueryNames();
	}

	public EventCRFDAO(DataSource ds, DAODigester digester) {
		super(ds);
		this.digester = digester;
		setQueryNames();
	}

	// This constructor sets up the Locale for JUnit tests; see the locale
	// member variable in EntityDAO, and its initializeI18nStrings() method
	public EventCRFDAO(DataSource ds, DAODigester digester, Locale locale) {

		this(ds, digester);
		this.locale = locale;
	}

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_EVENTCRF;
	}

	@Override
	public void setTypesExpected() {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.INT);
		this.setTypeExpected(3, TypeNames.INT);
		this.setTypeExpected(4, TypeNames.DATE);
		this.setTypeExpected(5, TypeNames.STRING);
		this.setTypeExpected(6, TypeNames.INT);
		this.setTypeExpected(7, TypeNames.INT);
		this.setTypeExpected(8, TypeNames.STRING);// annotations
		this.setTypeExpected(9, TypeNames.TIMESTAMP);// completed
		this.setTypeExpected(10, TypeNames.INT);// validator id
		this.setTypeExpected(11, TypeNames.DATE);// date validate
		this.setTypeExpected(12, TypeNames.TIMESTAMP);// date val. completed
		this.setTypeExpected(13, TypeNames.STRING);
		this.setTypeExpected(14, TypeNames.STRING);
		this.setTypeExpected(15, TypeNames.INT);// owner id
		this.setTypeExpected(16, TypeNames.DATE);
		this.setTypeExpected(17, TypeNames.INT);// subject id
		this.setTypeExpected(18, TypeNames.DATE);// date updated
		this.setTypeExpected(19, TypeNames.INT);// updater
		this.setTypeExpected(20, TypeNames.BOOL);// electronic_signature_status
		this.setTypeExpected(21, TypeNames.BOOL);// sdv_status
		this.setTypeExpected(22, TypeNames.INT);// old_status
		this.setTypeExpected(23, TypeNames.INT); // sdv_update_id
		this.setTypeExpected(24, TypeNames.BOOL); // not_started

	}

	public EntityBean update(EntityBean eb) {
		return update(eb, null);
	}

	public EntityBean update(EntityBean eb, Connection con) {
		EventCRFBean ecb = (EventCRFBean) eb;

		ecb.setActive(false);

		HashMap variables = new HashMap();
		HashMap nullVars = new HashMap();
		variables.put(1, ecb.getStudyEventId());
		variables.put(2, ecb.getCRFVersionId());
		if (ecb.getDateInterviewed() == null) {
			nullVars.put(3, Types.DATE);
			variables.put(3, null);
		} else {
			variables.put(3, ecb.getDateInterviewed());
		}
		variables.put(4, ecb.getInterviewerName());
		variables.put(5, ecb.getCompletionStatusId());
		variables.put(6, ecb.getStatus().getId());
		variables.put(7, ecb.getAnnotations());
		if (ecb.getDateCompleted() == null) {
			nullVars.put(8, Types.TIMESTAMP);
			variables.put(8, null);
		} else {
			variables.put(8, new java.sql.Timestamp(ecb.getDateCompleted().getTime()));
		}

		variables.put(9, ecb.getValidatorId());

		if (ecb.getDateValidate() == null) {
			nullVars.put(10, Types.DATE);
			variables.put(10, null);
		} else {
			variables.put(10, ecb.getDateValidate());
		}

		if (ecb.getDateValidateCompleted() == null) {
			nullVars.put(11, Types.TIMESTAMP);
			variables.put(11, null);
		} else {
			variables.put(11, new Timestamp(ecb.getDateValidateCompleted().getTime()));
		}
		variables.put(12, ecb.getValidatorAnnotations());
		variables.put(13, ecb.getValidateString());
		variables.put(14, ecb.getStudySubjectId());
		variables.put(15, ecb.getUpdaterId());
		variables.put(16, ecb.isElectronicSignatureStatus());

		variables.put(17, ecb.isSdvStatus());
		if (ecb.getOldStatus() != null && ecb.getOldStatus().getId() > 0) {
			variables.put(18, ecb.getOldStatus().getId());
		} else {
			variables.put(18, 0);
		}
		variables.put(19, ecb.getSdvUpdateId());
		variables.put(20, ecb.isNotStarted());
		variables.put(21, ecb.getId());
		this.execute(digester.getQuery("update"), variables, nullVars, con);
		if (isQuerySuccessful()) {
			ecb.setActive(true);
		}

		return ecb;
	}

	public void markComplete(EventCRFBean ecb, boolean ide) {
		markComplete(ecb, ide, null);
	}

	public void markComplete(EventCRFBean ecb, boolean ide, Connection con) {
		HashMap variables = new HashMap();
		variables.put(1, ecb.getId());

		if (ide) {
			execute(digester.getQuery("markCompleteIDE"), variables, con);
		} else {
			execute(digester.getQuery("markCompleteDDE"), variables, con);
		}
	}

	public EntityBean create(EntityBean eb) {
		EventCRFBean ecb = (EventCRFBean) eb;
		HashMap variables = new HashMap();
		HashMap nullVars = new HashMap();
		variables.put(1, ecb.getStudyEventId());
		variables.put(2, ecb.getCRFVersionId());

		Date interviewed = ecb.getDateInterviewed();
		if (interviewed != null) {
			variables.put(3, ecb.getDateInterviewed());
		} else {
			variables.put(3, null);
			nullVars.put(3, Types.DATE);
		}
		logger.info("created: ecb.getInterviewerName()" + ecb.getInterviewerName());
		variables.put(4, ecb.getInterviewerName());

		variables.put(5, ecb.getCompletionStatusId());
		variables.put(6, ecb.getStatus().getId());
		variables.put(7, ecb.getAnnotations());
		variables.put(8, ecb.getOwnerId());
		variables.put(9, ecb.getStudySubjectId());
		variables.put(10, ecb.getValidateString());
		variables.put(11, ecb.getValidatorAnnotations());
		variables.put(12, ecb.isNotStarted());

		executeWithPK(digester.getQuery("create"), variables, nullVars);
		if (isQuerySuccessful()) {
			ecb.setId(getLatestPK());
		}

		return ecb;
	}

	public Object getEntityFromHashMap(HashMap hm) {
		EventCRFBean eb = new EventCRFBean();
		this.setEntityAuditInformation(eb, hm);

		eb.setId((Integer) hm.get("event_crf_id"));
		eb.setStudyEventId((Integer) hm.get("study_event_id"));
		eb.setCRFVersionId((Integer) hm.get("crf_version_id"));
		eb.setDateInterviewed((Date) hm.get("date_interviewed"));
		eb.setInterviewerName((String) hm.get("interviewer_name"));
		eb.setCompletionStatusId((Integer) hm.get("completion_status_id"));
		eb.setAnnotations((String) hm.get("annotations"));
		eb.setDateCompleted((Date) hm.get("date_completed"));
		eb.setValidatorId((Integer) hm.get("validator_id"));
		eb.setDateValidate((Date) hm.get("date_validate"));
		eb.setDateValidateCompleted((Date) hm.get("date_validate_completed"));
		eb.setValidatorAnnotations((String) hm.get("validator_annotations"));
		eb.setValidateString((String) hm.get("validate_string"));
		eb.setStudySubjectId((Integer) hm.get("study_subject_id"));
		eb.setSdvStatus((Boolean) hm.get("sdv_status"));
		Integer oldStatusId = (Integer) hm.get("old_status_id");
		eb.setOldStatus(Status.get(oldStatusId));
		eb.setNotStarted((Boolean) hm.get("not_started"));

		return eb;
	}

	public Collection findAll() {
		this.setTypesExpected();
		ArrayList alist = this.select(digester.getQuery("findAll"));
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			EventCRFBean eb = (EventCRFBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	public EntityBean findByPK(int ID) {
		EventCRFBean eb = new EventCRFBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, ID);

		String sql = digester.getQuery("findByPK");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (EventCRFBean) this.getEntityFromHashMap((HashMap) it.next());
		}

		return eb;
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
		return new ArrayList();
	}

	public ArrayList findAllByStudyEvent(StudyEventBean studyEvent) {
		HashMap variables = new HashMap();
		variables.put(1, studyEvent.getId());

		return executeFindAllQuery("findAllByStudyEvent", variables);
	}

	public ArrayList findAllStartedByStudyEvent(StudyEventBean studyEvent) {
		HashMap variables = new HashMap();
		variables.put(1, studyEvent.getId());

		return executeFindAllQuery("findAllStartedByStudyEvent", variables);
	}

	public ArrayList findAllByStudyEventAndStatus(StudyEventBean studyEvent, Status status) {
		HashMap variables = new HashMap();
		variables.put(1, studyEvent.getId());
		variables.put(2, status.getId());
		return executeFindAllQuery("findAllByStudyEventAndStatus", variables);
	}

	public ArrayList<EventCRFBean> findAllByStudySubject(int studySubjectId) {
		HashMap variables = new HashMap();
		variables.put(1, studySubjectId);

		return executeFindAllQuery("findAllByStudySubject", variables);
	}

	public ArrayList findAllByStudyEventAndCrfOrCrfVersionOid(StudyEventBean studyEvent, String crfVersionOrCrfOID) {
		HashMap variables = new HashMap();
		variables.put(1, studyEvent.getId());
		variables.put(2, crfVersionOrCrfOID);
		variables.put(3, crfVersionOrCrfOID);

		return executeFindAllQuery("findAllByStudyEventAndCrfOrCrfVersionOid", variables);
	}

	public ArrayList findAllByCRF(int crfId) {
		HashMap variables = new HashMap();
		variables.put(1, crfId);

		return executeFindAllQuery("findAllByCRF", variables);
	}

	public ArrayList findAllByCRFVersion(int versionId) {
		HashMap variables = new HashMap();
		variables.put(1, versionId);

		return executeFindAllQuery("findAllByCRFVersion", variables);
	}

	public ArrayList findAllStudySubjectByCRFVersion(int versionId) {
		this.setTypesExpected();

		this.setTypeExpected(25, TypeNames.STRING);
		this.setTypeExpected(26, TypeNames.STRING);
		if ("oracle".equalsIgnoreCase(CoreResources.getDBType())) {
			this.setTypeExpected(25, TypeNames.STRING); // r
			this.setTypeExpected(26, TypeNames.STRING); // r
			this.setTypeExpected(27, TypeNames.STRING); // r
		}
		HashMap variables = new HashMap();
		variables.put(1, versionId);

		ArrayList alist = this.select(digester.getQuery("findAllStudySubjectByCRFVersion"), variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			EventCRFBean eb = (EventCRFBean) this.getEntityFromHashMap(hm);
			eb.setStudySubjectName((String) hm.get("label"));
			eb.setEventName((String) hm.get("sed_name"));
			eb.setStudyName((String) hm.get("study_name"));
			al.add(eb);
		}
		return al;

	}

	public ArrayList findUndeletedWithStudySubjectsByCRFVersion(int versionId) {
		this.setTypesExpected();
		this.setTypeExpected(25, TypeNames.STRING);
		this.setTypeExpected(26, TypeNames.STRING);
		this.setTypeExpected(27, TypeNames.INT);
		HashMap variables = new HashMap();
		variables.put(1, versionId);

		ArrayList alist = this.select(digester.getQuery("findUndeletedWithStudySubjectsByCRFVersion"), variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			EventCRFBean eb = (EventCRFBean) this.getEntityFromHashMap(hm);
			eb.setStudySubjectName((String) hm.get("label"));
			eb.setEventName((String) hm.get("sed_name"));
			eb.setStudyName((String) hm.get("study_name"));
			eb.setEventOrdinal((Integer) hm.get("repeat_number"));
			al.add(eb);
		}
		return al;
	}

	public ArrayList findByEventSubjectVersion(StudyEventBean studyEvent, StudySubjectBean studySubject,
			CRFVersionBean crfVersion) {
		HashMap variables = new HashMap();
		variables.put(1, studyEvent.getId());
		variables.put(2, crfVersion.getId());
		variables.put(3, studySubject.getId());

		return executeFindAllQuery("findByEventSubjectVersion", variables);
	}

	// TODO: to get rid of warning refactor executeFindAllQuery method in
	// superclass
	public EventCRFBean findByEventCrfVersion(StudyEventBean studyEvent, CRFVersionBean crfVersion) {
		EventCRFBean eventCrfBean = null;
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, studyEvent.getId());
		variables.put(2, crfVersion.getId());

		ArrayList<EventCRFBean> eventCrfs = executeFindAllQuery("findByEventCrfVersion", variables);
		if (!eventCrfs.isEmpty() && eventCrfs.size() == 1) {
			eventCrfBean = eventCrfs.get(0);
		}
		return eventCrfBean;

	}

	public void delete(int eventCRFId) {
		HashMap variables = new HashMap();
		variables.put(1, eventCRFId);
		this.execute(digester.getQuery("delete"), variables);
	}

	public void setSDVStatus(boolean sdvStatus, int userId, int eventCRFId) {
		HashMap variables = new HashMap();
		variables.put(1, sdvStatus);
		variables.put(2, userId);
		variables.put(3, eventCRFId);

		this.execute(digester.getQuery("setSDVStatus"), variables);
	}
	
	public Collection findSDVedEventCRFsByStudyAndYear(StudyBean study, int year) {

		HashMap variables = new HashMap();

		variables.put(1, study.getId());
		variables.put(2, study.getId());
		variables.put(3, year);

		return executeFindAllQuery("findSDVedEventCRFsByStudyAndYear", variables);
	}

	public Integer countEventCRFsByStudy(int studyId, int parentStudyId) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, parentStudyId);
		String sql = digester.getQuery("countEventCRFsByStudy");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");

		} else {
			return 0;
		}
	}

	public Integer countEventCRFsByStudyIdentifier(String identifier) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, identifier);
		String sql = digester.getQuery("countEventCRFsByStudyIdentifier");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");

		} else {
			return 0;
		}
	}

	public Integer countEventCRFsByStudySubject(int studySubjectId, int studyId, int parentStudyId) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, studySubjectId);
		variables.put(2, studyId);
		variables.put(3, parentStudyId);
		String sql = digester.getQuery("countEventCRFsByStudySubject");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");

		} else {
			return 0;
		}
	}

	public Integer countEventCRFsByStudyIdentifier(int studyId, int parentStudyId, String studyIdentifier) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, parentStudyId);
		variables.put(3, studyIdentifier);
		String sql = digester.getQuery("countEventCRFsByStudyIdentifier");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");

		} else {
			return 0;
		}
	}

	public Integer countEventCRFsByByStudySubjectCompleteOrLockedAndNotSDVd(int studySubjectId) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, studySubjectId);
		String sql = digester.getQuery("countEventCRFsByByStudySubjectCompleteOrLockedAndNotSDVd");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");

		} else {
			return 0;
		}
	}

	public ArrayList getEventCRFsByStudySubjectCompleteOrLocked(int studySubjectId) {

		HashMap variables = new HashMap();
		variables.put(1, studySubjectId);

		return executeFindAllQuery("getEventCRFsByStudySubjectCompleteOrLocked", variables);
	}

	public ArrayList getEventCRFsByStudySubjectExceptInvalid(int studySubjectId) {

		HashMap variables = new HashMap();
		variables.put(1, studySubjectId);

		return executeFindAllQuery("getEventCRFsByStudySubjectExceptInvalid", variables);
	}

	public ArrayList getEventCRFsByStudySubjectLimit(int studySubjectId, int studyId, int parentStudyId, int limit,
			int offset) {

		HashMap variables = new HashMap();
		variables.put(1, studySubjectId);
		variables.put(2, studyId);
		variables.put(3, parentStudyId);
		variables.put(4, limit);
		variables.put(5, offset);

		return executeFindAllQuery("getEventCRFsByStudySubjectLimit", variables);
	}

	public ArrayList getEventCRFsByStudySubject(int studySubjectId, int studyId, int parentStudyId) {

		HashMap variables = new HashMap();
		variables.put(1, studySubjectId);
		variables.put(2, studyId);
		variables.put(3, parentStudyId);

		return executeFindAllQuery("getEventCRFsByStudySubject", variables);
	}

	public ArrayList getEventCRFsWithNonLockedCRFsByStudySubject(int studySubjectId, int studyId, int parentStudyId) {

		HashMap variables = new HashMap();
		variables.put(1, studySubjectId);
		variables.put(2, studyId);
		variables.put(3, parentStudyId);

		return executeFindAllQuery("getEventCRFsWithNonLockedCRFsByStudySubject", variables);
	}

	public ArrayList getGroupByStudySubject(int studySubjectId, int studyId, int parentStudyId) {

		HashMap variables = new HashMap();
		variables.put(1, studySubjectId);
		variables.put(2, studyId);
		variables.put(3, parentStudyId);

		return executeFindAllQuery("getGroupByStudySubject", variables);
	}

	public ArrayList getEventCRFsByStudyIdentifier(int studyId, int parentStudyId, String studyIdentifier, int limit,
			int offset) {

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, parentStudyId);
		variables.put(3, studyIdentifier);
		variables.put(4, limit);
		variables.put(5, offset);

		return executeFindAllQuery("getEventCRFsByStudyIdentifier", variables);
	}

	public Integer getCountWithFilter(int studyId, int parentStudyId, EventCRFSDVFilter filter) {

		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, parentStudyId);
		String sql = digester.getQuery("getCountWithFilter");
		sql += filter.execute("");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return null;
		}
	}

	public Integer getCountOfAvailableWithFilter(int studyId, int parentStudyId, EventCRFSDVFilter filter,
			boolean allowSdvWithOpenQueries) {

		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, parentStudyId);
		String sql = digester.getQuery("getCountOfAvailableWithFilter");
		sql += filter.execute("");
		if (!allowSdvWithOpenQueries) {
			variables.put(3, studyId);
			variables.put(4, parentStudyId);

			sql = sql + digester.getQuery("notAllowSdvWithOpenQueries");
		}

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return null;
		}
	}

	public ArrayList<EventCRFBean> getWithFilterAndSort(int studyId, int parentStudyId, EventCRFSDVFilter filter,
			EventCRFSDVSort sort, int rowStart, int rowEnd) {
		ArrayList<EventCRFBean> eventCRFs = new ArrayList<EventCRFBean>();
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, parentStudyId);
		String sql = digester.getQuery("getWithFilterAndSort");
		sql = sql + filter.execute("");
		sql = sql + " order By  ec.date_created ASC "; // major hack
		if ("oracle".equalsIgnoreCase(CoreResources.getDBType())) {
			sql += " )x)where r between " + (rowStart + 1) + " and " + rowEnd;
		} else {
			sql = sql + " LIMIT " + (rowEnd - rowStart) + " OFFSET " + rowStart;
		}

		ArrayList rows = this.select(sql, variables);

		for (Object row : rows) {
			EventCRFBean eventCRF = (EventCRFBean) this.getEntityFromHashMap((HashMap) row);
			eventCRFs.add(eventCRF);
		}
		return eventCRFs;
	}

	public ArrayList<EventCRFBean> getAvailableWithFilterAndSort(int studyId, int parentStudyId,
			EventCRFSDVFilter filter, EventCRFSDVSort sort, boolean allowSdvWithOpenQueries, int rowStart, int rowEnd) {
		ArrayList<EventCRFBean> eventCRFs = new ArrayList<EventCRFBean>();
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, parentStudyId);

		String sql = digester.getQuery("getAvailableWithFilterAndSort");
		sql = sql + filter.execute("");

		if (!allowSdvWithOpenQueries) {
			variables.put(3, studyId);
			variables.put(4, parentStudyId);

			sql = sql + digester.getQuery("notAllowSdvWithOpenQueries");
		}

		String sortQuery = sort.execute("");
		if (sortQuery.isEmpty()) {
			sql = sql + " order by ec.study_event_id asc";
		} else {
			sql = sql + sortQuery;
		}

		if ("oracle".equalsIgnoreCase(CoreResources.getDBType())) {
			sql += " )x)where r between " + (rowStart + 1) + " and " + rowEnd;
		} else {
			sql = sql + " LIMIT " + (rowEnd - rowStart) + " OFFSET " + rowStart;
		}

		ArrayList rows = this.select(sql, variables);

		for (Object row : rows) {
			EventCRFBean eventCRF = (EventCRFBean) this.getEntityFromHashMap((HashMap) row);
			eventCRFs.add(eventCRF);
		}
		return eventCRFs;
	}

	public List<String> getAvailableForSDVEntitiesByStudyId(int studyId, String entityProperty) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.STRING);

		List<String> result = new ArrayList<String>();

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, studyId);
		String sql = digester.getQuery("getAvailableForSDVEntitiesByStudyId").replaceFirst("entity", entityProperty);
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		while (it.hasNext()) {
			result.add((String) ((HashMap) it.next()).get("property"));
		}
		return result;
	}

	public List<String> getAvailableForSDVCRFNamesByStudyId(int studyId) {
		return getAvailableForSDVEntitiesByStudyId(studyId, "crf.name");
	}

	public List<String> getAvailableForSDVSiteNamesByStudyId(int studyId) {
		return getAvailableForSDVEntitiesByStudyId(studyId, "s.unique_identifier");
	}

	public List<String> getAvailableForSDVEventNamesByStudyId(int studyId) {
		return getAvailableForSDVEntitiesByStudyId(studyId, "sed.name");
	}

	public ArrayList getEventCRFsByStudy(int studyId, int parentStudyId, int limit, int offset) {

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, parentStudyId);
		variables.put(3, limit);
		variables.put(4, offset);

		return executeFindAllQuery("getEventCRFsByStudy", variables);
	}

	public ArrayList getEventCRFsByStudySubjectLabelLimit(String label, int studyId, int parentStudyId, int limit,
			int offset) {

		HashMap variables = new HashMap();
		variables.put(1, '%' + label + '%');
		variables.put(2, studyId);
		variables.put(3, parentStudyId);
		variables.put(4, limit);
		variables.put(5, offset);

		return executeFindAllQuery("getEventCRFsByStudySubjectLabelLimit", variables);
	}

	public ArrayList getEventCRFsByEventNameLimit(String eventName, int limit, int offset) {

		HashMap variables = new HashMap();
		variables.put(1, eventName);
		variables.put(2, limit);
		variables.put(3, offset);

		return executeFindAllQuery("getEventCRFsByEventNameLimit", variables);
	}

	public ArrayList getEventCRFsByEventDateLimit(int studyId, String eventDate, int limit, int offset) {

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, eventDate);
		variables.put(3, limit);
		variables.put(4, offset);

		return executeFindAllQuery("getEventCRFsByEventDateLimit", variables);
	}

	public ArrayList getEventCRFsByStudySDV(int studyId, boolean sdvStatus, int limit, int offset) {

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, sdvStatus);
		variables.put(3, limit);
		variables.put(4, offset);

		return executeFindAllQuery("getEventCRFsByStudySDV", variables);
	}

	public ArrayList getEventCRFsByCRFStatus(int studyId, int subjectEventStatusId, int limit, int offset) {

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, subjectEventStatusId);
		variables.put(3, limit);
		variables.put(4, offset);

		return executeFindAllQuery("getEventCRFsByCRFStatus", variables);
	}

	public ArrayList getEventCRFsBySDVRequirement(int studyId, int parentStudyId, int limit, int offset,
			Integer... sdvCode) {

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, parentStudyId);
		this.setTypesExpected();

		String sql = digester.getQuery("getEventCRFsBySDVRequirement");
		sql += " AND ( ";
		for (int i = 0; i < sdvCode.length; i++) {
			sql += i != 0 ? " OR " : "";
			sql += " source_data_verification_code = " + sdvCode[i];
		}
		sql += " ) ))  limit " + limit + " offset " + offset;

		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();

		for (Object anAlist : alist) {
			EventCRFBean eb = (EventCRFBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	public Integer countEventCRFsByStudySubjectLabel(String label, int studyId, int parentStudyId) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, label);
		variables.put(2, studyId);
		variables.put(3, parentStudyId);

		String sql = digester.getQuery("countEventCRFsByStudySubjectLabel");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");

		} else {
			return 0;
		}
	}

	public Integer countEventCRFsByStudySDV(int studyId, boolean sdvStatus) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, sdvStatus);
		String sql = digester.getQuery("countEventCRFsByStudySDV");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");

		} else {
			return 0;
		}
	}

	public Integer countEventCRFsByCRFStatus(int studyId, int statusId) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, statusId);
		String sql = digester.getQuery("countEventCRFsByCRFStatus");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");

		} else {
			return 0;
		}
	}

	public Integer countEventCRFsByEventName(String eventName) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, eventName);
		String sql = digester.getQuery("countEventCRFsByEventName");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");

		} else {
			return 0;
		}
	}

	public Integer countEventCRFsBySDVRequirement(int studyId, int parentStudyId, Integer... sdvCode) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, parentStudyId);
		String sql = digester.getQuery("countEventCRFsBySDVRequirement");
		sql += " AND ( ";
		for (int i = 0; i < sdvCode.length; i++) {
			sql += i != 0 ? " OR " : "";
			sql += " source_data_verification_code = " + sdvCode[i];
		}
		sql += "))) ";

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");

		} else {
			return 0;
		}
	}

	public Integer countEventCRFsByEventNameSubjectLabel(String eventName, String subjectLabel) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, eventName);
		variables.put(2, subjectLabel);
		String sql = digester.getQuery("countEventCRFsByEventNameSubjectLabel");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");

		} else {
			return 0;
		}
	}

	public Integer countEventCRFsByEventDate(int studyId, String eventDate) {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, eventDate);
		String sql = digester.getQuery("countEventCRFsByEventDate");
		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");

		} else {
			return 0;
		}
	}

	public Map<Integer, SortedSet<EventCRFBean>> buildEventCrfListByStudyEvent(Integer studySubjectId) {
		this.setTypesExpected(); // <== Must be called first

		Map<Integer, SortedSet<EventCRFBean>> result = new HashMap<Integer, SortedSet<EventCRFBean>>();

		HashMap<Integer, Object> param = new HashMap<Integer, Object>();
		param.put(1, studySubjectId);

		List selectResult = select(digester.getQuery("buildEventCrfListByStudyEvent"), param);

		for (Object aSelectResult : selectResult) {
			EventCRFBean bean = (EventCRFBean) this.getEntityFromHashMap((HashMap) aSelectResult);

			Integer studyEventId = bean.getStudyEventId();
			if (!result.containsKey(studyEventId)) {
				result.put(studyEventId, new TreeSet<EventCRFBean>(new Comparator<EventCRFBean>() {
					public int compare(EventCRFBean o1, EventCRFBean o2) {
						Integer id1 = o1.getId();
						Integer id2 = o2.getId();
						return id1.compareTo(id2);
					}
				}));
			}
			result.get(studyEventId).add(bean);
		}

		return result;
	}

	public Set<Integer> buildNonEmptyEventCrfIds(Integer studySubjectId) {
		Set<Integer> result = new HashSet<Integer>();

		HashMap<Integer, Object> param = new HashMap<Integer, Object>();
		param.put(1, studySubjectId);

		List selectResult = select(digester.getQuery("buildNonEmptyEventCrfIds"), param);

		for (Object aSelectResult : selectResult) {
			HashMap hm = (HashMap) aSelectResult;
			result.add((Integer) hm.get("event_crf_id"));
		}

		return result;
	}

	public void updateCRFVersionID(int event_crf_id, int crf_version_id, int user_id) {
		updateCRFVersionID(event_crf_id, crf_version_id, user_id, null);
	}

	public void updateCRFVersionID(int event_crf_id, int crf_version_id, int user_id, Connection con) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.INT);
		this.setTypeExpected(3, TypeNames.INT);
		this.setTypeExpected(4, TypeNames.BOOL);
		this.setTypeExpected(3, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, crf_version_id);
		variables.put(2, user_id);
		variables.put(3, user_id);
		variables.put(4, false);
		variables.put(5, event_crf_id);
		String sql = digester.getQuery("updateCRFVersionID");
		// this is the way to make the change transactional
		if (con == null) {
			this.execute(sql, variables);
		} else {
			this.execute(sql, variables, con);
		}
	}

	public void deleteEventCRFDNMap(int eventCRFId) {
		HashMap<Integer, Comparable> variables = new HashMap<Integer, Comparable>();
		variables.put(1, eventCRFId);
		this.execute(digester.getQuery("deleteEventCRFDNMap"), variables);
	}
}
