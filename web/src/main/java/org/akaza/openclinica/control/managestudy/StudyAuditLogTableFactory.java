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

package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.AbstractTableFactory;
import org.akaza.openclinica.control.DefaultActionsEditor;
import org.akaza.openclinica.dao.hibernate.AuditUserLoginDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyAuditLogFilter;
import org.akaza.openclinica.dao.managestudy.StudyAuditLogSort;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.jmesa.core.filter.DateFilterMatcher;
import org.jmesa.core.filter.FilterMatcher;
import org.jmesa.core.filter.MatcherKey;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Filter;
import org.jmesa.limit.FilterSet;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.view.component.Row;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.editor.DateCellEditor;
import org.jmesa.view.html.editor.DroplistFilterEditor;
import org.jmesa.view.html.HtmlBuilder;

@SuppressWarnings({"unchecked"})
public class StudyAuditLogTableFactory extends AbstractTableFactory {

	private AuditUserLoginDao auditUserLoginDao;
	private StudySubjectDAO studySubjectDao;
	private UserAccountDAO userAccountDao;
	private SubjectDAO subjectDao;
	private StudyBean currentStudy;
	private ResourceBundle resword;
	private ResourceBundle resformat;

	@Override
	protected String getTableName() {
		return "studyAuditLogs";
	}

	@Override
	protected void configureColumns(TableFacade tableFacade, Locale locale) {
		tableFacade.setColumnProperties("studySubject.label", "studySubject.secondaryLabel", "studySubject.oid",
				"subject.dateOfBirth", "subject.uniqueIdentifier", "studySubject.owner", "studySubject.status",
				"actions");
		Row row = tableFacade.getTable().getRow();
		StudyBean currentStudy = (StudyBean) tableFacade.getWebContext().getSessionAttribute("study");
		configureColumn(row.getColumn("studySubject.label"),
				currentStudy == null ? resword.getString("study_subject_ID") : currentStudy.getStudyParameterConfig()
						.getStudySubjectIdLabel(), null, null);

        if (currentStudy.getStudyParameterConfig().getSecondaryIdRequired().equalsIgnoreCase("not_used")) {
            configureColumn(row.getColumn("studySubject.secondaryLabel"), resword.getString("secondary_subject_ID"), null, null);
        } else {
            configureColumn(row.getColumn("studySubject.secondaryLabel"), currentStudy.getStudyParameterConfig().getSecondaryIdLabel(), null, null);
        }

        configureColumn(row.getColumn("studySubject.oid"), resword.getString("study_subject_oid"), null, null);
		configureColumn(row.getColumn("subject.dateOfBirth"), resword.getString("date_of_birth"), new DateCellEditor(
				getDateFormat()), null);
		configureColumn(row.getColumn("subject.uniqueIdentifier"), resword.getString("person_ID"), null, null);
		configureColumn(row.getColumn("studySubject.owner"), resword.getString("created_by"), new OwnerCellEditor(),
				null, true, false);
		configureColumn(row.getColumn("studySubject.status"), resword.getString("status"), new StatusCellEditor(),
				new StatusDroplistFilterEditor());
		String actionsHeader = resword.getString("actions")
				+ "&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;";
		configureColumn(row.getColumn("actions"), actionsHeader, new ActionsCellEditor(), new DefaultActionsEditor(
				locale), true, false);

	}

	@Override
	public void configureTableFacade(HttpServletResponse response, TableFacade tableFacade) {
		super.configureTableFacade(response, tableFacade);
		tableFacade.addFilterMatcher(new MatcherKey(Date.class, "subject.dateOfBirth"), new DateFilterMatcher(
				getDateFormat()));
		tableFacade.addFilterMatcher(new MatcherKey(Status.class, "studySubject.status"), new GenericFilterMatecher());
		tableFacade.addFilterMatcher(new MatcherKey(UserAccountBean.class, "studySubject.owner"),
				new GenericFilterMatecher());
	}

	@Override
	public void setDataAndLimitVariables(TableFacade tableFacade) {
		// initialize i18n
		resword = ResourceBundleProvider.getWordsBundle(getLocale());
		resformat = ResourceBundleProvider.getFormatBundle(getLocale());

		Limit limit = tableFacade.getLimit();
		StudyAuditLogFilter auditLogStudyFilter = getAuditLogStudyFilter(limit);

		if (!limit.isComplete()) {
			int totalRows = getStudySubjectDao().getCountWithFilter(auditLogStudyFilter, getCurrentStudy());
			tableFacade.setTotalRows(totalRows);
		}

		StudyAuditLogSort auditLogStudySort = getAuditLogStudySort(limit);
		int rowStart = limit.getRowSelect().getRowStart();
		int rowEnd = limit.getRowSelect().getRowEnd();

		Collection<StudySubjectBean> items = getStudySubjectDao().getWithFilterAndSort(getCurrentStudy(),
				auditLogStudyFilter, auditLogStudySort, rowStart, rowEnd);
		Collection<HashMap<Object, Object>> theItems = new ArrayList<HashMap<Object, Object>>();

		for (StudySubjectBean studySubjectBean : items) {
			SubjectBean subject = (SubjectBean) getSubjectDao().findByPK(studySubjectBean.getSubjectId());
			UserAccountBean owner = (UserAccountBean) getUserAccountDao().findByPK(studySubjectBean.getOwnerId());
			HashMap<Object, Object> h = new HashMap<Object, Object>();
			h.put("studySubject", studySubjectBean);
			h.put("studySubject.label", studySubjectBean.getLabel());
			if (currentStudy == null
					|| !currentStudy.getStudyParameterConfig().getSecondaryIdRequired().equalsIgnoreCase("not_used")) {
				h.put("studySubject.secondaryLabel", studySubjectBean.getSecondaryLabel());
			}
			h.put("studySubject.oid", studySubjectBean.getOid());
			h.put("studySubject.owner", owner);
			h.put("studySubject.status", studySubjectBean.getStatus());
			h.put("subject", subject);
			h.put("subject.dateOfBirth", subject.getDateOfBirth());
			h.put("subject.uniqueIdentifier", subject.getUniqueIdentifier());

			theItems.add(h);
		}

		tableFacade.setItems(theItems);
	}

