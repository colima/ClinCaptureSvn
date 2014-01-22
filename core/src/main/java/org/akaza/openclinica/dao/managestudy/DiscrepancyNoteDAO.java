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
package org.akaza.openclinica.dao.managestudy;

import java.sql.Connection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteStatisticBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DiscrepancyNoteDAO extends AuditableEntityDAO {
	public static final String UNION_OP = " UNION ";
	// if true, we fetch the mapping along with the bean
	// only applies to functions which return a single bean
	private boolean fetchMapping = false;

	/**
	 * @return Returns the fetchMapping.
	 */
	public boolean isFetchMapping() {
		return fetchMapping;
	}

	/**
	 * @param fetchMapping
	 *            The fetchMapping to set.
	 */
	public void setFetchMapping(boolean fetchMapping) {
		this.fetchMapping = fetchMapping;
	}

	private void setQueryNames() {
		findByPKAndStudyName = "findByPKAndStudy";
		getCurrentPKName = "getCurrentPrimaryKey";
	}

	public DiscrepancyNoteDAO(DataSource ds) {
		super(ds);
		setQueryNames();
	}

	public DiscrepancyNoteDAO(DataSource ds, DAODigester digester) {
		super(ds);
		this.digester = digester;
		setQueryNames();
	}

	public DiscrepancyNoteDAO(DataSource ds, Connection connection) {
		super(ds, connection);
		setQueryNames();
	}

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_DISCREPANCY_NOTE;
	}

	@Override
	public void setTypesExpected() {

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.STRING);
		this.setTypeExpected(3, TypeNames.INT);
		this.setTypeExpected(4, TypeNames.INT);

		this.setTypeExpected(5, TypeNames.STRING);
		this.setTypeExpected(6, TypeNames.DATE);
		this.setTypeExpected(7, TypeNames.INT);
		this.setTypeExpected(8, TypeNames.INT);
		this.setTypeExpected(9, TypeNames.STRING);
		this.setTypeExpected(10, TypeNames.INT);
		this.setTypeExpected(11, TypeNames.INT);
	}

	public void setMapTypesExpected() {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.INT);
		this.setTypeExpected(3, TypeNames.STRING);
	}

	public void setStatisticTypesExpected() {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.INT);
		this.setTypeExpected(3, TypeNames.INT);
	}

	/**
	 * <p>
	 * getEntityFromHashMap, the method that gets the object from the database query.
	 */
	@SuppressWarnings("deprecation")
	public Object getEntityFromHashMap(HashMap hm) {
		DiscrepancyNoteBean eb = new DiscrepancyNoteBean();
		Date dateCreated = (Date) hm.get("date_created");
		Integer ownerId = (Integer) hm.get("owner_id");
		eb.setCreatedDate(dateCreated);
		eb.setOwnerId(ownerId);
		eb.setId(selectInt(hm, "discrepancy_note_id"));
		eb.setDescription((String) hm.get("description"));
		eb.setDiscrepancyNoteTypeId((Integer) hm.get("discrepancy_note_type_id"));
		eb.setResolutionStatusId((Integer) hm.get("resolution_status_id"));
		eb.setParentDnId((Integer) hm.get("parent_dn_id"));
		eb.setDetailedNotes((String) hm.get("detailed_notes"));
		eb.setEntityType((String) hm.get("entity_type"));
		eb.setDisType(DiscrepancyNoteType.get(eb.getDiscrepancyNoteTypeId()));
		eb.setResStatus(ResolutionStatus.get(eb.getResolutionStatusId()));
		eb.setStudyId(selectInt(hm, "study_id"));
		eb.setAssignedUserId(selectInt(hm, "assigned_user_id"));
		if (hm.get("item_id") != null) {
			eb.setItemId((Integer) hm.get("item_id"));
		}
		if (eb.getAssignedUserId() > 0) {
			UserAccountDAO userAccountDAO = new UserAccountDAO(ds);
			UserAccountBean assignedUser = (UserAccountBean) userAccountDAO.findByPK(eb.getAssignedUserId());
			eb.setAssignedUser(assignedUser);
		}
		eb.setAge(selectInt(hm, "age"));
		eb.setDays(selectInt(hm, "days"));
		return eb;
	}

	public DiscrepancyNoteStatisticBean getStatisticEntityFromHashMap(Map hm) {
		DiscrepancyNoteStatisticBean statisticBean = new DiscrepancyNoteStatisticBean();

		statisticBean.setDiscrepancyNotesCount((Integer) hm.get("sum"));
		statisticBean.setDiscrepancyNoteTypeId((Integer) hm.get("discrepancy_note_type_id"));
		statisticBean.setResolutionStatusId((Integer) hm.get("resolution_status_id"));

		return statisticBean;

	}

	public Collection findAll() {
		return this.executeFindAllQuery("findAll");
	}

	public ArrayList findAllParentsByStudy(StudyBean study) {
		HashMap variables = new HashMap();
		variables.put(1, study.getId());
		variables.put(2, study.getId());
		ArrayList notes = executeFindAllQuery("findAllParentsByStudy", variables);

		if (fetchMapping) {
			for (int i = 0; i < notes.size(); i++) {
				DiscrepancyNoteBean dnb = (DiscrepancyNoteBean) notes.get(i);
				dnb = findSingleMapping(dnb);
				notes.set(i, dnb);
			}
		}

		return notes;
	}

	public ArrayList findAllByStudyAndParent(StudyBean study, int parentId) {
		HashMap variables = new HashMap();
		variables.put(1, parentId);
		variables.put(2, study.getId());
		variables.put(3, study.getId());
		variables.put(4, study.getId());
		return this.executeFindAllQuery("findAllByStudyAndParent", variables);
	}

	public ArrayList<DiscrepancyNoteBean> findAllItemNotesByEventCRF(int eventCRFId) {
		this.setTypesExpected();
		ArrayList alist;
		HashMap variables = new HashMap();
		variables.put(1, eventCRFId);
		alist = this.select(digester.getQuery("findAllItemNotesByEventCRF"), variables);
		ArrayList<DiscrepancyNoteBean> al = new ArrayList<DiscrepancyNoteBean>();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			al.add(eb);
		}
		return al;
	}

	public ArrayList<DiscrepancyNoteBean> findAllParentItemNotesByEventCRF(int eventCRFId) {
		this.setTypesExpected();
		ArrayList alist;
		HashMap variables = new HashMap();
		variables.put(1, eventCRFId);
		alist = this.select(digester.getQuery("findAllParentItemNotesByEventCRF"), variables);
		ArrayList<DiscrepancyNoteBean> al = new ArrayList<DiscrepancyNoteBean>();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			al.add(eb);
		}
		return al;
	}

	public ArrayList<DiscrepancyNoteBean> findAllParentItemNotesByEventCRFWithConstraints(int eventCRFId,
			StringBuffer constraints) {
		this.setTypesExpected();
		ArrayList alist;
		HashMap variables = new HashMap();
		variables.put(1, eventCRFId);
		String sql = digester.getQuery("findAllParentItemNotesByEventCRF");
		String[] s = sql.split("order by");
		sql = s[0] + " " + constraints.toString() + " order by " + s[1];
		alist = this.select(sql, variables);
		ArrayList<DiscrepancyNoteBean> al = new ArrayList<DiscrepancyNoteBean>();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			al.add(eb);
		}
		return al;
	}

	public Integer getCountWithFilter(ListNotesFilter filter, StudyBean currentStudy) {
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, currentStudy.getId());
		variables.put(2, currentStudy.getId());
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

	public ArrayList<DiscrepancyNoteBean> getWithFilterAndSort(StudyBean currentStudy, ListNotesFilter filter,
			ListNotesSort sort, int rowStart, int rowEnd) {
		ArrayList<DiscrepancyNoteBean> discNotes = new ArrayList<DiscrepancyNoteBean>();
		setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, currentStudy.getId());
		variables.put(2, currentStudy.getId());
		String sql = digester.getQuery("getWithFilterAndSort");
		sql = sql + filter.execute("");

		if ("oracle".equalsIgnoreCase(CoreResources.getDBName())) {
			sql += " AND rownum <= " + rowEnd + " and rownum >" + rowStart;
			sql = sql + sort.execute("");
		} else {
			sql = sql + sort.execute("");
			sql = sql + " LIMIT " + (rowEnd - rowStart) + " OFFSET " + rowStart;
		}

		ArrayList rows = select(sql, variables);

		for (Object row : rows) {
			DiscrepancyNoteBean discBean = (DiscrepancyNoteBean) this.getEntityFromHashMap((HashMap) row);
			discBean = findSingleMapping(discBean);
			discNotes.add(discBean);
		}
		return discNotes;

	}

	public Integer getViewNotesCountWithFilter(ListNotesFilter filter, StudyBean currentStudy) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, currentStudy.getId());
		variables.put(2, currentStudy.getId());
		variables.put(3, currentStudy.getId());
		variables.put(4, currentStudy.getId());
		variables.put(5, currentStudy.getId());
		variables.put(6, currentStudy.getId());
		variables.put(7, currentStudy.getId());
		variables.put(8, currentStudy.getId());
		variables.put(9, currentStudy.getId());
		variables.put(10, currentStudy.getId());
		String sql = "select count(all_dn.discrepancy_note_id) as COUNT from (";
		sql += digester.getQuery("findAllSubjectDNByStudy");
		sql += filter.execute("");
		sql += UNION_OP;
		sql += digester.getQuery("findAllStudySubjectDNByStudy");
		sql += filter.execute("");
		sql += UNION_OP;
		sql += digester.getQuery("findAllStudyEventDNByStudy");
		sql += filter.execute("");
		sql += UNION_OP;
		sql += digester.getQuery("findAllEventCrfDNByStudy");
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql += " and ec.event_crf_id not in ( " + this.findSiteHiddenEventCrfIdsString(currentStudy) + " ) ";
		}
		sql += filter.execute("");
		sql += UNION_OP;
		sql += digester.getQuery("findAllItemDataDNByStudy");
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql += " and ec.event_crf_id not in ( " + this.findSiteHiddenEventCrfIdsString(currentStudy) + " ) ";
		}
		sql += filter.execute("");
		if ("oracle".equalsIgnoreCase(CoreResources.getDBName())) {
			sql += " ) all_dn";
		} else {
			sql += " ) as all_dn";
		}

		ArrayList rows = select(sql, variables);
		Iterator it = rows.iterator();
		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return null;
		}
	}

	/*
	 * ClinCapture #64 Get count of filtered discrepancy notes
	 */
	public Integer getViewNotesCountWithFilter(String filter, StudyBean currentStudy) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		for (int i = 1; i <= 10; i++) {
			variables.put(i, currentStudy.getId());
		}

		StringBuilder sql = new StringBuilder("select count(all_dn.discrepancy_note_id) as COUNT from (");

		sql.append(digester.getQuery("findAllSubjectDNByStudy"));
		sql.append(filter).append(UNION_OP);
		sql.append(digester.getQuery("findAllStudySubjectDNByStudy"));
		sql.append(filter).append(UNION_OP);
		sql.append(digester.getQuery("findAllStudyEventDNByStudy"));
		sql.append(filter).append(UNION_OP);
		sql.append(digester.getQuery("findAllEventCrfDNByStudy"));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(filter).append(UNION_OP);
		sql.append(digester.getQuery("findAllItemDataDNByStudy"));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(filter);
		if ("oracle".equalsIgnoreCase(CoreResources.getDBName())) {
			sql.append(" ) all_dn");
		} else {
			sql.append(" ) as all_dn");
		}

		ArrayList rows = select(sql.toString(), variables);
		Iterator it = rows.iterator();
		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return null;
		}
	}

	public ArrayList<DiscrepancyNoteBean> getViewNotesWithFilterAndSort(StudyBean currentStudy, ListNotesFilter filter,
			ListNotesSort sort, int rowStart, int rowEnd) {
		ArrayList<DiscrepancyNoteBean> discNotes = new ArrayList<DiscrepancyNoteBean>();
		setTypesExpected();
		this.setTypeExpected(12, TypeNames.STRING);
		this.setTypeExpected(13, TypeNames.INT);
		this.setTypeExpected(14, TypeNames.INT);
		this.setTypeExpected(15, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, currentStudy.getId());
		variables.put(2, currentStudy.getId());
		variables.put(3, currentStudy.getId());
		variables.put(4, currentStudy.getId());
		variables.put(5, currentStudy.getId());
		variables.put(6, currentStudy.getId());
		variables.put(7, currentStudy.getId());
		variables.put(8, currentStudy.getId());
		variables.put(9, currentStudy.getId());
		variables.put(10, currentStudy.getId());

		String sql = "";
		if ("oracle".equalsIgnoreCase(CoreResources.getDBName())) {
			sql = sql + "SELECT * FROM ( SELECT x.*, ROWNUM as rnum FROM (";
		}
		sql = sql + digester.getQuery("findAllSubjectDNByStudy");
		sql = sql + filter.execute("");
		sql += UNION_OP;
		sql += digester.getQuery("findAllStudySubjectDNByStudy");
		sql += filter.execute("");
		sql += UNION_OP;
		sql += digester.getQuery("findAllStudyEventDNByStudy");
		sql += filter.execute("");
		sql += UNION_OP;
		sql += digester.getQuery("findAllEventCrfDNByStudy");
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql += " and ec.event_crf_id not in ( " + this.findSiteHiddenEventCrfIdsString(currentStudy) + " ) ";
		}
		sql += filter.execute("");
		sql += UNION_OP;
		sql += digester.getQuery("findAllItemDataDNByStudy");
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql += " and ec.event_crf_id not in ( " + this.findSiteHiddenEventCrfIdsString(currentStudy) + " ) ";
		}
		sql += filter.execute("");

		if ("oracle".equalsIgnoreCase(CoreResources.getDBName())) {
			sql += ") x )  WHERE rnum BETWEEN " + (rowStart + 1) + " and " + rowEnd;
			sql += sort.execute("");
		} else {
			sql += sort.execute("");
			sql += " LIMIT " + (rowEnd - rowStart) + " OFFSET " + rowStart;
		}

		ArrayList rows = select(sql, variables);

		for (Object row : rows) {
			DiscrepancyNoteBean discBean = (DiscrepancyNoteBean) this.getEntityFromHashMap((HashMap) row);
			discBean = findSingleMapping(discBean);
			discNotes.add(discBean);
		}
		return discNotes;
	}

	public ArrayList<DiscrepancyNoteBean> getViewNotesWithFilterAndSortLimits(StudyBean currentStudy,
			ListNotesFilter filter, ListNotesSort sort, int offset, int limit) {
		setTypesExpected();
		this.setTypeExpected(12, TypeNames.STRING);
		this.setTypeExpected(13, TypeNames.INT);
		this.setTypeExpected(14, TypeNames.INT);
		this.setTypeExpected(15, TypeNames.STRING);
		this.setTypeExpected(16, TypeNames.STRING);
		this.setTypeExpected(17, TypeNames.STRING);

		Map variables = new HashMap();
		for (int i = 1; i <= 10; i++) {
			variables.put(i, currentStudy.getId());
		}

		StringBuilder sql = new StringBuilder("SELECT dns.* FROM ( ");

		String filterPart = filter.execute("");
		String sortPart = sort.execute("");

		sql.append(digester.getQuery("findAllSubjectDNByStudy"));
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllStudySubjectDNByStudy"));
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllStudyEventDNByStudy"));
		sql.append(filterPart).append(filter.getAdditionalStudyEventFilter()).append(UNION_OP);
		sql.append(digester.getQuery("findAllEventCrfDNByStudy"));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(filterPart).append(filter.getAdditionalStudyEventFilter()).append(UNION_OP);
		sql.append(digester.getQuery("findAllItemDataDNByStudy"));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(filterPart).append(filter.getAdditionalStudyEventFilter());
		sql.append(") dns ");
		// additional filters crfName, eventName, entityName
		sql.append(filter.getAdditionalFilter());
		sql.append(sortPart);

		sql.append(" offset ").append(offset).append(" limit ").append(limit);

		ArrayList rows = select(sql.toString(), variables);
		Iterator it = rows.iterator();
		ArrayList<DiscrepancyNoteBean> discNotes = new ArrayList<DiscrepancyNoteBean>();
		while (it.hasNext()) {
			DiscrepancyNoteBean discBean = (DiscrepancyNoteBean) this.getEntityFromHashMap((HashMap) it.next());
			discBean = findSingleMapping(discBean);
			discNotes.add(discBean);
		}
		return discNotes;
	}

	public Integer countViewNotesWithFilter(StudyBean currentStudy, ListNotesFilter filter) {
		unsetTypeExpected();
		setTypeExpected(1, TypeNames.INT);

		Map variables = new HashMap();
		for (int i = 1; i <= 10; i++) {
			variables.put(i, currentStudy.getId());
		}

		StringBuilder sql = new StringBuilder("select count(*) count from (");

		String filterPart = filter.execute("");

		sql.append(digester.getQuery("findAllSubjectDNByStudy"));
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllStudySubjectDNByStudy"));
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllStudyEventDNByStudy"));
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllEventCrfDNByStudy"));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllItemDataDNByStudy"));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(filterPart);

		sql.append(") dns ").append(filter.getAdditionalFilter());

		ArrayList rows = select(sql.toString(), variables);

		return rows.size() != 0 ? (Integer) ((Map) rows.get(0)).get("count") : new Integer(0);
	}

	public ArrayList<DiscrepancyNoteBean> getViewNotesWithFilterAndSort(StudyBean currentStudy, ListNotesFilter filter,
			ListNotesSort sort) {
		ArrayList<DiscrepancyNoteBean> discNotes = new ArrayList<DiscrepancyNoteBean>();
		setTypesExpected();
		this.setTypeExpected(12, TypeNames.STRING);
		this.setTypeExpected(13, TypeNames.INT);
		this.setTypeExpected(14, TypeNames.INT);
		this.setTypeExpected(15, TypeNames.STRING);
		this.setTypeExpected(16, TypeNames.STRING);
		this.setTypeExpected(17, TypeNames.STRING);

		Map variables = new HashMap();
		for (int i = 1; i <= 10; i++) {
			variables.put(i, currentStudy.getId());
		}

		StringBuilder sql = new StringBuilder("SELECT dns.* FROM ( ");

		String filterPart = filter.execute("");
		String sortPart = sort.execute("");

		sql.append(digester.getQuery("findAllSubjectDNByStudy"));
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllStudySubjectDNByStudy"));
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllStudyEventDNByStudy"));
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllEventCrfDNByStudy"));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllItemDataDNByStudy"));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(filterPart);
		sql.append(") dns ");
		// additional filters crfName, eventName, entityName
		sql.append(filter.getAdditionalFilter());
		sql.append(sortPart);

		ArrayList rows = select(sql.toString(), variables);

		for (Object row : rows) {
			DiscrepancyNoteBean discBean = (DiscrepancyNoteBean) this.getEntityFromHashMap((HashMap) row);
			discBean = findSingleMapping(discBean);
			discNotes.add(discBean);
		}
		return discNotes;
	}

	public ArrayList<DiscrepancyNoteBean> findAllDiscrepancyNotesDataByStudy(StudyBean currentStudy) {
		ArrayList<DiscrepancyNoteBean> discNotes = new ArrayList<DiscrepancyNoteBean>();
		setTypesExpected();
		this.setTypeExpected(12, TypeNames.STRING);
		this.setTypeExpected(13, TypeNames.INT);
		this.setTypeExpected(14, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, currentStudy.getId());
		variables.put(2, currentStudy.getId());
		variables.put(3, currentStudy.getId());
		variables.put(4, currentStudy.getId());
		variables.put(5, currentStudy.getId());
		variables.put(6, currentStudy.getId());
		variables.put(7, currentStudy.getId());
		variables.put(8, currentStudy.getId());
		variables.put(9, currentStudy.getId());
		variables.put(10, currentStudy.getId());
		String sql = digester.getQuery("findAllSubjectDNByStudy");
		sql += UNION_OP;
		sql += digester.getQuery("findAllStudySubjectDNByStudy");
		sql += UNION_OP;
		sql += digester.getQuery("findAllStudyEventDNByStudy");
		sql += UNION_OP;
		sql += digester.getQuery("findAllEventCrfDNByStudy");
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql += " and ec.event_crf_id not in ( " + this.findSiteHiddenEventCrfIdsString(currentStudy) + " ) ";
		}
		sql += UNION_OP;
		sql += digester.getQuery("findAllItemDataDNByStudy");
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql += " and ec.event_crf_id not in ( " + this.findSiteHiddenEventCrfIdsString(currentStudy) + " ) ";
		}

		ArrayList rows = select(sql, variables);

		for (Object row : rows) {
			DiscrepancyNoteBean discBean = (DiscrepancyNoteBean) this.getEntityFromHashMap((HashMap) row);
			discBean = findSingleMapping(discBean);
			discNotes.add(discBean);
		}
		return discNotes;
	}

	public List<DiscrepancyNoteStatisticBean> countNotesStatistic(StudyBean currentStudy) {
		setStatisticTypesExpected();

		Map variables = new HashMap();
		for (int i = 1; i <= 10; i++) {
			variables.put(i, currentStudy.getId());
		}

		StringBuilder sql = new StringBuilder(
				"SELECT sum(count), discrepancy_note_type_id, resolution_status_id FROM (");

		sql.append(digester.getQuery("countAllSubjectDNByStudyForStat"));
		sql.append(UNION_OP);
		sql.append(digester.getQuery("countAllStudySubjectDNByStudyForStat"));
		sql.append(UNION_OP);
		sql.append(digester.getQuery("countAllStudyEventDNByStudyForStat"));
		sql.append(UNION_OP);
		sql.append(digester.getQuery("countAllEventCrfDNByStudyForStat"));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(" GROUP BY dn.discrepancy_note_type_id, dn.resolution_status_id ");
		sql.append(UNION_OP);
		sql.append(digester.getQuery("countAllItemDataDNByStudyForStat"));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(" GROUP BY dn.discrepancy_note_type_id, dn.resolution_status_id ");
		sql.append(") types GROUP BY discrepancy_note_type_id, resolution_status_id");

		ArrayList rows = select(sql.toString(), variables);
		Iterator it = rows.iterator();
		List<DiscrepancyNoteStatisticBean> notesStat = new ArrayList<DiscrepancyNoteStatisticBean>();
		while (it.hasNext()) {
			notesStat.add(getStatisticEntityFromHashMap((Map) it.next()));
		}
		return notesStat;
	}

	public ArrayList<DiscrepancyNoteBean> getNotesWithFilterAndSort(StudyBean currentStudy, ListNotesFilter filter) {
		setTypesExpected();
		this.setTypeExpected(12, TypeNames.STRING);
		this.setTypeExpected(13, TypeNames.INT);
		this.setTypeExpected(14, TypeNames.INT);
		this.setTypeExpected(15, TypeNames.STRING);
		this.setTypeExpected(16, TypeNames.STRING);
		this.setTypeExpected(17, TypeNames.STRING);

		Map variables = new HashMap();
		for (int i = 1; i <= 10; i++) {
			variables.put(i, currentStudy.getId());
		}

		StringBuilder sql = new StringBuilder("SELECT dns.* FROM ( ");

		String filterPart = filter.execute("");

		sql.append(digester.getQuery("findAllSubjectDNByStudy"));
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllStudySubjectDNByStudy"));
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllStudyEventDNByStudy"));
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllEventCrfDNByStudy"));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(filterPart).append(UNION_OP);
		sql.append(digester.getQuery("findAllItemDataDNByStudy"));
		if (currentStudy.isSite(currentStudy.getParentStudyId())) {
			sql.append(" and ec.event_crf_id not in ( ").append(this.findSiteHiddenEventCrfIdsString(currentStudy))
					.append(" ) ");
		}
		sql.append(filterPart);
		sql.append(") dns ");
		// additional filters crfName, eventName, entityName
		sql.append(filter.getAdditionalFilter());
		sql.append(" order by dns.label");

		ArrayList rows = select(sql.toString(), variables);
		Iterator it = rows.iterator();
		ArrayList<DiscrepancyNoteBean> discNotes = new ArrayList<DiscrepancyNoteBean>();
		while (it.hasNext()) {
			DiscrepancyNoteBean discBean = (DiscrepancyNoteBean) this.getEntityFromHashMap((HashMap) it.next());
			discBean = findSingleMapping(discBean);
			discNotes.add(discBean);
		}
		return discNotes;
	}

	public Collection findAllByEntityAndColumn(String entityName, int entityId, String column) {
		this.setTypesExpected();
		this.setTypeExpected(12, TypeNames.STRING);// ss.label
		ArrayList alist = new ArrayList();
		HashMap variables = new HashMap();
		variables.put(1, entityId);
		variables.put(2, column);
		if ("subject".equalsIgnoreCase(entityName)) {
			alist = this.select(digester.getQuery("findAllBySubjectAndColumn"), variables);
		} else if ("studySub".equalsIgnoreCase(entityName)) {
			alist = this.select(digester.getQuery("findAllByStudySubjectAndColumn"), variables);
		} else if ("eventCrf".equalsIgnoreCase(entityName)) {
			this.setTypeExpected(13, TypeNames.DATE);// date_start
			this.setTypeExpected(14, TypeNames.STRING);// sed_name
			this.setTypeExpected(15, TypeNames.STRING);// crf_name
			alist = this.select(digester.getQuery("findAllByEventCRFAndColumn"), variables);
		} else if ("studyEvent".equalsIgnoreCase(entityName)) {
			this.setTypeExpected(13, TypeNames.DATE);// date_start
			this.setTypeExpected(14, TypeNames.STRING);// sed_name
			alist = this.select(digester.getQuery("findAllByStudyEventAndColumn"), variables);
		} else if ("itemData".equalsIgnoreCase(entityName)) {
			this.setTypeExpected(13, TypeNames.DATE);// date_start
			this.setTypeExpected(14, TypeNames.STRING);// sed_name
			this.setTypeExpected(15, TypeNames.STRING);// crf_name
			this.setTypeExpected(16, TypeNames.STRING);// item_name
			alist = this.select(digester.getQuery("findAllByItemDataAndColumn"), variables);
		}

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			eb.setSubjectName((String) hm.get("label"));
			if ("eventCrf".equalsIgnoreCase(entityName) || "itemData".equalsIgnoreCase(entityName)) {
				eb.setEventName((String) hm.get("sed_name"));
				eb.setEventStart((Date) hm.get("date_start"));
				eb.setCrfName((String) hm.get("crf_name"));
				eb.setEntityName((String) hm.get("item_name"));

			} else if ("studyEvent".equalsIgnoreCase(entityName)) {
				eb.setEventName((String) hm.get("sed_name"));
				eb.setEventStart((Date) hm.get("date_start"));
			}
			if (fetchMapping) {
				eb = findSingleMapping(eb);
			}

			al.add(eb);
		}
		return al;
	}

	public ArrayList findAllEntityByPK(String entityName, int noteId) {
		this.setTypesExpected();
		ArrayList alist = new ArrayList();
		this.setTypeExpected(12, TypeNames.STRING);// ss.label

		HashMap variables = new HashMap();
		variables.put(1, noteId);
		variables.put(2, noteId);
		if ("subject".equalsIgnoreCase(entityName)) {
			this.setTypeExpected(13, TypeNames.STRING);// column_name
			alist = this.select(digester.getQuery("findAllSubjectByPK"), variables);
		} else if ("studySub".equalsIgnoreCase(entityName)) {
			this.setTypeExpected(13, TypeNames.STRING);// column_name
			alist = this.select(digester.getQuery("findAllStudySubjectByPK"), variables);
		} else if ("eventCrf".equalsIgnoreCase(entityName)) {
			this.setTypeExpected(13, TypeNames.DATE);// date_start
			this.setTypeExpected(14, TypeNames.STRING);// sed_name
			this.setTypeExpected(15, TypeNames.STRING);// crf_name
			this.setTypeExpected(16, TypeNames.STRING);// column_name
			alist = this.select(digester.getQuery("findAllEventCRFByPK"), variables);
		} else if ("studyEvent".equalsIgnoreCase(entityName)) {
			this.setTypeExpected(13, TypeNames.DATE);// date_start
			this.setTypeExpected(14, TypeNames.STRING);// sed_name
			this.setTypeExpected(15, TypeNames.STRING);// column_name
			alist = this.select(digester.getQuery("findAllStudyEventByPK"), variables);
		} else if ("itemData".equalsIgnoreCase(entityName)) {
			this.setTypeExpected(13, TypeNames.DATE);// date_start
			this.setTypeExpected(14, TypeNames.STRING);// sed_name
			this.setTypeExpected(15, TypeNames.STRING);// crf_name
			this.setTypeExpected(16, TypeNames.STRING);// item_name
			this.setTypeExpected(17, TypeNames.STRING);// value
			this.setTypeExpected(18, TypeNames.INT);// item_data_id
			this.setTypeExpected(19, TypeNames.INT);// item_id
			alist = this.select(digester.getQuery("findAllItemDataByPK"), variables);
		}

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			if ("subject".equalsIgnoreCase(entityName) || "studySub".equalsIgnoreCase(entityName)) {
				eb.setSubjectName((String) hm.get("label"));
				eb.setColumn((String) hm.get("column_name"));
			} else if ("eventCrf".equalsIgnoreCase(entityName)) {
				eb.setSubjectName((String) hm.get("label"));
				eb.setEventName((String) hm.get("sed_name"));
				eb.setEventStart((Date) hm.get("date_start"));
				eb.setCrfName((String) hm.get("crf_name"));
				eb.setColumn((String) hm.get("column_name"));
			} else if ("itemData".equalsIgnoreCase(entityName)) {
				eb.setSubjectName((String) hm.get("label"));
				eb.setEventName((String) hm.get("sed_name"));
				eb.setEventStart((Date) hm.get("date_start"));
				eb.setCrfName((String) hm.get("crf_name"));
				eb.setEntityName((String) hm.get("item_name"));
				eb.setEntityValue((String) hm.get("value"));
				// YW <<
				eb.setEntityId((Integer) hm.get("item_data_id"));
				eb.setItemId((Integer) hm.get("item_id"));
				// YW >>

			} else if ("studyEvent".equalsIgnoreCase(entityName)) {
				eb.setSubjectName((String) hm.get("label"));
				eb.setEventName((String) hm.get("sed_name"));
				eb.setEventStart((Date) hm.get("date_start"));
				eb.setColumn((String) hm.get("column_name"));
			}
			if (fetchMapping) {
				eb = findSingleMapping(eb);
			}
			al.add(eb);
		}
		return al;
	}

	public ArrayList findAllSubjectByStudy(StudyBean study) {
		this.setTypesExpected();
		ArrayList alist;
		this.setTypeExpected(12, TypeNames.STRING);// ss.label
		this.setTypeExpected(13, TypeNames.STRING);// column_name
		this.setTypeExpected(14, TypeNames.INT);// subject_id

		HashMap variables = new HashMap();
		variables.put(1, study.getId());
		variables.put(2, study.getId());
		variables.put(3, study.getId());

		alist = this.select(digester.getQuery("findAllSubjectByStudy"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			eb.setSubjectName((String) hm.get("label"));
			eb.setColumn((String) hm.get("column_name"));
			eb.setEntityId((Integer) hm.get("subject_id"));
			al.add(eb);
		}
		return al;
	}

	public ArrayList<DiscrepancyNoteBean> findAllSubjectByStudyAndId(StudyBean study, int subjectId) {
		this.setTypesExpected();
		ArrayList alist;
		this.setTypeExpected(12, TypeNames.STRING);// ss.label
		this.setTypeExpected(13, TypeNames.STRING);// column_name
		this.setTypeExpected(14, TypeNames.INT);// subject_id

		HashMap variables = new HashMap();
		variables.put(1, study.getId());
		variables.put(2, study.getId());
		variables.put(3, study.getId());
		variables.put(4, subjectId);

		alist = this.select(digester.getQuery("findAllSubjectByStudyAndId"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			eb.setSubjectName((String) hm.get("label"));
			eb.setColumn((String) hm.get("column_name"));
			eb.setEntityId((Integer) hm.get("subject_id"));
			al.add(eb);
		}
		return al;
	}

	public ArrayList findAllStudySubjectByStudy(StudyBean study) {
		this.setTypesExpected();
		ArrayList alist;
		this.setTypeExpected(12, TypeNames.STRING);// ss.label
		this.setTypeExpected(13, TypeNames.STRING);// column_name
		this.setTypeExpected(14, TypeNames.INT);// study_subject_id

		HashMap variables = new HashMap();
		variables.put(1, study.getId());
		variables.put(2, study.getId());

		alist = this.select(digester.getQuery("findAllStudySubjectByStudy"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			eb.setSubjectName((String) hm.get("label"));
			eb.setColumn((String) hm.get("column_name"));
			eb.setEntityId((Integer) hm.get("study_subject_id"));
			al.add(eb);
		}
		return al;
	}

	public ArrayList<DiscrepancyNoteBean> findAllStudySubjectByStudyAndId(StudyBean study, int studySubjectId) {
		this.setTypesExpected();
		ArrayList alist;
		this.setTypeExpected(12, TypeNames.STRING);// ss.label
		this.setTypeExpected(13, TypeNames.STRING);// column_name
		this.setTypeExpected(14, TypeNames.INT);// study_subject_id

		HashMap variables = new HashMap();
		variables.put(1, study.getId());
		variables.put(2, study.getId());
		variables.put(3, studySubjectId);

		alist = this.select(digester.getQuery("findAllStudySubjectByStudyAndId"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			eb.setSubjectName((String) hm.get("label"));
			eb.setColumn((String) hm.get("column_name"));
			eb.setEntityId((Integer) hm.get("study_subject_id"));
			al.add(eb);
		}
		return al;
	}

	public ArrayList<DiscrepancyNoteBean> findAllStudySubjectByStudiesAndStudySubjectId(StudyBean currentStudy,
			StudyBean subjectStudy, int studySubjectId) {
		this.setTypesExpected();
		ArrayList alist;
		this.setTypeExpected(12, TypeNames.STRING);// ss.label
		this.setTypeExpected(13, TypeNames.STRING);// column_name
		this.setTypeExpected(14, TypeNames.INT);// study_subject_id

		HashMap variables = new HashMap();
		variables.put(1, currentStudy.getId());
		variables.put(2, subjectStudy.getId());
		variables.put(3, subjectStudy.getId());
		variables.put(4, studySubjectId);

		alist = this.select(digester.getQuery("findAllStudySubjectByStudiesAndStudySubjectId"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			eb.setSubjectName((String) hm.get("label"));
			eb.setColumn((String) hm.get("column_name"));
			eb.setEntityId((Integer) hm.get("study_subject_id"));
			al.add(eb);
		}
		return al;
	}

	public ArrayList<DiscrepancyNoteBean> findAllSubjectByStudiesAndSubjectId(StudyBean currentStudy,
			StudyBean subjectStudy, int studySubjectId) {
		this.setTypesExpected();
		ArrayList alist;
		this.setTypeExpected(12, TypeNames.STRING);// ss.label
		this.setTypeExpected(13, TypeNames.STRING);// column_name
		this.setTypeExpected(14, TypeNames.INT);// subject_id

		HashMap variables = new HashMap();
		variables.put(1, currentStudy.getId());
		variables.put(2, subjectStudy.getId());
		variables.put(3, subjectStudy.getId());
		variables.put(4, currentStudy.getId());
		variables.put(5, subjectStudy.getId());
		variables.put(6, studySubjectId);

		alist = this.select(digester.getQuery("findAllSubjectByStudiesAndSubjectId"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			eb.setSubjectName((String) hm.get("label"));
			eb.setColumn((String) hm.get("column_name"));
			eb.setEntityId((Integer) hm.get("subject_id"));
			al.add(eb);
		}
		return al;
	}

	public ArrayList findAllStudyEventByStudy(StudyBean study) {
		this.setTypesExpected();
		ArrayList alist;
		this.setTypeExpected(12, TypeNames.STRING);// ss.label
		this.setTypeExpected(13, TypeNames.DATE);// date_start
		this.setTypeExpected(14, TypeNames.STRING);// sed_name
		this.setTypeExpected(15, TypeNames.STRING);// column_name
		this.setTypeExpected(16, TypeNames.INT);// study_event_id

		HashMap variables = new HashMap();
		variables.put(1, study.getId());
		variables.put(2, study.getId());
		alist = this.select(digester.getQuery("findAllStudyEventByStudy"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			eb.setSubjectName((String) hm.get("label"));
			eb.setEventName((String) hm.get("sed_name"));
			eb.setEventStart((Date) hm.get("date_start"));
			eb.setColumn((String) hm.get("column_name"));
			eb.setEntityId((Integer) hm.get("study_event_id"));

			al.add(eb);
		}
		return al;
	}

	/**
	 * Find all DiscrepancyNoteBeans associated with a certain Study Subject and Study.
	 * 
	 * @param study
	 *            A StudyBean, whose id property is checked.
	 * @param studySubjectId
	 *            The id of a Study Subject.
	 * @return An ArrayList of DiscrepancyNoteBeans.
	 */
	public ArrayList findAllStudyEventByStudyAndId(StudyBean study, int studySubjectId) {
		this.setTypesExpected();
		ArrayList alist;
		this.setTypeExpected(12, TypeNames.STRING);// ss.label
		this.setTypeExpected(13, TypeNames.DATE);// date_start
		this.setTypeExpected(14, TypeNames.STRING);// sed_name
		this.setTypeExpected(15, TypeNames.STRING);// column_name
		this.setTypeExpected(16, TypeNames.INT);// study_event_id

		HashMap variables = new HashMap();
		variables.put(1, study.getId());
		variables.put(2, study.getId());
		variables.put(3, studySubjectId);
		alist = this.select(digester.getQuery("findAllStudyEventByStudyAndId"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			eb.setSubjectName((String) hm.get("label"));
			eb.setEventName((String) hm.get("sed_name"));
			eb.setEventStart((Date) hm.get("date_start"));
			eb.setColumn((String) hm.get("column_name"));
			eb.setEntityId((Integer) hm.get("study_event_id"));

			al.add(eb);
		}
		return al;
	}

	public ArrayList findAllStudyEventByStudiesAndSubjectId(StudyBean currentStudy, StudyBean subjectStudy,
			int studySubjectId) {
		this.setTypesExpected();
		ArrayList alist;
		this.setTypeExpected(12, TypeNames.STRING);// ss.label
		this.setTypeExpected(13, TypeNames.DATE);// date_start
		this.setTypeExpected(14, TypeNames.STRING);// sed_name
		this.setTypeExpected(15, TypeNames.STRING);// column_name
		this.setTypeExpected(16, TypeNames.INT);// study_event_id

		HashMap variables = new HashMap();
		variables.put(1, currentStudy.getId());
		variables.put(2, subjectStudy.getId());
		variables.put(3, currentStudy.getId());
		variables.put(4, studySubjectId);
		alist = this.select(digester.getQuery("findAllStudyEventByStudiesAndSubjectId"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			eb.setSubjectName((String) hm.get("label"));
			eb.setEventName((String) hm.get("sed_name"));
			eb.setEventStart((Date) hm.get("date_start"));
			eb.setColumn((String) hm.get("column_name"));
			eb.setEntityId((Integer) hm.get("study_event_id"));

			al.add(eb);
		}
		return al;
	}

	public ArrayList findAllEventCRFByStudy(StudyBean study) {
		this.setTypesExpected();
		ArrayList alist;
		this.setTypeExpected(12, TypeNames.STRING);// ss.label
		this.setTypeExpected(13, TypeNames.DATE);// date_start
		this.setTypeExpected(14, TypeNames.STRING);// sed_name
		this.setTypeExpected(15, TypeNames.STRING);// crf_name
		this.setTypeExpected(16, TypeNames.STRING);// column_name
		this.setTypeExpected(17, TypeNames.INT);// event_crf_id

		HashMap variables = new HashMap();
		variables.put(1, study.getId());
		variables.put(2, study.getId());
		alist = this.select(digester.getQuery("findAllEventCRFByStudy"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			eb.setEventName((String) hm.get("sed_name"));
			eb.setEventStart((Date) hm.get("date_start"));
			eb.setCrfName((String) hm.get("crf_name"));
			eb.setSubjectName((String) hm.get("label"));
			eb.setColumn((String) hm.get("column_name"));
			eb.setEntityId((Integer) hm.get("event_crf_id"));
			al.add(eb);
		}
		return al;
	}

	public ArrayList findAllEventCRFByStudyAndParent(StudyBean study, DiscrepancyNoteBean parent) {
		this.setTypesExpected();
		ArrayList alist;
		this.setTypeExpected(12, TypeNames.STRING);// ss.label
		this.setTypeExpected(13, TypeNames.DATE);// date_start
		this.setTypeExpected(14, TypeNames.STRING);// sed_name
		this.setTypeExpected(15, TypeNames.STRING);// crf_name
		this.setTypeExpected(16, TypeNames.STRING);// column_name
		this.setTypeExpected(17, TypeNames.INT);// event_crf_id

		HashMap variables = new HashMap();
		variables.put(1, study.getId());
		variables.put(2, study.getId());
		variables.put(3, parent.getId());

		alist = this.select(digester.getQuery("findAllEventCRFByStudyAndParent"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			eb.setEventName((String) hm.get("sed_name"));
			eb.setEventStart((Date) hm.get("date_start"));
			eb.setCrfName((String) hm.get("crf_name"));
			eb.setSubjectName((String) hm.get("label"));
			eb.setColumn((String) hm.get("column_name"));
			eb.setEntityId((Integer) hm.get("event_crf_id"));
			al.add(eb);
		}
		return al;
	}

	public ArrayList<DiscrepancyNoteBean> findItemDataDNotesFromEventCRF(EventCRFBean eventCRFBean) {

		this.setTypesExpected();
		ArrayList dNotelist;

		HashMap variables = new HashMap();
		variables.put(1, eventCRFBean.getId());
		dNotelist = this.select(digester.getQuery("findItemDataDNotesFromEventCRF"), variables);

		ArrayList<DiscrepancyNoteBean> returnedNotelist = new ArrayList<DiscrepancyNoteBean>();
		for (Object aDNotelist : dNotelist) {
			HashMap hm = (HashMap) aDNotelist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			eb.setEventCRFId(eventCRFBean.getId());
			returnedNotelist.add(eb);
		}
		return returnedNotelist;

	}

	public ArrayList<DiscrepancyNoteBean> findParentItemDataDNotesFromEventCRF(EventCRFBean eventCRFBean) {

		this.setTypesExpected();
		ArrayList dNotelist;

		HashMap variables = new HashMap();
		variables.put(1, eventCRFBean.getId());
		dNotelist = this.select(digester.getQuery("findParentItemDataDNotesFromEventCRF"), variables);

		ArrayList<DiscrepancyNoteBean> returnedNotelist = new ArrayList<DiscrepancyNoteBean>();
		for (Object aDNotelist : dNotelist) {
			HashMap hm = (HashMap) aDNotelist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			eb.setEventCRFId(eventCRFBean.getId());
			returnedNotelist.add(eb);
		}
		return returnedNotelist;

	}

	public ArrayList<DiscrepancyNoteBean> findEventCRFDNotesFromEventCRF(EventCRFBean eventCRFBean) {

		this.setTypesExpected();
		this.setTypeExpected(12, TypeNames.STRING);
		ArrayList dNotelist;

		HashMap variables = new HashMap();
		variables.put(1, eventCRFBean.getId());
		dNotelist = this.select(digester.getQuery("findEventCRFDNotesFromEventCRF"), variables);

		ArrayList<DiscrepancyNoteBean> returnedNotelist = new ArrayList<DiscrepancyNoteBean>();
		for (Object aDNotelist : dNotelist) {
			HashMap hm = (HashMap) aDNotelist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			eb.setColumn((String) hm.get("column_name"));
			eb.setEventCRFId(eventCRFBean.getId());
			returnedNotelist.add(eb);
		}
		return returnedNotelist;

	}

	public ArrayList<DiscrepancyNoteBean> findEventCRFDNotesToolTips(EventCRFBean eventCRFBean) {

		this.setTypesExpected();
		this.setTypeExpected(12, TypeNames.STRING);
		ArrayList dNotelist;

		HashMap variables = new HashMap();
		variables.put(1, eventCRFBean.getId());
		variables.put(2, eventCRFBean.getId());
		variables.put(3, eventCRFBean.getId());
		variables.put(4, eventCRFBean.getId());
		variables.put(5, eventCRFBean.getId());
		variables.put(6, eventCRFBean.getId());
		variables.put(7, eventCRFBean.getId());
		variables.put(8, eventCRFBean.getId());
		variables.put(9, eventCRFBean.getId());
		variables.put(10, eventCRFBean.getId());

		dNotelist = this.select(digester.getQuery("findEventCRFDNotesForToolTips"), variables);

		ArrayList<DiscrepancyNoteBean> returnedNotelist = new ArrayList<DiscrepancyNoteBean>();
		for (Object aDNotelist : dNotelist) {
			HashMap hm = (HashMap) aDNotelist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			eb.setColumn((String) hm.get("column_name"));
			eb.setEventCRFId(eventCRFBean.getId());
			returnedNotelist.add(eb);
		}
		return returnedNotelist;

	}

	public ArrayList<DiscrepancyNoteBean> findAllDNotesByItemNameAndEventCRF(EventCRFBean eventCRFBean, String itemName) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, eventCRFBean.getId());
		variables.put(2, itemName);
		ArrayList dNotelist;

		dNotelist = this.select(digester.getQuery("findAllDNotesByItemNameAndEventCRF"), variables);

		ArrayList<DiscrepancyNoteBean> returnedNotelist = new ArrayList<DiscrepancyNoteBean>();
		for (Object aDNotelist : dNotelist) {
			HashMap hm = (HashMap) aDNotelist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			// eb.setColumn((String) hm.get("column_name"));
			// eb.setEventCRFId(eventCRFBean.getId());
			returnedNotelist.add(eb);
		}
		return returnedNotelist;

	}

	public ArrayList findAllItemDataByStudy(StudyBean study) {
		this.setTypesExpected();
		ArrayList alist;
		this.setTypeExpected(12, TypeNames.STRING);// ss.label
		this.setTypeExpected(13, TypeNames.DATE);// date_start
		this.setTypeExpected(14, TypeNames.STRING);// sed_name
		this.setTypeExpected(15, TypeNames.STRING);// crf_name
		this.setTypeExpected(16, TypeNames.STRING);// item_name
		this.setTypeExpected(17, TypeNames.STRING);// value
		this.setTypeExpected(18, TypeNames.INT);// item_data_id
		this.setTypeExpected(19, TypeNames.INT);// item_id

		HashMap variables = new HashMap();
		variables.put(1, study.getId());
		variables.put(2, study.getId());
		alist = this.select(digester.getQuery("findAllItemDataByStudy"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			eb.setEventName((String) hm.get("sed_name"));
			eb.setEventStart((Date) hm.get("date_start"));
			eb.setCrfName((String) hm.get("crf_name"));
			eb.setSubjectName((String) hm.get("label"));
			eb.setEntityName((String) hm.get("item_name"));
			eb.setEntityValue((String) hm.get("value"));
			// YW << change EntityId from item_id to item_data_id.
			eb.setEntityId((Integer) hm.get("item_data_id"));
			eb.setItemId((Integer) hm.get("item_id"));
			// YW >>
			al.add(eb);
		}
		return al;
	}

	public ArrayList findAllItemDataByStudy(StudyBean study, Set<String> hiddenCrfNames) {
		this.setTypesExpected();
		ArrayList al = new ArrayList();
		this.setTypeExpected(12, TypeNames.STRING);// ss.label
		this.setTypeExpected(13, TypeNames.DATE);// date_start
		this.setTypeExpected(14, TypeNames.INT);// sed_id
		this.setTypeExpected(15, TypeNames.STRING);// sed_name
		this.setTypeExpected(16, TypeNames.STRING);// crf_name
		this.setTypeExpected(17, TypeNames.STRING);// item_name
		this.setTypeExpected(18, TypeNames.STRING);// value
		this.setTypeExpected(19, TypeNames.INT);// item_data_id
		this.setTypeExpected(20, TypeNames.INT);// item_id

		HashMap variables = new HashMap();
		variables.put(1, study.getId());
		variables.put(2, study.getId());
		ArrayList alist = this.select(digester.getQuery("findAllItemDataByStudy"), variables);
		Iterator it = alist.iterator();

		if (hiddenCrfNames.size() > 0) {
			while (it.hasNext()) {
				HashMap hm = (HashMap) it.next();
				Integer sedId = (Integer) hm.get("sed_id");
				String crfName = (String) hm.get("crf_name");
				if (!hiddenCrfNames.contains(sedId + "_" + crfName)) {
					DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
					eb.setEventName((String) hm.get("sed_name"));
					eb.setEventStart((Date) hm.get("date_start"));
					eb.setCrfName(crfName);
					eb.setSubjectName((String) hm.get("label"));
					eb.setEntityName((String) hm.get("item_name"));
					eb.setEntityValue((String) hm.get("value"));
					eb.setEntityId((Integer) hm.get("item_data_id"));
					eb.setItemId((Integer) hm.get("item_id"));
					al.add(eb);
				}
			}
		} else {
			while (it.hasNext()) {
				HashMap hm = (HashMap) it.next();
				DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
				eb.setEventName((String) hm.get("sed_name"));
				eb.setEventStart((Date) hm.get("date_start"));
				eb.setCrfName((String) hm.get("crf_name"));
				eb.setSubjectName((String) hm.get("label"));
				eb.setEntityName((String) hm.get("item_name"));
				eb.setEntityValue((String) hm.get("value"));
				eb.setEntityId((Integer) hm.get("item_data_id"));
				eb.setItemId((Integer) hm.get("item_id"));
				al.add(eb);
			}
		}
		return al;
	}

	public Integer countAllItemDataByStudyAndUser(StudyBean study, UserAccountBean user) {
		this.setTypesExpected();
		this.setTypeExpected(12, TypeNames.STRING);// ss.label
		this.setTypeExpected(13, TypeNames.DATE);// date_start
		this.setTypeExpected(14, TypeNames.STRING);// sed_name
		this.setTypeExpected(15, TypeNames.STRING);// crf_name
		this.setTypeExpected(16, TypeNames.STRING);// item_name
		this.setTypeExpected(17, TypeNames.STRING);// value
		this.setTypeExpected(18, TypeNames.INT);// item_data_id
		this.setTypeExpected(19, TypeNames.INT);// item_id

		HashMap variables = new HashMap();
		variables.put(1, study.getId());
		variables.put(2, study.getId());
		variables.put(3, user.getId());

		ArrayList rows = this.select(digester.getQuery("countAllItemDataByStudyAndUser"), variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return null;
		}
	}

	public ArrayList findAllItemDataByStudyAndParent(StudyBean study, DiscrepancyNoteBean parent) {
		this.setTypesExpected();
		ArrayList alist;
		this.setTypeExpected(12, TypeNames.STRING);// ss.label
		this.setTypeExpected(13, TypeNames.DATE);// date_start
		this.setTypeExpected(14, TypeNames.STRING);// sed_name
		this.setTypeExpected(15, TypeNames.STRING);// crf_name
		this.setTypeExpected(16, TypeNames.STRING);// item_name
		this.setTypeExpected(17, TypeNames.STRING);// value
		this.setTypeExpected(18, TypeNames.INT);// item_data_id
		this.setTypeExpected(19, TypeNames.INT);// item_id

		HashMap variables = new HashMap();
		variables.put(1, study.getId());
		variables.put(2, study.getId());
		variables.put(3, parent.getId());
		alist = this.select(digester.getQuery("findAllItemDataByStudyAndParent"), variables);

		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			eb.setEventName((String) hm.get("sed_name"));
			eb.setEventStart((Date) hm.get("date_start"));
			eb.setCrfName((String) hm.get("crf_name"));
			eb.setSubjectName((String) hm.get("label"));
			eb.setEntityName((String) hm.get("item_name"));
			eb.setEntityValue((String) hm.get("value"));
			// YW << change EntityId from item_id to item_data_id.
			eb.setEntityId((Integer) hm.get("item_data_id"));
			eb.setItemId((Integer) hm.get("item_id"));
			// YW >>
			al.add(eb);
		}
		return al;
	}

	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	public EntityBean findByPK(int ID) {
		DiscrepancyNoteBean eb = new DiscrepancyNoteBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, ID);

		String sql = digester.getQuery("findByPK");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (DiscrepancyNoteBean) this.getEntityFromHashMap((HashMap) it.next());
		}

		if (fetchMapping) {
			eb = findSingleMapping(eb);
		}

		return eb;
	}

	/**
	 * Creates a new discrepancy note
	 */
	public EntityBean create(EntityBean eb) {
		return create(eb, null);
	}

	public EntityBean create(EntityBean eb, Connection connection) {
		DiscrepancyNoteBean sb = (DiscrepancyNoteBean) eb;
		HashMap variables = new HashMap();
		HashMap nullVars = new HashMap();
		variables.put(1, sb.getDescription());
		variables.put(2, sb.getDiscrepancyNoteTypeId());
		variables.put(3, sb.getResolutionStatusId());
		variables.put(4, sb.getDetailedNotes());

		variables.put(5, sb.getOwner().getId());
		if (sb.getParentDnId() == 0) {
			nullVars.put(6, Types.INTEGER);
			variables.put(6, null);
		} else {
			variables.put(6, sb.getParentDnId());
		}
		variables.put(7, sb.getEntityType());
		variables.put(8, sb.getStudyId());
		if (sb.getAssignedUserId() == 0) {
			nullVars.put(9, Types.INTEGER);
			variables.put(9, null);
		} else {
			variables.put(9, sb.getAssignedUserId());
		}

		this.executeWithPK(digester.getQuery("create"), variables, nullVars, connection);
		if (isQuerySuccessful()) {
			sb.setId(getLatestPK());
		}

		return sb;
	}

	/**
	 * Creates a new discrepancy note map
	 */
	public void createMapping(DiscrepancyNoteBean eb) {
		createMapping(eb, null);
	}

	public void createMapping(DiscrepancyNoteBean eb, Connection connection) {
		HashMap variables = new HashMap();
		variables.put(1, eb.getEntityId());
		variables.put(2, eb.getId());
		variables.put(3, eb.getColumn());
		String entityType = eb.getEntityType();

		if ("subject".equalsIgnoreCase(entityType)) {
			this.execute(digester.getQuery("createSubjectMap"), variables, connection);
		} else if ("studySub".equalsIgnoreCase(entityType)) {
			this.execute(digester.getQuery("createStudySubjectMap"), variables, connection);
		} else if ("eventCrf".equalsIgnoreCase(entityType)) {
			this.execute(digester.getQuery("createEventCRFMap"), variables, connection);
		} else if ("studyEvent".equalsIgnoreCase(entityType)) {
			this.execute(digester.getQuery("createStudyEventMap"), variables, connection);
		} else if ("itemData".equalsIgnoreCase(entityType)) {
			this.execute(digester.getQuery("createItemDataMap"), variables, connection);
		}

	}

	/**
	 * Updates a Study event
	 */
	public EntityBean update(EntityBean eb) {
		DiscrepancyNoteBean dnb = (DiscrepancyNoteBean) eb;
		dnb.setActive(false);

		HashMap variables = new HashMap();

		variables.put(1, dnb.getDescription());
		variables.put(2, dnb.getDiscrepancyNoteTypeId());
		variables.put(3, dnb.getResolutionStatusId());
		variables.put(4, dnb.getDetailedNotes());
		variables.put(5, dnb.getId());
		this.execute(digester.getQuery("update"), variables);

		if (isQuerySuccessful()) {
			dnb.setActive(true);
		}

		return dnb;
	}

	public EntityBean updateAssignedUser(EntityBean eb) {
		DiscrepancyNoteBean dnb = (DiscrepancyNoteBean) eb;
		dnb.setActive(false);

		HashMap variables = new HashMap();

		variables.put(1, dnb.getAssignedUserId());
		variables.put(2, dnb.getId());
		this.execute(digester.getQuery("updateAssignedUser"), variables);

		if (isQuerySuccessful()) {
			dnb.setActive(true);
		}

		return dnb;
	}

	public EntityBean updateAssignedUserToNull(EntityBean eb) {
		DiscrepancyNoteBean dnb = (DiscrepancyNoteBean) eb;
		dnb.setActive(false);

		HashMap variables = new HashMap();

		variables.put(1, dnb.getId());
		this.execute(digester.getQuery("updateAssignedUserToNull"), variables);

		if (isQuerySuccessful()) {
			dnb.setActive(true);
		}

		return dnb;
	}

	public void deleteNotes(int id) {
		HashMap<Integer, Comparable> variables = new HashMap<Integer, Comparable>();
		variables.put(1, id);
		this.execute(digester.getQuery("deleteNotes"), variables);
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
		return new ArrayList();
	}

	@Override
	public int getCurrentPK() {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		int pk = 0;
		ArrayList al = select(digester.getQuery("getCurrentPrimaryKey"));

		if (al.size() > 0) {
			HashMap h = (HashMap) al.get(0);
			pk = (Integer) h.get("key");
		}

		return pk;
	}

	public ArrayList findAllByParent(DiscrepancyNoteBean parent) {
		HashMap variables = new HashMap();
		variables.put(1, parent.getId());

		return this.executeFindAllQuery("findAllByParent", variables);
	}

	public ArrayList findAllByStudyEvent(StudyEventBean studyEvent) {
		HashMap variables = new HashMap();
		variables.put(1, studyEvent.getId());

		return this.executeFindAllQuery("findByStudyEvent", variables);
	}

	public List<Integer> findAllDnIdsByStudyEvent(int studyEventId) {
		List<Integer> result = new ArrayList<Integer>();
		unsetTypeExpected();
		setTypeExpected(1, TypeNames.INT);
		HashMap variables = new HashMap();
		variables.put(1, studyEventId);
		for (Object o : select(digester.getQuery("findAllDnIdsByStudyEvent"), variables)) {
			result.add((Integer) ((HashMap) o).get("discrepancy_note_id"));
		}
		return result;
	}

	public ArrayList findAllByStudyEventWithConstraints(StudyEventBean studyEvent, StringBuffer constraints) {
		this.setTypesExpected();
		ArrayList answer = new ArrayList();
		HashMap variables = new HashMap();
		variables.put(1, studyEvent.getId());
		String sql = digester.getQuery("findByStudyEvent");
		sql += constraints.toString();
		for (Object o : this.select(sql, variables)) {
			answer.add(this.getEntityFromHashMap((HashMap) o));
		}
		return answer;
	}

	public HashMap<ResolutionStatus, Integer> findAllByStudyEventWithConstraints(StudyEventBean studyEvent,
			StringBuffer constraints, boolean isSite) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, studyEvent.getId());
		String sql = digester.getQuery("findByStudyEvent");
		sql += constraints.toString();
		if (isSite) {
			if ("oracle".equalsIgnoreCase(CoreResources.getDBName())) {
				sql += " AND ec.crf_version_id not in (select cv.crf_version_id from crf_version cv where cv.crf_id in ("
						+ "select edc.crf_id from event_definition_crf edc, study_event se where se.study_event_id = "
						+ studyEvent.getId()
						+ " AND edc.study_event_definition_id = se.study_event_definition_id AND edc.hide_crf = 1"
						+ " AND edc.event_definition_crf_id not in ("
						+ "select parent_id from event_definition_crf where study_event_definition_id = se.study_event_definition_id and parent_id > 0)) )";
			} else {
				sql += " AND ec.crf_version_id not in (select cv.crf_version_id from crf_version cv where cv.crf_id in ("
						+ "select edc.crf_id from event_definition_crf edc, study_event se where se.study_event_id = "
						+ studyEvent.getId()
						+ " AND edc.study_event_definition_id = se.study_event_definition_id AND edc.hide_crf = 'true'"
						+ " AND edc.event_definition_crf_id not in ("
						+ "select parent_id from event_definition_crf where study_event_definition_id = se.study_event_definition_id and parent_id > 0)) )";

			}
		}
		sql += " group By  dn.resolution_status_id ";
		Iterator it = this.select(sql, variables).iterator();
		HashMap<ResolutionStatus, Integer> discCounts = new HashMap<ResolutionStatus, Integer>();
		while (it.hasNext()) {
			HashMap h = (HashMap) it.next();
			Integer resolutionStatusId = (Integer) h.get("resolution_status_id");
			Integer count = (Integer) h.get("count");
			discCounts.put(ResolutionStatus.get(resolutionStatusId), count);
		}
		return discCounts;
	}

	public HashMap<ResolutionStatus, Integer> countByEntityTypeAndStudyEventWithConstraints(String entityType,
			StudyEventBean studyEvent, StringBuffer constraints, boolean isSite) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.INT);
		// ArrayList answer = new ArrayList();
		HashMap variables = new HashMap();
		variables.put(1, studyEvent.getId());
		String sql = "";
		String temp = "";
		if ("itemData".equalsIgnoreCase(entityType)) {
			sql = digester.getQuery("findByStudyEvent");
			temp = " and (dn.entity_type='itemData' or dn.entity_type='ItemData') ";
			if (isSite) {
				if ("oracle".equalsIgnoreCase(CoreResources.getDBName())) {
					temp += " AND ec.crf_version_id not in (select cv.crf_version_id from crf_version cv where cv.crf_id in ("
							+ "select edc.crf_id from event_definition_crf edc, study_event se where se.study_event_id = "
							+ studyEvent.getId()
							+ " AND edc.study_event_definition_id = se.study_event_definition_id AND edc.hide_crf = 1"
							+ " AND edc.event_definition_crf_id not in ("
							+ "select parent_id from event_definition_crf where study_event_definition_id = se.study_event_definition_id and parent_id > 0)) )";
				} else {
					temp += " AND ec.crf_version_id not in (select cv.crf_version_id from crf_version cv where cv.crf_id in ("
							+ "select edc.crf_id from event_definition_crf edc, study_event se where se.study_event_id = "
							+ studyEvent.getId()
							+ " AND edc.study_event_definition_id = se.study_event_definition_id AND edc.hide_crf = 'true'"
							+ " AND edc.event_definition_crf_id not in ("
							+ "select parent_id from event_definition_crf where study_event_definition_id = se.study_event_definition_id and parent_id > 0)) )";

				}
			}
		} else if ("eventCrf".equalsIgnoreCase(entityType)) {
			sql = digester.getQuery("countByEventCrfTypeAndStudyEvent");
			temp = " and dn.entity_type='eventCrf' ";
			if (isSite) {
				if ("oracle".equalsIgnoreCase(CoreResources.getDBName())) {
					temp += " AND ec.crf_version_id not in (select cv.crf_version_id from crf_version cv where cv.crf_id in ("
							+ "select edc.crf_id from event_definition_crf edc, study_event se where se.study_event_id = "
							+ studyEvent.getId()
							+ " AND edc.study_event_definition_id = se.study_event_definition_id AND edc.hide_crf = 1"
							+ " AND edc.event_definition_crf_id not in ("
							+ "select parent_id from event_definition_crf where study_event_definition_id = se.study_event_definition_id and parent_id > 0)) )";
				} else {
					temp += " AND ec.crf_version_id not in (select cv.crf_version_id from crf_version cv where cv.crf_id in ("
							+ "select edc.crf_id from event_definition_crf edc, study_event se where se.study_event_id = "
							+ studyEvent.getId()
							+ " AND edc.study_event_definition_id = se.study_event_definition_id AND edc.hide_crf = 'true'"
							+ " AND edc.event_definition_crf_id not in ("
							+ "select parent_id from event_definition_crf where study_event_definition_id = se.study_event_definition_id and parent_id > 0)) )";

				}
			}
		} else if ("studyEvent".equalsIgnoreCase(entityType)) {
			sql = digester.getQuery("countByStudyEventTypeAndStudyEvent");
			temp = " and dn.entity_type='studyEvent' ";
		} else if ("studySub".equalsIgnoreCase(entityType)) {
			sql = digester.getQuery("countByStudySubjectTypeAndStudyEvent");
			temp = " and dn.entity_type='studySub' ";
		} else if ("subject".equalsIgnoreCase(entityType)) {
			sql = digester.getQuery("countBySubjectTypeAndStudyEvent");
			temp = " and dn.entity_type='subject' ";
		}
		sql += temp;
		sql += constraints.toString();
		sql += " group By  dn.resolution_status_id ";
		Iterator it = this.select(sql, variables).iterator();
		HashMap<ResolutionStatus, Integer> discCounts = new HashMap<ResolutionStatus, Integer>();
		while (it.hasNext()) {
			HashMap h = (HashMap) it.next();
			Integer resolutionStatusId = (Integer) h.get("resolution_status_id");
			Integer count = (Integer) h.get("count");
			discCounts.put(ResolutionStatus.get(resolutionStatusId), count);
		}
		return discCounts;
	}

	private DiscrepancyNoteBean findSingleMapping(DiscrepancyNoteBean note) {
		HashMap variables = new HashMap();
		variables.put(1, note.getId());

		setMapTypesExpected();
		String entityType = note.getEntityType();
		String sql = "";
		if ("subject".equalsIgnoreCase(entityType)) {
			sql = digester.getQuery("findSubjectMapByDNId");
		} else if ("studySub".equalsIgnoreCase(entityType)) {
			sql = digester.getQuery("findStudySubjectMapByDNId");
		} else if ("eventCrf".equalsIgnoreCase(entityType)) {
			sql = digester.getQuery("findEventCRFMapByDNId");
		} else if ("studyEvent".equalsIgnoreCase(entityType)) {
			sql = digester.getQuery("findStudyEventMapByDNId");
		} else if ("itemData".equalsIgnoreCase(entityType)) {
			sql = digester.getQuery("findItemDataMapByDNId");
			this.unsetTypeExpected();
			this.setTypeExpected(1, TypeNames.INT);
			this.setTypeExpected(2, TypeNames.INT);
			this.setTypeExpected(3, TypeNames.STRING);
			this.setTypeExpected(4, TypeNames.INT);
		}

		ArrayList hms = select(sql, variables);

		if (hms.size() > 0) {
			HashMap hm = (HashMap) hms.get(0);
			note = getMappingFromHashMap(hm, note);
		}

		return note;
	}

	private DiscrepancyNoteBean getMappingFromHashMap(HashMap hm, DiscrepancyNoteBean note) {
		String entityType = note.getEntityType();
		String entityIDColumn = getEntityIDColumn(entityType);

		if (!entityIDColumn.equals("")) {
			note.setEntityId(selectInt(hm, entityIDColumn));
		}
		note.setColumn(selectString(hm, "column_name"));
		return note;
	}

	public static String getEntityIDColumn(String entityType) {
		String entityIDColumn = "";
		if ("subject".equalsIgnoreCase(entityType)) {
			entityIDColumn = "subject_id";
		} else if ("studySub".equalsIgnoreCase(entityType)) {
			entityIDColumn = "study_subject_id";
		} else if ("eventCrf".equalsIgnoreCase(entityType)) {
			entityIDColumn = "event_crf_id";
		} else if ("studyEvent".equalsIgnoreCase(entityType)) {
			entityIDColumn = "study_event_id";
		} else if ("itemData".equalsIgnoreCase(entityType)) {
			entityIDColumn = "item_data_id";
		}
		return entityIDColumn;
	}

	public AuditableEntityBean findEntity(DiscrepancyNoteBean note) {
		AuditableEntityDAO aedao = getAEDAO(note, ds);
		try {
			if (aedao != null) {
				return (AuditableEntityBean) aedao.findByPK(note.getEntityId());
			}
		} catch (Exception e) {
			//
		}
		return null;
	}

	public static AuditableEntityDAO getAEDAO(DiscrepancyNoteBean note, DataSource ds) {
		String entityType = note.getEntityType();
		if ("subject".equalsIgnoreCase(entityType)) {
			return new SubjectDAO(ds);
		} else if ("studySub".equalsIgnoreCase(entityType)) {
			return new StudySubjectDAO(ds);
		} else if ("eventCrf".equalsIgnoreCase(entityType)) {
			return new EventCRFDAO(ds);
		} else if ("studyEvent".equalsIgnoreCase(entityType)) {
			return new StudyEventDAO(ds);
		} else if ("itemData".equalsIgnoreCase(entityType)) {
			return new ItemDataDAO(ds);
		}

		return null;
	}

	public int findNumExistingNotesForItem(int itemDataId) {
		unsetTypeExpected();
		setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, itemDataId);
		String sql = digester.getQuery("findNumExistingNotesForItem");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			HashMap hm = (HashMap) it.next();
			try {
				return (Integer) hm.get("num");
			} catch (Exception e) {
				//
			}
		}

		return 0;
	}

	public ArrayList findExistingNotesForItemData(int itemDataId) {
		this.setTypesExpected();
		ArrayList alist;
		HashMap variables = new HashMap();
		variables.put(1, itemDataId);
		alist = this.select(digester.getQuery("findExistingNotesForItemData"), variables);
		ArrayList<DiscrepancyNoteBean> al = new ArrayList<DiscrepancyNoteBean>();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			al.add(eb);
		}
		return al;

	}

	public ArrayList findExistingNotesForToolTip(int itemDataId) {
		this.setTypesExpected();
		ArrayList alist;
		HashMap variables = new HashMap();
		variables.put(1, itemDataId);
		variables.put(2, itemDataId);
		variables.put(3, itemDataId);
		variables.put(4, itemDataId);
		alist = this.select(digester.getQuery("findExistingNotesForToolTip"), variables);
		ArrayList<DiscrepancyNoteBean> al = new ArrayList<DiscrepancyNoteBean>();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			al.add(eb);
		}

		return al;

	}

	public ArrayList findParentNotesForToolTip(int itemDataId) {
		this.setTypesExpected();
		ArrayList alist;
		HashMap variables = new HashMap();
		variables.put(1, itemDataId);
		variables.put(2, itemDataId);

		alist = this.select(digester.getQuery("findParentNotesForToolTip"), variables);
		ArrayList<DiscrepancyNoteBean> al = new ArrayList<DiscrepancyNoteBean>();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			al.add(eb);
		}
		return al;

	}

	public ArrayList<DiscrepancyNoteBean> findAllTopNotesByEventCRF(int eventCRFId) {
		this.setTypesExpected();
		this.setTypeExpected(12, TypeNames.INT);
		ArrayList alist;
		HashMap variables = new HashMap();
		variables.put(1, eventCRFId);
		alist = this.select(digester.getQuery("findAllTopNotesByEventCRF"), variables);
		ArrayList<DiscrepancyNoteBean> al = new ArrayList<DiscrepancyNoteBean>();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			al.add(eb);
		}
		return al;
	}

	public ArrayList<DiscrepancyNoteBean> findOnlyParentEventCRFDNotesFromEventCRF(EventCRFBean eventCRFBean) {
		this.setTypesExpected();
		this.setTypeExpected(12, TypeNames.STRING);
		ArrayList dNotelist;

		HashMap variables = new HashMap();
		variables.put(1, eventCRFBean.getId());
		dNotelist = this.select(digester.getQuery("findOnlyParentEventCRFDNotesFromEventCRF"), variables);

		ArrayList<DiscrepancyNoteBean> returnedNotelist = new ArrayList<DiscrepancyNoteBean>();
		for (Object aDNotelist : dNotelist) {
			HashMap hm = (HashMap) aDNotelist;
			DiscrepancyNoteBean eb = (DiscrepancyNoteBean) this.getEntityFromHashMap(hm);
			eb.setColumn((String) hm.get("column_name"));
			eb.setEventCRFId(eventCRFBean.getId());
			returnedNotelist.add(eb);
		}
		return returnedNotelist;
	}

	public String findSiteHiddenEventCrfIdsString(StudyBean site) {
		String sql;
		String valueOfBooleanTrue = ("oracle".equalsIgnoreCase(CoreResources.getDBName())) ? "1" : "'true'";
	
		sql = "SELECT DISTINCT ec.event_crf_id " 
				+ "FROM (((event_crf ec LEFT JOIN study_event se ON ec.study_event_id = se.study_event_id) "
				+ "LEFT JOIN crf_version cv ON ec.crf_version_id = cv.crf_version_id) "
				+ "LEFT JOIN study_subject ss ON ec.study_subject_id = ss.study_subject_id) "
				+ "LEFT JOIN (SELECT edc.study_id, edc.study_event_definition_id, edc.crf_id "
							+ "FROM event_definition_crf edc "
							+ "WHERE (edc.study_id = " + site.getId() + " OR edc.study_id = (SELECT s.parent_study_id FROM study s WHERE s.study_id = " + site.getId() + ")) "
								+ "AND edc.status_id = 1 " 
								+ "AND edc.hide_crf = " + valueOfBooleanTrue + ") sedc ON cv.crf_id = sedc.crf_id "
				+ "WHERE ec.status_id NOT IN (5,7) "
					+ "AND se.study_event_definition_id = sedc.study_event_definition_id " 
					+ "AND (ss.study_id = " + site.getId() + ")";
		
		return sql;
	}

	public EntityBean findLatestChildByParent(int parentId) {
		DiscrepancyNoteBean eb = new DiscrepancyNoteBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, parentId);
		variables.put(2, parentId);

		String sql = digester.getQuery("findLatestChildByParent");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (DiscrepancyNoteBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return eb;
	}

	public int getResolutionStatusIdForSubjectDNFlag(int subjectId, String column) {
		int id = 0;
		unsetTypeExpected();
		setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, subjectId);
		variables.put(2, column);

		String sql = digester.getQuery("getResolutionStatusIdForSubjectDNFlag");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();
		if (it.hasNext()) {
			HashMap hm = (HashMap) it.next();
			try {
				id = (Integer) hm.get("resolution_status_id");
			} catch (Exception e) {
				//
			}
		}
		return id;
	}

	public boolean doesNotHaveOutstandingDNs(StudySubjectBean ssb) {
		Integer count = null;
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, ssb.getId());

		ArrayList rows = select(digester.getQuery("countOfOutstandingDNsForStudySubject"), variables);
		Iterator it = rows.iterator();
		if (it.hasNext()) {
			count = (Integer) ((HashMap) it.next()).get("count");
		}

		return count != null && count == 0;
	}

	public boolean doesNotHaveOutstandingDNs(StudyEventBean seb) {
		Integer count = null;
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, seb.getId());

		ArrayList rows = select(digester.getQuery("countOfOutstandingDNsForStudyEvent"), variables);
		rows.addAll(select(digester.getQuery("countOfOutstandingDNsForStudyEventFromStudyEventMap"), variables));
		for (Object row : rows) {
			count = (Integer) ((HashMap) row).get("count");
			if (count != null && count > 0) {
				break;
			}
		}

		return count != null && count == 0;
	}

	public boolean doesSubjectHasSomeNDsInStudy(StudyBean study, String subjectLabel, String resolutionStatus) {
		ListNotesFilter listNotesFilter = new ListNotesFilter();
		listNotesFilter.addFilter("studySubject.label", subjectLabel);
		listNotesFilter.addFilter("discrepancyNoteBean.resolutionStatus", resolutionStatus);
		List<DiscrepancyNoteBean> noteBeans = this.getViewNotesWithFilterAndSortLimits(study, listNotesFilter,
				new ListNotesSort(), 0, 100);
		return noteBeans.size() > 0;
	}

	public boolean doesSubjectHasNewNDsInStudy(StudyBean study, String subjectLabel) {
		return doesSubjectHasSomeNDsInStudy(study, subjectLabel, "1");
	}

	public boolean doesSubjectHasUnclosedNDsInStudy(StudyBean study, String subjectLabel) {
		return doesSubjectHasSomeNDsInStudy(study, subjectLabel, "123");
	}

	public boolean doesEventHasSomeNDsInStudy(StudyBean study, String eventLabel, int eventId, String subjectLabel,
			String resolutionStatus) {
		ListNotesFilter listNotesFilter = new ListNotesFilter();
		listNotesFilter.addFilter("eventId", eventId);
		listNotesFilter.addFilter("eventName", eventLabel);
		listNotesFilter.addFilter("studySubject.label", subjectLabel);
		listNotesFilter.addFilter("discrepancyNoteBean.resolutionStatus", resolutionStatus);
		List<DiscrepancyNoteBean> noteBeans = this.getViewNotesWithFilterAndSortLimits(study, listNotesFilter,
				new ListNotesSort(), 0, 100);
		return noteBeans.size() > 0;
	}

	public boolean doesEventHasNewNDsInStudy(StudyBean study, String eventLabel, int eventId, String subjectLabel) {
		return doesEventHasSomeNDsInStudy(study, eventLabel, eventId, subjectLabel, "1");
	}

	public boolean doesEventHasUnclosedNDsInStudy(StudyBean study, String eventLabel, int eventId, String subjectLabel) {
		return doesEventHasSomeNDsInStudy(study, eventLabel, eventId, subjectLabel, "123");
	}

	public boolean doesCRFHasSomeNDsInStudyForSubject(StudyBean study, String eventLabel, int eventId,
			String subjectLabel, String crfName, String resolutionStatus) {
		ListNotesFilter listNotesFilter = new ListNotesFilter();
		listNotesFilter.addFilter("eventId", eventId);
		listNotesFilter.addFilter("crfName", crfName);
		listNotesFilter.addFilter("eventName", eventLabel);
		listNotesFilter.addFilter("studySubject.label", subjectLabel);
		listNotesFilter.addFilter("discrepancyNoteBean.resolutionStatus", resolutionStatus);
		List<DiscrepancyNoteBean> noteBeans = this.getViewNotesWithFilterAndSortLimits(study, listNotesFilter,
				new ListNotesSort(), 0, 100);
		return noteBeans.size() > 0;
	}

	public boolean doesCRFHasNewNDsInStudyForSubject(StudyBean study, String eventLabel, int eventId,
			String subjectLabel, String crfName) {
		return doesCRFHasSomeNDsInStudyForSubject(study, eventLabel, eventId, subjectLabel, crfName, "1");
	}

	public boolean doesCRFHasUnclosedNDsInStudyForSubject(StudyBean study, String eventLabel, int eventId,
			String subjectLabel, String crfName) {
		return doesCRFHasSomeNDsInStudyForSubject(study, eventLabel, eventId, subjectLabel, crfName, "123");
	}

	public Integer countAllByStudyEventTypeAndStudyEvent(StudyEventBean studyEvent) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, studyEvent.getId());

		ArrayList rows = select(digester.getQuery("countAllByStudyEventTypeAndStudyEvent"), variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			Integer count = (Integer) ((HashMap) it.next()).get("count");
			return count == null ? 0 : count;
		} else {
			return 0;
		}
	}

	public int countViewNotesByStatusId(int studyId, int statusId) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		HashMap variables = new HashMap();
		variables.put(1, statusId);
		variables.put(2, studyId);
		variables.put(3, studyId);

		ArrayList rows = select(digester.getQuery("countViewNotesByStatusId"), variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			Integer count = (Integer) ((HashMap) it.next()).get("count");
			return count == null ? 0 : count;
		} else {
			return 0;
		}
	}
}
