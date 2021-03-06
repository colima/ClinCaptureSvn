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

package org.akaza.openclinica.web.table.sdv;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.AbstractTableFactory;
import org.akaza.openclinica.control.DefaultActionsEditor;
import org.akaza.openclinica.controller.helper.table.SDVToolbarSubject;
import org.akaza.openclinica.controller.helper.table.SubjectAggregateContainer;
import org.akaza.openclinica.dao.StudySubjectSDVFilter;
import org.akaza.openclinica.dao.StudySubjectSDVSort;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.jmesa.core.filter.MatcherKey;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Filter;
import org.jmesa.limit.FilterSet;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.view.component.Row;
import org.jmesa.view.html.AbstractHtmlView;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.HtmlSnippets;
import org.jmesa.view.html.component.HtmlColumn;
import org.jmesa.view.html.component.HtmlRow;
import org.jmesa.view.html.component.HtmlTable;
import org.jmesa.web.WebContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A Jmesa table that represents study subjects in each row.
 */
@Component
@SuppressWarnings({ "unchecked" })
public class SubjectIdSDVFactory extends AbstractTableFactory {

	private static final String ICON_FORCRFSTATUS_SUFFIX = ".gif'/>";

	private int studyId;
	private String contextPath;
	private boolean showMoreLink;
	private DataSource dataSource;
	private boolean showBackButton;

	public boolean isShowMoreLink() {
		return showMoreLink;
	}

	public void setShowMoreLink(boolean showMoreLink) {
		this.showMoreLink = showMoreLink;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	protected String getTableName() {
		// This name creates the underlying id of the HTML table
		return "s_sdv";
	}

	@Override
	public void configureTableFacadeCustomView(TableFacade tableFacade) {
		tableFacade.setView(new SubjectSDVView(getLocale()));
	}

	@Override
	protected void configureColumns(TableFacade tableFacade, Locale locale) {

		tableFacade.setColumnProperties("sdvStatus", "studySubjectId", "siteId", "personId", "studySubjectStatus",
				"group", "numberCRFComplete", "numberOfCRFsSDV", "totalEventCRF", "actions");

		ResourceBundle resword = ResourceBundleProvider.getWordsBundle(locale);

		Row row = tableFacade.getTable().getRow();

		StudyBean currentStudy = (StudyBean) tableFacade.getWebContext().getSessionAttribute("study");

		SDVUtil sdvUtil = new SDVUtil();
		String[] allTitles = new String[] {
				resword.getString("SDV_status"),
				currentStudy != null ? currentStudy.getStudyParameterConfig().getStudySubjectIdLabel() : resword
						.getString("study_subject_ID"), resword.getString("site_id"), resword.getString("person_ID"),
				resword.getString("study_subject_status"), resword.getString("group"),
				resword.getString("num_CRFs_completed"), resword.getString("num_CRFs_SDV"),
				resword.getString("total_events_CRF"), resword.getString("actions") };

		sdvUtil.setTitles(allTitles, (HtmlTable) tableFacade.getTable());
		sdvUtil.turnOffFilters(tableFacade, new String[] { "personId", "studySubjectStatus", "group",
				"numberCRFComplete", "numberOfCRFsSDV", "totalEventCRF", "actions" });
		sdvUtil.turnOffSorts(tableFacade, new String[] { "sdvStatus", "personId", "studySubjectStatus", "group",
				"numberCRFComplete", "numberOfCRFsSDV", "totalEventCRF" });

		sdvUtil.setHtmlCellEditors(tableFacade, new String[] { "sdvStatus", "actions" }, false);

		HtmlColumn sdvStatus = ((HtmlRow) row).getColumn("sdvStatus");
		sdvStatus.getFilterRenderer().setFilterEditor(new SdvStatusFilter());

		// siteId-filter
		StudyDAO studyDao = new StudyDAO(dataSource);
		List<String> studyIds = new ArrayList<String>();
		List<StudyBean> sites = (List<StudyBean>) studyDao.findAll(studyId);
		for (StudyBean studyBean : sites) {
			studyIds.add(studyBean.getIdentifier());
		}
		Collections.sort(studyIds);

		HtmlColumn studyIdentifier = ((HtmlRow) row).getColumn("siteId");
		studyIdentifier.getFilterRenderer().setFilterEditor(new SDVSimpleListFilter(studyIds));

		String actionsHeader = resword.getString("rule_actions")
				+ "&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;";
		configureColumn(row.getColumn("actions"), actionsHeader, sdvUtil.getCellEditorNoEscapes(),
				new DefaultActionsEditor(locale), true, false);

	}

	@Override
	public void configureTableFacade(HttpServletResponse response, TableFacade tableFacade) {
		super.configureTableFacade(response, tableFacade);

		tableFacade.addFilterMatcher(new MatcherKey(String.class, "sdvStatus"), new SdvStatusMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(String.class, "siteId"), new SDVSimpleMatcher());

	}

	@Override
	public void setDataAndLimitVariables(TableFacade tableFacade) {

		Limit limit = tableFacade.getLimit();

		StudySubjectSDVFilter studySubjectSDVFilter = getStudySubjectSDVFilter(limit);
		WebContext context = tableFacade.getWebContext();
		if (context != null) {
			showBackButton = context.getParameter("sbb") != null;
			studyId = Integer.parseInt(context.getParameter("studyId"));
			contextPath = context.getContextPath();
		}

		StudyBean currentStudy = (StudyBean) tableFacade.getWebContext().getSessionAttribute("study");
		UserAccountBean ub = (UserAccountBean) tableFacade.getWebContext().getSessionAttribute("userBean");
		int totalRows = getTotalRowCount(currentStudy, studySubjectSDVFilter, ub.getId());

		if (!limit.isComplete()) {
			tableFacade.setTotalRows(totalRows);
		} else {
			int pageNum = limit.getRowSelect().getPage();
			int maxRows = limit.getRowSelect().getMaxRows();
			tableFacade.setMaxRows(maxRows);
			tableFacade.setTotalRows(totalRows);
			limit.getRowSelect().setPage(pageNum);
		}

		StudySubjectSDVSort studySubjectSDVSort = getStudySubjectSDVSort(limit);

		int rowStart = limit.getRowSelect().getRowStart();
		int page = limit.getRowSelect().getPage();
		int pageSize = limit.getRowSelect().getMaxRows();

		Collection<SubjectAggregateContainer> items = getFilteredItems(currentStudy, studySubjectSDVFilter,
				studySubjectSDVSort, rowStart, page * pageSize, ub);
		tableFacade.setItems(items);

	}

	protected StudySubjectSDVFilter getStudySubjectSDVFilter(Limit limit) {
		StudySubjectSDVFilter studySubjectSDVFilter = new StudySubjectSDVFilter();
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();
			studySubjectSDVFilter.addFilter(property, value);
		}

		return studySubjectSDVFilter;
	}