	/**
	 * A very custom way to filter the items. The AuditUserLoginFilter acts as a command for the Hibernate criteria
	 * object. Take the Limit information and filter the rows.
	 * 
	 * @param limit
	 *            The Limit to use.
	 */
	protected StudyAuditLogFilter getAuditLogStudyFilter(Limit limit) {
		StudyAuditLogFilter auditLogStudyFilter = new StudyAuditLogFilter(getDateFormat());
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();
			if ("studySubject.status".equalsIgnoreCase(property)) {
				value = Status.getByName(value).getId() + "";
			}
			auditLogStudyFilter.addFilter(property, value);
		}

		return auditLogStudyFilter;
	}

	/**
	 * A very custom way to sort the items. The AuditUserLoginSort acts as a command for the Hibernate criteria object.
	 * Take the Limit information and sort the rows.
	 * 
	 * @param limit
	 *            The Limit to use.
	 */
	protected StudyAuditLogSort getAuditLogStudySort(Limit limit) {
		StudyAuditLogSort auditLogStudySort = new StudyAuditLogSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			auditLogStudySort.addSort(property, order);
		}

		return auditLogStudySort;
	}

	public AuditUserLoginDao getAuditUserLoginDao() {
		return auditUserLoginDao;
	}

	public void setAuditUserLoginDao(AuditUserLoginDao auditUserLoginDao) {
		this.auditUserLoginDao = auditUserLoginDao;
	}

	private class StatusDroplistFilterEditor extends DroplistFilterEditor {
		@Override
		protected List<Option> getOptions() {
			List<Option> options = new ArrayList<Option>();
			for (Object status : Status.toActiveArrayList()) {
				options.add(new Option(((Status) status).getName(), ((Status) status).getName()));
			}
			return options;
		}
	}

	private class GenericFilterMatecher implements FilterMatcher {
		public boolean evaluate(Object itemValue, String filterValue) {
			return true;
		}
	}

	private class StatusCellEditor implements CellEditor {
		public Object getValue(Object item, String property, int rowcount) {
			Status status = (Status) ((HashMap<Object, Object>) item).get("studySubject.status");
			HtmlBuilder builder = new HtmlBuilder();

			if (status != null) {
				builder.span();
				if (status.isAvailable()) {
					builder.styleClass("aka_green_highlight");
				} else if (status.isLocked() || status.isDeleted()) {
					builder.styleClass("aka_red_highlight");
				}

				builder.close().append(status.getName()).spanEnd();
			}
			
			return builder.toString();
		}
	}

	private class OwnerCellEditor implements CellEditor {
		public Object getValue(Object item, String property, int rowcount) {
			String value = "";
			UserAccountBean user = (UserAccountBean) ((HashMap<Object, Object>) item).get("studySubject.owner");

			if (user != null) {
				value = user.getName();
			}
			return value;
		}
	}

	private class ActionsCellEditor implements CellEditor {
		public Object getValue(Object item, String property, int rowcount) {
			String value = "";
			StudySubjectBean studySubjectBean = (StudySubjectBean) ((HashMap<Object, Object>) item).get("studySubject");
			Integer studySubjectId = studySubjectBean.getId();
			if (studySubjectBean != null) {
				StringBuilder url = new StringBuilder();
				url.append("<a onmouseup=\"javascript:setImage('bt_View1','images/bt_View.gif');\" onmousedown=\"javascript:setImage('bt_View1','images/bt_View_d.gif');\" href=\"javascript:openDocWindow('ViewStudySubjectAuditLog?id=");
				url.append(studySubjectId);
				url.append("')\" data-cc-studyAuditLogId=\"");
				url.append(studySubjectId);
				url.append("\" onclick=\"setAccessedObjected(this)\">");
				url.append("<img hspace=\"6\" border=\"0\" align=\"left\" title=\"View\" alt=\"View\" src=\"images/bt_View.gif\" name=\"bt_View1\"/></a>");
				value = url.toString();
			}
			return value;
		}
	}

	private String getDateFormat() {
		return resformat.getString("date_format_string");
	}

	public StudySubjectDAO getStudySubjectDao() {
		return studySubjectDao;
	}

	public void setStudySubjectDao(StudySubjectDAO studySubjectDao) {
		this.studySubjectDao = studySubjectDao;
	}

	public SubjectDAO getSubjectDao() {
		return subjectDao;
	}

	public void setSubjectDao(SubjectDAO subjectDao) {
		this.subjectDao = subjectDao;
	}

	public StudyBean getCurrentStudy() {
		return currentStudy;
	}

	public void setCurrentStudy(StudyBean currentStudy) {
		this.currentStudy = currentStudy;
	}

	public UserAccountDAO getUserAccountDao() {
		return userAccountDao;
	}

	public void setUserAccountDao(UserAccountDAO userAccountDao) {
		this.userAccountDao = userAccountDao;
	}
}