	protected StudySubjectSDVSort getStudySubjectSDVSort(Limit limit) {
		StudySubjectSDVSort studySubjectSDVSort = new StudySubjectSDVSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			studySubjectSDVSort.addSort(property, order);
		}

		return studySubjectSDVSort;
	}

	/**
	 * Returns how many subjects exist in the study.
	 * 
	 * @param currentStudy
	 *            StudyBean
	 * @param studySubjectSDVFilter
	 *            StudySubjectSDVFilter
	 * @param userId int
	 * @return int
	 */
	public int getTotalRowCount(StudyBean currentStudy, StudySubjectSDVFilter studySubjectSDVFilter, int userId) {
		StudySubjectDAO studySubDAO = new StudySubjectDAO(dataSource);
		return studySubDAO.countAllByStudySDV(currentStudy, studySubjectSDVFilter, userId);
	}

	@Override
	public void configureTableFacadePostColumnConfiguration(TableFacade tableFacade) {
		tableFacade.setToolbar(new SDVToolbarSubject(showMoreLink));
	}

	private Collection<SubjectAggregateContainer> getFilteredItems(StudyBean currentStudy,
			StudySubjectSDVFilter filterSet, StudySubjectSDVSort sortSet, int rowStart, int rowEnd, UserAccountBean ub) {
		List<SubjectAggregateContainer> rows = new ArrayList<SubjectAggregateContainer>();
		StudySubjectDAO studySubjectDAO = new StudySubjectDAO(dataSource);
		if (sortSet.getSorts().size() == 0) {
			sortSet.addSort("studySubject.createdDate", "desc");
		}
		List<StudySubjectBean> studySubjectBeans = studySubjectDAO.findAllByStudySDV(currentStudy, filterSet, sortSet,
				rowStart, rowEnd, ub.getId());

		for (StudySubjectBean studSubjBean : studySubjectBeans) {
			rows.add(getRow(studSubjBean, currentStudy, ub));
		}

		return rows;
	}

	String getIconForCrfStatusPrefix() {
		String prefix = "../";
		return "<img hspace='2' border='0'  title='SDV Complete' alt='SDV Status' src='" + prefix + "images/icon_";
	}

	private SubjectAggregateContainer getRow(StudySubjectBean studySubjectBean, StudyBean currentStudy, UserAccountBean ub) {
		SubjectAggregateContainer row = new SubjectAggregateContainer();
		EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);
		StudyDAO studyDAO = new StudyDAO(dataSource);
		StudyGroupDAO studyGroupDAO = new StudyGroupDAO(dataSource);

		row.setStudySubjectId(studySubjectBean.getLabel());
		row.setPersonId(studySubjectBean.getUniqueIdentifier());
		row.setStudySubjectStatus(studySubjectBean.getStatus().getName());

		StudyBean studyBean = (StudyBean) studyDAO.findByPK(studySubjectBean.getStudyId());
		row.setSiteId(studyBean.getIdentifier());

		List<EventCRFBean> eventCRFBeans = eventCRFDAO.getEventCRFsWithNonLockedCRFsByStudySubject(
				studySubjectBean.getId(), studySubjectBean.getStudyId(), studySubjectBean.getStudyId());
		int numberEventCRFs = eventCRFBeans.size();
		row.setTotalEventCRF(numberEventCRFs + "");

		HashMap<String, Integer> stats = getEventCRFStats(eventCRFBeans, currentStudy, studySubjectBean, ub);

		int numberOfCompletedEventCRFs = stats.get("numberOfCompletedEventCRFs");
		int numberOfSDVdEventCRFs = stats.get("numberOfSDVdEventCRFs");
		int numberOfCompletedRequiredEventCRFs = stats.get("numberOfCompletedRequiredEventCRFs");
		boolean studySubjectSDVed = stats.get("studySubjectSDVed") == 1;
		boolean studySubjectIsReadyToBeSDVed = stats.get("studySubjectIsReadyToBeSDVed") == 1;
		String allowSdvWithOpenQueries = currentStudy.getStudyParameterConfig().getAllowSdvWithOpenQueries();
		boolean areAllCompletedRequiredEventCRFsHaveUnclosedDNs = allowSdvWithOpenQueries.equals("no")
				&& stats.get("areAllCompletedRequiredEventCRFsHaveUnclosedDNs") == 1;

		row.setNumberCRFComplete(numberOfCompletedEventCRFs + "");
		row.setNumberOfCRFsSDV(numberOfSDVdEventCRFs + "");

		StringBuilder sdvStatus = new StringBuilder("");

		if (studySubjectSDVed) {
			sdvStatus.append("<center><a href='javascript:void(0)' onclick='prompt(document.sdvForm,");
			sdvStatus.append(studySubjectBean.getId());
			sdvStatus.append(")'>");
			sdvStatus.append(getIconForCrfStatusPrefix()).append("DoubleCheck").append(ICON_FORCRFSTATUS_SUFFIX)
					.append("</a></center>");
		} else {
			if (numberOfCompletedRequiredEventCRFs > 0 && !areAllCompletedRequiredEventCRFsHaveUnclosedDNs) {
				sdvStatus.append("<center><input style='margin-right: 5px' type='checkbox' ")
						.append("class='sdvCheck'").append(" name='").append("sdvCheck_")
						.append(studySubjectBean.getId()).append("' onclick='setAccessedObjected(this)'")
						.append(" /></center>");
			}

		}
		row.setSdvStatus(sdvStatus.toString());

		List<StudyGroupBean> studyGroupBeans = studyGroupDAO.getGroupByStudySubject(studySubjectBean.getId(),
				studySubjectBean.getStudyId(), studySubjectBean.getStudyId());

		if (studyGroupBeans != null && !studyGroupBeans.isEmpty()) {
			row.setGroup(studyGroupBeans.get(0).getName());
		}
		StringBuilder actions = new StringBuilder("<table><tr class=\"innerTable\"><td>");
		StringBuilder urlPrefix = new StringBuilder("<a href=\"");
		StringBuilder path = new StringBuilder(contextPath).append("/pages/viewAllSubjectSDVtmp?")
				.append(showBackButton ? "sbb=true&" : "").append("studyId=").append(studyId)
				.append("&sdv_f_studySubjectId=");
		path.append(studySubjectBean.getLabel()).append("\" data-cc-sdvStudySubjectId=\"")
				.append(studySubjectBean.getLabel()).append("\" onclick=\"setAccessedObjected(this)\"");
		urlPrefix.append(path).append("\">");
		actions.append(urlPrefix).append(SDVUtil.VIEW_ICON_HTML).append("</a></td>");

		if (studySubjectIsReadyToBeSDVed) {
			actions.append("<td><input type=\"image\" src=\"").append(contextPath)
					.append("/images/icon_DoubleCheck_Action.gif\"").append(" name=\"sdvSubmit\" ")
					.append("onclick=\"").append("this.form.method='GET'; this.form.action='").append(contextPath)
					.append("/pages/sdvStudySubject").append("';").append("this.form.theStudySubjectId.value='")
					.append(studySubjectBean.getId()).append("';")
					.append("this.form.submit();setAccessedObjected(this);").append("\" /></td>");
		} else if (!studySubjectSDVed) {
			actions.append("<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>");
		}
		actions.append("</tr></table>");

		row.setActions(actions.toString());

		return row;

	}

	private HashMap<String, Integer> getEventCRFStats(List<EventCRFBean> eventCRFBeans, StudyBean currentStudy,
			StudySubjectBean studySubject, UserAccountBean ub) {
		StudyEventDAO studyEventDAO = new StudyEventDAO(dataSource);
		StudySubjectDAO studySubjectDAO = new StudySubjectDAO(dataSource);
		StudyEventDefinitionDAO studyEventDefinitionDAO = new StudyEventDefinitionDAO(dataSource);
		EventDefinitionCRFDAO eventDefinitionCrfDAO = new EventDefinitionCRFDAO(dataSource);
		CRFDAO crfDAO = new CRFDAO(dataSource);
		EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);
		StudyEventBean studyEventBean;
		Integer numberOfCompletedEventCRFs = 0;
		Integer numberOfSDVdEventCRFs = 0;
		Integer numberOfCompletedRequiredEventCRFs = 0;
		Integer numberOfSDVdRequiredEventCRFs = 0;
		List<Integer> eventCRFDefIds = eventDefinitionCrfDAO.getRequiredEventCRFDefIdsThatShouldBeSDVd(currentStudy);
		List<Integer> eventCRFDefIdsCopy = new ArrayList<Integer>(eventCRFDefIds);
		HashMap<String, Integer> stats = new HashMap<String, Integer>();
		List<Integer> eventCRFIdWithRequiredSDVCodesList = eventCRFDAO
				.findAllIdsWithRequiredSDVCodesBySSubjectId(studySubject.getId(), ub.getId());

		boolean studySubjectSDVed = studySubjectDAO.isStudySubjectSDVed(currentStudy, studySubject);
		boolean studySubjectIsReadyToBeSDVed = !studySubjectSDVed
				&& studySubjectDAO.isStudySubjectReadyToBeSDVed(currentStudy, studySubject);

		if (currentStudy.getStudyParameterConfig().getAllowSdvWithOpenQueries().equals("no")) {
			stats.put(
					"areAllCompletedRequiredEventCRFsHaveUnclosedDNs",
					areAllCompletedRequiredEventCRFsHaveUnclosedDNs(studySubject, eventCRFIdWithRequiredSDVCodesList,
							eventCRFBeans) ? 1 : 0);
		}

		for (EventCRFBean eventBean : eventCRFBeans) {
			studyEventBean = (StudyEventBean) studyEventDAO.findByPK(eventBean.getStudyEventId());
			StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO
					.findByPK(studyEventBean.getStudyEventDefinitionId());
			if (!studyEventDefinitionBean.getStatus().isAvailable()
					|| studyEventBean.getSubjectEventStatus() == SubjectEventStatus.LOCKED
					|| studyEventBean.getSubjectEventStatus() == SubjectEventStatus.STOPPED
					|| studyEventBean.getSubjectEventStatus() == SubjectEventStatus.SKIPPED) {
				continue;
			}
			CRFBean crfBean = crfDAO.findByVersionId(eventBean.getCRFVersionId());
			EventDefinitionCRFBean eventDefinitionCrf = eventDefinitionCrfDAO
					.findByStudyEventDefinitionIdAndCRFIdAndStudyId(studyEventBean.getStudyEventDefinitionId(),
							crfBean.getId(), studySubject.getStudyId());
			if (eventDefinitionCrf.getId() == 0) {
				eventDefinitionCrf = eventDefinitionCrfDAO.findForStudyByStudyEventDefinitionIdAndCRFId(
						studyEventBean.getStudyEventDefinitionId(), crfBean.getId());
			}
			// get number of completed event crfs
			if (eventBean.getStage() == DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE) {
				numberOfCompletedEventCRFs++;
				if (eventCRFIdWithRequiredSDVCodesList.contains(eventBean.getId()) && !eventBean.isSdvStatus()) {
					numberOfCompletedRequiredEventCRFs++;
				}
			}
			// get number of completed event SDVd eventeventDefinitionCrfDAOs
			if (eventBean.getStage() == DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE && eventBean.isSdvStatus()) {
				numberOfSDVdEventCRFs++;
				if (eventCRFIdWithRequiredSDVCodesList.contains(eventBean.getId())) {
					numberOfSDVdRequiredEventCRFs++;
				}
			}
			if (eventDefinitionCrf.getSourceDataVerification() == SourceDataVerification.AllREQUIRED
					|| eventDefinitionCrf.getSourceDataVerification() == SourceDataVerification.PARTIALREQUIRED) {
				if (eventBean.isSdvStatus()) {
					eventCRFDefIds.remove((Integer) eventDefinitionCrf.getId());
					eventCRFDefIdsCopy.remove((Integer) eventDefinitionCrf.getId());
				}
				if (eventBean.getStatus().getId() == Status.UNAVAILABLE.getId() && eventBean.getDateCompleted() != null) {
					eventCRFDefIdsCopy.remove((Integer) eventDefinitionCrf.getId());
				}
			}
		}

		stats.put("numberOfCompletedEventCRFs", numberOfCompletedEventCRFs);
		stats.put("numberOfSDVdEventCRFs", numberOfSDVdEventCRFs);
		stats.put("numberOfCompletedRequiredEventCRFs", numberOfCompletedRequiredEventCRFs);
		stats.put("numberOfSDVdRequiredEventCRFs", numberOfSDVdRequiredEventCRFs);
		stats.put("studySubjectSDVed", studySubjectSDVed ? 1 : 0);
		stats.put("studySubjectIsReadyToBeSDVed", studySubjectIsReadyToBeSDVed ? 1 : 0);
		return stats;
	}

	private boolean areAllCompletedRequiredEventCRFsHaveUnclosedDNs(StudySubjectBean studySubject,
			List<Integer> eventCRFIdWithRequiredSDVCodesList, List<EventCRFBean> eventCRFBeans) {
		DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(dataSource);
		List<Integer> eventCRFIdWithUnclosedDNsList = dndao.findAllEvCRFIdsWithUnclosedDNsByStSubId(studySubject
				.getId());

		for (EventCRFBean eventBean : eventCRFBeans) {
			if (eventBean.getStage() == DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE
					&& !eventBean.isSdvStatus()
					&& (eventCRFIdWithRequiredSDVCodesList.contains(eventBean.getId()) && !eventCRFIdWithUnclosedDNsList
							.contains(eventBean.getId()))) {
				return false;
			}
		}

		return true;
	}

	class SubjectSDVView extends AbstractHtmlView {

		private final ResourceBundle resword;

		public SubjectSDVView(Locale locale) {
			resword = ResourceBundleProvider.getWordsBundle(locale);
		}

		public Object render() {
			HtmlSnippets snippets = getHtmlSnippets();
			HtmlBuilder html = new HtmlBuilder();
			html.append(snippets.themeStart());
			html.append(snippets.tableStart());
			html.append(snippets.theadStart());
			html.append(snippets.toolbar());
			html.append(selectAll());
			html.append(snippets.header());
			html.append(snippets.filter());
			html.append(snippets.theadEnd());
			html.append(snippets.tbodyStart());
			html.append(snippets.body());
			html.append(snippets.tbodyEnd());
			html.append(snippets.footer());
			html.append(snippets.statusBar());
			html.append(snippets.tableEnd());
			html.append(snippets.themeEnd());
			html.append(snippets.initJavascriptLimit());
			return html.toString();
		}

		String selectAll() {
			HtmlBuilder html = new HtmlBuilder();
			html.tr(1).styleClass("logic").close().td(1).colspan("100%").style("font-size: 12px;").close();
			html.append("<b>" + resword.getString("table_sdv_select") + "</b>&#160;&#160;");
			html.append("<a name='checkSDVAll' href='javascript:selectAllChecks(document.sdvForm,true)'>"
					+ resword.getString("table_sdv_all"));
			html.append(",</a>");
			html.append("&#160;&#160;&#160;");
			html.append("<a name='checkSDVAll' href='javascript:selectAllChecks(document.sdvForm,false)'>"
					+ resword.getString("table_sdv_none"));
			html.append("</a>");
			html.tdEnd().trEnd(1);
			return html.toString();
		}
	}
}
