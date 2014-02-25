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

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.dynamicevent.DynamicEventBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.*;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.control.AbstractTableFactory;
import org.akaza.openclinica.control.DefaultActionsEditor;
import org.akaza.openclinica.control.submit.ListStudySubjectTableFactory;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.dynamicevent.DynamicEventDao;
import org.akaza.openclinica.dao.managestudy.*;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.util.DAOWrapper;
import org.akaza.openclinica.util.SignUtil;
import org.akaza.openclinica.util.SubjectLabelNormalizer;
import org.akaza.openclinica.view.Page;
import org.apache.commons.lang.StringUtils;
import org.jmesa.core.filter.DateFilterMatcher;
import org.jmesa.core.filter.FilterMatcher;
import org.jmesa.core.filter.MatcherKey;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.*;
import org.jmesa.view.component.Row;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.editor.DroplistFilterEditor;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ListEventsForSubjectTableFactory extends AbstractTableFactory {

	private StudyEventDefinitionDAO studyEventDefinitionDao;
	private DynamicEventDao dynamicEventDAO;
	private StudySubjectDAO studySubjectDAO;
	private SubjectDAO subjectDAO;
	private StudyEventDAO studyEventDAO;
	private StudyGroupClassDAO studyGroupClassDAO;
	private SubjectGroupMapDAO subjectGroupMapDAO;
	private StudyDAO studyDAO;
	private StudyGroupDAO studyGroupDAO;
	private EventCRFDAO eventCRFDAO;
	private EventDefinitionCRFDAO eventDefintionCRFDAO;
	private CRFDAO crfDAO;
	private DiscrepancyNoteDAO discrepancyNoteDAO;
	private StudyBean studyBean;
	private String[] columnNames = new String[] {};
	private ArrayList<StudyEventDefinitionBean> studyEventDefinitions;
	private ArrayList<CRFBean> crfBeans;
	private ArrayList<EventDefinitionCRFBean> eventDefinitionCrfs;
	private ArrayList<StudyGroupClassBean> studyGroupClasses;
	private StudyUserRoleBean currentRole;
	private UserAccountBean currentUser;
	private boolean showMoreLink;
	private ResourceBundle resword = ResourceBundleProvider.getWordsBundle();
	private ResourceBundle resformat = ResourceBundleProvider.getFormatBundle();
	private StudyEventDefinitionBean selectedStudyEventDefinition;

	private final int POPUP_BASE_WIDTH = 600;
	private final String POPUP_BASE_WIDTH_PX = "width: " + POPUP_BASE_WIDTH + "px";

	private final HashMap<Integer, String> imageIconPaths = new HashMap<Integer, String>();
	private final HashMap<Integer, String> crfColumnImageIconPaths = new HashMap<Integer, String>();

	public ListEventsForSubjectTableFactory(boolean showMoreLink) {
		imageIconPaths.put(1, "images/icon_Scheduled.gif");
		imageIconPaths.put(2, "images/icon_NotStarted.gif");
		imageIconPaths.put(3, "images/icon_InitialDE.gif");
		imageIconPaths.put(4, "images/icon_DEcomplete.gif");
		imageIconPaths.put(5, "images/icon_Stopped.gif");
		imageIconPaths.put(6, "images/icon_Skipped.gif");
		imageIconPaths.put(7, "images/icon_Locked.gif");
		imageIconPaths.put(8, "images/icon_Signed.gif");
		imageIconPaths.put(9, "images/icon_DoubleCheck.gif");
		imageIconPaths.put(10, "images/icon_Invalid.gif");

		crfColumnImageIconPaths.put(Status.NOT_STARTED.getId(), "images/icon_NotStarted.gif");
		crfColumnImageIconPaths.put(Status.DATA_ENTRY_STARTED.getId(), "images/icon_InitialDE.gif");
		crfColumnImageIconPaths.put(Status.INITIAL_DATA_ENTRY_COMPLETED.getId(), "images/icon_InitialDEcomplete.gif");
		crfColumnImageIconPaths.put(Status.DOUBLE_DATA_ENTRY.getId(), "images/icon_DDE.gif");
		crfColumnImageIconPaths.put(Status.SOURCE_DATA_VERIFIED.getId(), "images/icon_DoubleCheck.gif");
		crfColumnImageIconPaths.put(Status.SIGNED.getId(), "images/icon_Signed.gif");
		crfColumnImageIconPaths.put(Status.COMPLETED.getId(), "images/icon_DEcomplete.gif");
		crfColumnImageIconPaths.put(Status.LOCKED.getId(), "images/icon_Locked.gif");
		crfColumnImageIconPaths.put(Status.DELETED.getId(), "images/icon_Invalid.gif");

		this.showMoreLink = showMoreLink;
	}

	@Override
	protected String getTableName() {
		return "listEventsForSubject";
	}

	@Override
	protected void configureColumns(TableFacade tableFacade, Locale locale) {
		resword = ResourceBundleProvider.getWordsBundle(locale);
		resformat = ResourceBundleProvider.getFormatBundle(locale);
		tableFacade.setColumnProperties(columnNames);
		Row row = tableFacade.getTable().getRow();
		int index = 0;
		StudyBean currentStudy = (StudyBean) tableFacade.getWebContext().getSessionAttribute("study");
		configureColumn(row.getColumn(columnNames[index]), currentStudy == null ? resword.getString("study_subject_ID")
				: currentStudy.getStudyParameterConfig().getStudySubjectIdLabel(), null, null);
		++index;
		configureColumn(row.getColumn(columnNames[index]), resword.getString("subject_status"), new StatusCellEditor(),
				new StatusDroplistFilterEditor());
		++index;
		configureColumn(row.getColumn(columnNames[index]), resword.getString("site_id"), null, null);
		++index;
		if (currentStudy == null || currentStudy.getStudyParameterConfig().getGenderRequired().equalsIgnoreCase("true")) {
			configureColumn(row.getColumn(columnNames[index]), currentStudy == null ? resword.getString("gender")
					: currentStudy.getStudyParameterConfig().getGenderLabel(), null, null, true, false);
			++index;
		}

		// group class columns
		for (int i = index; i < index + studyGroupClasses.size(); i++) {
			StudyGroupClassBean studyGroupClass = studyGroupClasses.get(i - index);
			configureColumn(row.getColumn(columnNames[i]), studyGroupClass.getName(), new StudyGroupClassCellEditor(
					studyGroupClass), new SubjectGroupClassDroplistFilterEditor(studyGroupClass), true, false);
		}

		configureColumn(row.getColumn(columnNames[index + studyGroupClasses.size()]),
				resword.getString("event_status"), new EventStatusCellEditor(),
				new SubjectEventStatusDroplistFilterEditor(), true, false);
		++index;
		configureColumn(row.getColumn(columnNames[index + studyGroupClasses.size()]), resword.getString("event_date"),
				new EventStartDateCellEditor(), null, false, true);
		++index;

		// crf columns
		for (int i = index + studyGroupClasses.size(); i < columnNames.length - 1; i++) {
			CRFBean crfBean = crfBeans.get(i - (index + studyGroupClasses.size()));
			configureColumn(row.getColumn(columnNames[i]), crfBean.getName(), new EventCrfCellEditor(),
					new SubjectEventCRFStatusDroplistFilterEditor(), true, false);
		}

		configureColumn(row.getColumn(columnNames[columnNames.length - 1]), resword.getString("rule_actions"),
				new ActionsCellEditor(), new ListSubjectsActionsFilterEditor(locale), true, false);
	}

	@Override
	public void configureTableFacade(HttpServletResponse response, TableFacade tableFacade) {
		super.configureTableFacade(response, tableFacade);
		int stratFrom = getColumnNamesMap(tableFacade);
		tableFacade.addFilterMatcher(new MatcherKey(Character.class), new CharFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(Status.class), new StatusFilterMatcher());
		
		tableFacade.addFilterMatcher(new MatcherKey(String.class, "event.status"),
				new SubjectEventStatusFilterMatcher());

		// subject group class filter matcher
		for (int i = stratFrom; i < stratFrom + studyGroupClasses.size(); i++) {
			tableFacade.addFilterMatcher(new MatcherKey(String.class, columnNames[i]), new SubjectGroupFilterMatcher());
		}

		// crf columns filtering
		for (int i = stratFrom + 2 + studyGroupClasses.size(); i < columnNames.length - 1; i++) {
			tableFacade.addFilterMatcher(new MatcherKey(String.class, columnNames[i]),
					new SubjectEventCRFStatusFilterMatcher());
		}

		tableFacade.addFilterMatcher(new MatcherKey(Date.class, "studySubject.createdDate"), new DateFilterMatcher(
				getDateFormat()));
	}

	@Override
	public void configureTableFacadePostColumnConfiguration(TableFacade tableFacade) {
		Role r = currentRole.getRole();
		boolean addSubjectLinkShow = studyBean.getStatus().isAvailable() && !r.equals(Role.STUDY_MONITOR);
		tableFacade.setToolbar(new ListEventsForSubjectTableToolbar(getStudyEventDefinitions(), getStudyGroupClasses(),
				selectedStudyEventDefinition, addSubjectLinkShow, showMoreLink));
	}

	@Override
	public void setDataAndLimitVariables(TableFacade tableFacade) {

		resformat = ResourceBundleProvider.getFormatBundle(getLocale());

		Limit limit = tableFacade.getLimit();

		ListEventsForSubjectFilter eventsForSubjectFilter = getListEventsForSubjectFilter(limit);

		if (!limit.isComplete()) {
			int totalRows = getStudySubjectDAO().getCountWithFilter(eventsForSubjectFilter, getStudyBean());
			tableFacade.setTotalRows(totalRows);
		}

		ListEventsForSubjectSort eventsForSubjectSort = getListEventsForSubjectSort(limit);

		int rowStart = limit.getRowSelect().getRowStart();
		int rowEnd = limit.getRowSelect().getRowEnd();
		Collection<StudySubjectBean> items = getStudySubjectDAO().getWithFilterAndSort(getStudyBean(),
				eventsForSubjectFilter, eventsForSubjectSort, rowStart, rowEnd);
		Collection<HashMap<Object, Object>> theItems = new ArrayList<HashMap<Object, Object>>();

		for (StudySubjectBean studySubjectBean : items) {
			HashMap<Object, Object> theItem = new HashMap<Object, Object>();
			theItem.put("studySubject", studySubjectBean);
			theItem.put("studySubject.label", studySubjectBean.getLabel());
			theItem.put("studySubject.status", studySubjectBean.getStatus());
			theItem.put("enrolledAt",
					((StudyBean) getStudyDAO().findByPK(studySubjectBean.getStudyId())).getIdentifier());

			SubjectBean subjectBean = (SubjectBean) getSubjectDAO().findByPK(studySubjectBean.getSubjectId());
			theItem.put("subject", subjectBean);
			theItem.put("subject.charGender", subjectBean.getGender());

			theItem.put("isSignable", SignUtil.permitSign(studySubjectBean, new DAOWrapper(getStudyDAO(),
					getStudyEventDAO(), getStudySubjectDAO(), getEventCRFDAO(), getEventDefintionCRFDAO(),
					getDiscrepancyNoteDAO())));

			// study group classes
			SubjectGroupMapBean subjectGroupMapBean = new SubjectGroupMapBean();
			for (StudyGroupClassBean studyGroupClass : getStudyGroupClasses()) {
				subjectGroupMapBean = getSubjectGroupMapDAO().findAllByStudySubjectAndStudyGroupClass(
						studySubjectBean.getId(), studyGroupClass.getId());
				if (null != subjectGroupMapBean) {
					theItem.put("sgc_" + studyGroupClass.getId(), subjectGroupMapBean.getStudyGroupId());
					theItem.put("grpName_sgc_" + studyGroupClass.getId(), subjectGroupMapBean.getStudyGroupName());
				}
			}
			subjectGroupMapBean = null;

			// Get EventCrfs for study Subject
			List<EventCRFBean> eventCrfs = getEventCRFDAO().findAllByStudySubject(studySubjectBean.getId());
			HashMap<String, EventCRFBean> crfAsKeyEventCrfAsValue = new HashMap<String, EventCRFBean>();
			for (EventCRFBean eventCRFBean : eventCrfs) {
				CRFBean crf = getCrfDAO().findByVersionId(eventCRFBean.getCRFVersionId());
				crfAsKeyEventCrfAsValue.put(crf.getId() + "_" + eventCRFBean.getStudyEventId(), eventCRFBean);
			}

			// Get the event Status
			List<StudyEventBean> eventsForStudySubjectAndEventDefinitions = getStudyEventDAO()
					.findAllByDefinitionAndSubjectOrderByOrdinal(selectedStudyEventDefinition, studySubjectBean);
			List<DisplayBean> events = new ArrayList<DisplayBean>();
			
			theItem.put("numberOfEvents", eventsForStudySubjectAndEventDefinitions.size());
			
			// study event size < 1
			if (eventsForStudySubjectAndEventDefinitions.isEmpty()) {
				DisplayBean d = new DisplayBean();
				d.getProps().put("event", null);
				d.getProps().put("event.status", SubjectEventStatus.NOT_SCHEDULED);
				d.getProps().put("studySubject.createdDate", null);
				for (int i = 0; i < getCrfs(selectedStudyEventDefinition).size(); i++) {
					CRFBean crf = getCrfs(selectedStudyEventDefinition).get(i);
					d.getProps().put("crf_" + crf.getId(), getCRFStatusId(studySubjectBean, null, null, null));
					d.getProps().put("crf_" + crf.getId() + "_eventCrf", null);
					d.getProps().put("crf_" + crf.getId() + "_crf", crf);
					d.getProps().put("crf_" + crf.getId() + "_eventDefinitionCrf",
							getEventDefinitionCRFBean(selectedStudyEventDefinition.getId(), crf, studySubjectBean));
					theItem.put("crf_" + crf.getId(), "");
				}
				events.add(d);
			}
			// study event size >0
			for (StudyEventBean studyEventBean : eventsForStudySubjectAndEventDefinitions) {
				DisplayBean d = new DisplayBean();
				d.getProps().put("event", studyEventBean);
				d.getProps().put("event.status", studyEventBean.getSubjectEventStatus());
				d.getProps().put("studySubject.createdDate", studyEventBean.getDateStarted());
				for (int i = 0; i < getCrfs(selectedStudyEventDefinition).size(); i++) {
					CRFBean crf = getCrfs(selectedStudyEventDefinition).get(i);
					EventCRFBean eventCRFBean = crfAsKeyEventCrfAsValue.get(crf.getId() + "_" + studyEventBean.getId());
					d.getProps().put("crf_" + crf.getId(), getCRFStatusId(studySubjectBean, studyEventBean,
							studyEventBean.getSubjectEventStatus(), eventCRFBean));
					if (eventCRFBean != null) {
						d.getProps().put("crf_" + crf.getId() + "_eventCrf", eventCRFBean);
					} else {
						d.getProps().put("crf_" + crf.getId() + "_eventCrf", null);
					}
					d.getProps().put("crf_" + crf.getId() + "_crf", crf);
					d.getProps().put("crf_" + crf.getId() + "_eventDefinitionCrf",
							getEventDefinitionCRFBean(selectedStudyEventDefinition.getId(), crf, studySubjectBean));

					theItem.put("crf_" + crf.getId(), "");
				}
				
				if (eventsForStudySubjectAndEventDefinitions.size() > 1 && !eventsForSubjectFilter.isEmpty()) {
					if (isStudyEventMatchingFilters(d, eventsForSubjectFilter)) {
						events.add(d);
					}			
				} else {
					events.add(d);
				}
				
			}

			theItem.put("events", events);
			theItem.put("event.status", "");
			theItems.add(theItem);
		}

		// Do not forget to set the items back on the tableFacade.
		tableFacade.setItems(theItems);

	}

	private EventDefinitionCRFBean getEventDefinitionCRFBean(Integer studyEventDefinitionId, CRFBean crfBean,
			StudySubjectBean studySubject) {
		EventDefinitionCRFBean eventDefinitionCrf = getEventDefintionCRFDAO()
				.findByStudyEventDefinitionIdAndCRFIdAndStudyId(studyEventDefinitionId, crfBean.getId(),
						studySubject.getStudyId());
		if (eventDefinitionCrf.getId() == 0) {
			eventDefinitionCrf = getEventDefintionCRFDAO().findForStudyByStudyEventDefinitionIdAndCRFId(
					studyEventDefinitionId, crfBean.getId());
		}
		return eventDefinitionCrf;
	}

	private int getColumnNamesMap(TableFacade tableFacade) {
		int stratFrom = 3;
		ArrayList<String> columnNamesList = new ArrayList<String>();
		columnNamesList.add("studySubject.label");
		columnNamesList.add("studySubject.status");
		columnNamesList.add("enrolledAt");

		StudyBean currentStudy = (StudyBean) tableFacade.getWebContext().getSessionAttribute("study");
		if (currentStudy == null || currentStudy.getStudyParameterConfig().getGenderRequired().equalsIgnoreCase("true")) {
			stratFrom++;
			columnNamesList.add("subject.charGender");
		}

		for (StudyGroupClassBean studyGroupClass : getStudyGroupClasses()) {
			columnNamesList.add("sgc_" + studyGroupClass.getId());
		}
		columnNamesList.add("event.status");
		columnNamesList.add("studySubject.createdDate");

		for (CRFBean crfBean : getCrfs(selectedStudyEventDefinition)) {
			columnNamesList.add("crf_" + crfBean.getId());
		}
		columnNamesList.add("actions");
		columnNames = columnNamesList.toArray(columnNames);
		return stratFrom;
	}

	protected ListEventsForSubjectFilter getListEventsForSubjectFilter(Limit limit) {

		int dynamicGroupClassIdToFilterBy = 0;
		int studyId;
		DynamicEventBean dynamicEventBean = getDynamicEventDAO().findByStudyEventDefinitionId(
				selectedStudyEventDefinition.getId());
		if (dynamicEventBean != null) {

			if (studyBean.getParentStudyId() > 0) {
				studyId = studyBean.getParentStudyId();
			} else {
				studyId = studyBean.getId();
			}

			for (StudyGroupClassBean studyGroupClassBean : (ArrayList<StudyGroupClassBean>) getStudyGroupClassDAO()
					.findAllActiveDynamicGroupsByStudyId(studyId)) {
				if (studyGroupClassBean.getId() == dynamicEventBean.getStudyGroupClassId()
						&& !studyGroupClassBean.isDefault()) {
					dynamicGroupClassIdToFilterBy = studyGroupClassBean.getId();
					break;
				}
			}
		}

		ListEventsForSubjectFilter listEventsForSubjectFilter = new ListEventsForSubjectFilter(
				selectedStudyEventDefinition.getId(), dynamicGroupClassIdToFilterBy);
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();
			if ("studySubject.status".equalsIgnoreCase(property)) {
				value = Status.getByName(value).getId() + "";
			} else if ("event.status".equalsIgnoreCase(property)) {
				value = SubjectEventStatus.getByName(value).getId() + "";
			} else if (property.startsWith("sgc_")) {
				int studyGroupClassId = property.endsWith("_") ? 0 : Integer.valueOf(property.split("_")[1]);
				value = studyGroupDAO.findByNameAndGroupClassID(value, studyGroupClassId).getId() + "";
			}
			listEventsForSubjectFilter.addFilter(property, value);
		}

		return listEventsForSubjectFilter;
	}

	protected ListEventsForSubjectSort getListEventsForSubjectSort(Limit limit) {
		ListEventsForSubjectSort listEventsForSubjectSort = new ListEventsForSubjectSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			listEventsForSubjectSort.addSort(property, order);
		}

		return listEventsForSubjectSort;
	}

	private ArrayList<StudyEventDefinitionBean> getStudyEventDefinitions() {
		if (this.studyEventDefinitions == null) {
			if (studyBean.getParentStudyId() > 0) {
				StudyBean parentStudy = (StudyBean) getStudyDAO().findByPK(studyBean.getParentStudyId());
				studyEventDefinitions = getStudyEventDefinitionDAO().findAllByStudy(parentStudy);
			} else {
				studyEventDefinitions = getStudyEventDefinitionDAO().findAllByStudy(studyBean);
			}
		}
		return this.studyEventDefinitions;
	}

	private ArrayList<CRFBean> getCrfs(StudyEventDefinitionBean eventDefinition) {
		if (this.crfBeans == null) {
			crfBeans = new ArrayList<CRFBean>();
			eventDefinitionCrfs = new ArrayList<EventDefinitionCRFBean>();
			for (EventDefinitionCRFBean eventDefinitionCrf : (List<EventDefinitionCRFBean>) getEventDefintionCRFDAO()
					.findAllActiveByEventDefinitionId(eventDefinition.getId())) {
				CRFBean crfBean = (CRFBean) getCrfDAO().findByPK(eventDefinitionCrf.getCrfId());
				if (eventDefinitionCrf.getParentId() == 0) {
					crfBeans.add(crfBean);
					eventDefinitionCrfs.add(eventDefinitionCrf);
				}

			}
			return crfBeans;
		}
		return crfBeans;
	}

	private ArrayList<StudyGroupClassBean> getStudyGroupClasses() {
		boolean filterOnDynamic = true;

		if (this.studyGroupClasses == null) {
			if (studyBean.getParentStudyId() > 0) {
				studyGroupClasses = getStudyGroupClassDAO().findAllActiveByStudyId(studyBean.getParentStudyId(),
						filterOnDynamic);
			} else {
				studyGroupClasses = getStudyGroupClassDAO().findAllActiveByStudyId(studyBean.getId(), filterOnDynamic);
			}
		}
		return studyGroupClasses;
	}

	public StudyEventDefinitionDAO getStudyEventDefinitionDAO() {
		return studyEventDefinitionDao;
	}

	public void setStudyEventDefinitionDao(StudyEventDefinitionDAO studyEventDefinitionDao) {
		this.studyEventDefinitionDao = studyEventDefinitionDao;
	}

	public DynamicEventDao getDynamicEventDAO() {
		return dynamicEventDAO;
	}

	public void setDynamicEventDAO(DynamicEventDao dynamicEventDAO) {
		this.dynamicEventDAO = dynamicEventDAO;
	}

	public StudyBean getStudyBean() {
		return studyBean;
	}

	public void setStudyBean(StudyBean studyBean) {
		this.studyBean = studyBean;
	}

	public StudySubjectDAO getStudySubjectDAO() {
		return studySubjectDAO;
	}

	public void setStudySubjectDAO(StudySubjectDAO studySubjectDAO) {
		this.studySubjectDAO = studySubjectDAO;
	}

	public SubjectDAO getSubjectDAO() {
		return subjectDAO;
	}

	public void setSubjectDAO(SubjectDAO subjectDAO) {
		this.subjectDAO = subjectDAO;
	}

	public StudyEventDAO getStudyEventDAO() {
		return studyEventDAO;
	}

	public void setStudyEventDAO(StudyEventDAO studyEventDAO) {
		this.studyEventDAO = studyEventDAO;
	}

	public StudyGroupClassDAO getStudyGroupClassDAO() {
		return studyGroupClassDAO;
	}

	public void setStudyGroupClassDAO(StudyGroupClassDAO studyGroupClassDAO) {
		this.studyGroupClassDAO = studyGroupClassDAO;
	}

	public SubjectGroupMapDAO getSubjectGroupMapDAO() {
		return subjectGroupMapDAO;
	}

	public void setSubjectGroupMapDAO(SubjectGroupMapDAO subjectGroupMapDAO) {
		this.subjectGroupMapDAO = subjectGroupMapDAO;
	}

	public StudyDAO getStudyDAO() {
		return studyDAO;
	}

	public void setStudyDAO(StudyDAO studyDAO) {
		this.studyDAO = studyDAO;
	}

	public StudyGroupDAO getStudyGroupDAO() {
		return studyGroupDAO;
	}

	public void setStudyGroupDAO(StudyGroupDAO studyGroupDAO) {
		this.studyGroupDAO = studyGroupDAO;
	}

	public StudyUserRoleBean getCurrentRole() {
		return currentRole;
	}

	public void setCurrentRole(StudyUserRoleBean currentRole) {
		this.currentRole = currentRole;
	}

	public EventCRFDAO getEventCRFDAO() {
		return eventCRFDAO;
	}

	public void setEventCRFDAO(EventCRFDAO eventCRFDAO) {
		this.eventCRFDAO = eventCRFDAO;
	}

	public EventDefinitionCRFDAO getEventDefintionCRFDAO() {
		return eventDefintionCRFDAO;
	}

	public void setEventDefintionCRFDAO(EventDefinitionCRFDAO eventDefintionCRFDAO) {
		this.eventDefintionCRFDAO = eventDefintionCRFDAO;
	}

	public CRFDAO getCrfDAO() {
		return crfDAO;
	}

	public void setCrfDAO(CRFDAO crfDAO) {
		this.crfDAO = crfDAO;
	}

	public DiscrepancyNoteDAO getDiscrepancyNoteDAO() {
		return discrepancyNoteDAO;
	}

	public void setDiscrepancyNoteDAO(DiscrepancyNoteDAO discrepancyNoteDAO) {
		this.discrepancyNoteDAO = discrepancyNoteDAO;
	}

	public UserAccountBean getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(UserAccountBean currentUser) {
		this.currentUser = currentUser;
	}

	public StudyEventDefinitionBean getSelectedStudyEventDefinition() {
		return selectedStudyEventDefinition;
	}

	public void setSelectedStudyEventDefinition(StudyEventDefinitionBean selectedStudyEventDefinition) {
		this.selectedStudyEventDefinition = selectedStudyEventDefinition;
	}

	private class CharFilterMatcher implements FilterMatcher {
		public boolean evaluate(Object itemValue, String filterValue) {
			String item = StringUtils.lowerCase(String.valueOf(itemValue));
			String filter = StringUtils.lowerCase(String.valueOf(filterValue));
			if (StringUtils.contains(item, filter)) {
				return true;
			}

			return false;
		}
	}

	public class StatusFilterMatcher implements FilterMatcher {
		public boolean evaluate(Object itemValue, String filterValue) {

			String item = StringUtils.lowerCase(((Status) itemValue).getName());
			String filter = StringUtils.lowerCase(filterValue);

			if (filter.equals(item)) {
				return true;
			}
			return false;
		}
	}
	
	public class SubjectEventStatusFilterMatcher implements FilterMatcher {
		public boolean evaluate(Object itemValue, String filterValue) {
			// No need to evaluate itemValue and filterValue.
			return true;
		}
	}

	public class SubjectGroupFilterMatcher implements FilterMatcher {

		public boolean evaluate(Object itemValue, String filterValue) {
			String itemSGName = studyGroupDAO.findByPK(Integer.valueOf(itemValue.toString())).getName();
			return filterValue.equalsIgnoreCase(itemSGName);
		}
	}
	
	public class SubjectEventCRFStatusFilterMatcher implements FilterMatcher {

		public boolean evaluate(Object itemValue, String filterValue) {
			// No need to evaluate itemValue and filterValue.
			return true;
		}
	}

	private class StatusCellEditor implements CellEditor {
		public Object getValue(Object item, String property, int rowcount) {
			StudySubjectBean studySubject = (StudySubjectBean) new BasicCellEditor().getValue(item, "studySubject",
					rowcount);
			return studySubject.getStatus().getName();
		}
	}

	private class StatusDroplistFilterEditor extends DroplistFilterEditor {
		@Override
		protected List<Option> getOptions() {
			List<Option> options = new ArrayList<Option>();
			for (Object status : Status.toDropDownArrayList()) {
				options.add(new Option(((Status) status).getName(), ((Status) status).getName()));
			}
			return options;
		}
	}

	private class SubjectEventStatusDroplistFilterEditor extends DroplistFilterEditor {
		@Override
		protected List<Option> getOptions() {
			List<Option> options = new ArrayList<Option>();
			for (Object subjectEventStatus : SubjectEventStatus.toArrayList()) {
				options.add(new Option(((SubjectEventStatus) subjectEventStatus).getName(),
						((SubjectEventStatus) subjectEventStatus).getName()));
			}
			return options;
		}
	}

	private class SubjectEventCRFStatusDroplistFilterEditor extends DroplistFilterEditor {
		@Override
		protected List<Option> getOptions() {
			List<Option> options = new ArrayList<Option>();
			for (Status eventCRFStatus : Status.toCRFStatusDropDownList()) {
				options.add(new Option(eventCRFStatus.getName(), eventCRFStatus.getName()));
			}
			return options;
		}
	}

	private class SubjectGroupClassDroplistFilterEditor extends DroplistFilterEditor {
		private StudyGroupClassBean studyGroupClass = new StudyGroupClassBean();

		// constructor
		SubjectGroupClassDroplistFilterEditor(StudyGroupClassBean studyGroupClass) {
			this.studyGroupClass = studyGroupClass;
		}

		@Override
		protected List<Option> getOptions() {
			List<Option> options = new ArrayList<Option>();
			StudyGroupDAO studyGroupDAO = getStudyGroupDAO();
			ArrayList<StudyGroupBean> groups = studyGroupDAO.findAllByGroupClass(this.studyGroupClass);
			for (Object subjectStudyGroup : groups) {
				options.add(new Option(((StudyGroupBean) subjectStudyGroup).getName(),
						((StudyGroupBean) subjectStudyGroup).getName()));
			}
			return options;
		}
	}

	private class StudyGroupClassCellEditor implements CellEditor {
		StudyGroupClassBean studyGroupClass;
		String groupName;

		public StudyGroupClassCellEditor(StudyGroupClassBean studyGroupClass) {
			this.studyGroupClass = studyGroupClass;
		}

		private String logic() {
			return groupName != null ? groupName : "";
		}

		public Object getValue(Object item, String property, int rowcount) {
			groupName = (String) ((HashMap<Object, Object>) item).get("grpName_sgc_" + studyGroupClass.getId());
			return logic();
		}
	}

	private class EventStatusCellEditor implements CellEditor {

		SubjectEventStatus subjectEventStatus;
		StudyEventBean studyEvent;
		StudySubjectBean studySubjectBean;
		List<DisplayBean> events;
		SubjectBean subject;
		StudyEventDefinitionBean studyEventDefinition;

		public Object getValue(Object item, String property, int rowcount) {

			events = (List<DisplayBean>) ((HashMap<Object, Object>) item).get("events");
			studySubjectBean = (StudySubjectBean) ((HashMap<Object, Object>) item).get("studySubject");
			subject = (SubjectBean) ((HashMap<Object, Object>) item).get("subject");
			studyEventDefinition = selectedStudyEventDefinition;
			List<StudyEventBean> studyEvents;
			String imageIconPath;
			int numberOfEvents = (Integer) ((HashMap<Object, Object>) item).get("numberOfEvents");

			StringBuilder url = new StringBuilder();
			for (int i = 0; i < events.size(); i++) {
				DisplayBean display = events.get(i);
				subjectEventStatus = (SubjectEventStatus) display.getProps().get("event.status");
				studyEvent = (StudyEventBean) display.getProps().get("event");
				studyEvents = new ArrayList<StudyEventBean>();
				if (studyEvent != null) {
					studyEvents.add(studyEvent);
				}

				url.append(eventDivBuilder(subject, Integer.valueOf(rowcount + String.valueOf(i)), studyEvents,
						numberOfEvents, studyEventDefinition, studySubjectBean));

				if (studySubjectBean.getStatus().isDeleted()) {
					imageIconPath = imageIconPaths.get(10);
				} else if (studySubjectBean.getStatus().isLocked()) {
					imageIconPath = imageIconPaths.get(7);
				} else {
					imageIconPath = imageIconPaths.get(subjectEventStatus.getId());
				}

				url.append("<img src='" + imageIconPath + "' border='0' style=''>");
				url.append("</a></td></tr></table>");
			}

			return url.toString();
		}

	}

	private class EventStartDateCellEditor implements CellEditor {
		Date eventStartDate;
		List<DisplayBean> events;

		public Object getValue(Object item, String property, int rowcount) {
			events = (List<DisplayBean>) ((HashMap<Object, Object>) item).get("events");
			StringBuilder url = new StringBuilder();

			for (DisplayBean display : events) {
				eventStartDate = (Date) display.getProps().get("studySubject.createdDate");
				url.append("<table border='0'  cellpadding='0'  cellspacing='0' ><tr valign='top' ><td>");
				url.append(eventStartDate == null ? "" : formatDate(eventStartDate));
				url.append("</td></tr></table>");
			}
			return url.toString();
		}
	}

	private class EventCrfCellEditor implements CellEditor {

		StudyEventBean studyEvent;
		StudySubjectBean studySubjectBean;
		List<DisplayBean> events;
		SubjectBean subject;
		EventCRFBean eventCrf;
		EventDefinitionCRFBean eventDefintionCrf;

		public Object getValue(Object item, String property, int rowcount) {
			events = (List<DisplayBean>) ((HashMap<Object, Object>) item).get("events");
			studySubjectBean = (StudySubjectBean) ((HashMap<Object, Object>) item).get("studySubject");
			subject = (SubjectBean) ((HashMap<Object, Object>) item).get("subject");
			List<StudyEventBean> studyEvents;
			int eventCRFStatusId;
			int numberOfEvents = (Integer) ((HashMap<Object, Object>) item).get("numberOfEvents");

			StringBuilder url = new StringBuilder();
			for (int i = 0; i < events.size(); i++) {

				DisplayBean display = events.get(i);
				eventCRFStatusId = (Integer) display.getProps().get(property);
				eventDefintionCrf = (EventDefinitionCRFBean) display.getProps().get(property + "_eventDefinitionCrf");
				eventCrf = (EventCRFBean) display.getProps().get(property + "_eventCrf");
				studyEvent = (StudyEventBean) display.getProps().get("event");
				studyEvents = new ArrayList<StudyEventBean>();
				if (studyEvent != null) {
					studyEvents.add(studyEvent);
				}

				url.append(eventDivBuilder(subject, Integer.valueOf(rowcount + String.valueOf(i)), studyEvents, 
						numberOfEvents, selectedStudyEventDefinition, studySubjectBean,
						(eventCrf == null ? null : String.valueOf(eventCrf.getId())), String.valueOf(eventDefintionCrf
								.getId()), false));

				url.append("<img src='"	+ crfColumnImageIconPaths.get(eventCRFStatusId) + "' border='0'>");

				url.append("</a></td></tr></table>");

			}

			return url.toString();
		}

	}

	private class ActionsCellEditor implements CellEditor {
		public Object getValue(Object item, String property, int rowcount) {
			return ListStudySubjectTableFactory.getSubjectActionsColumnContent(item, currentUser, getCurrentRole(),
					getStudyBean(), new DAOWrapper(getStudyDAO(), getStudyEventDAO(), getStudySubjectDAO(),
							getEventCRFDAO(), getEventDefintionCRFDAO(), getStudyEventDefinitionDAO(),
							getDiscrepancyNoteDAO()), resword, logger);
		}
	}

	private class ListSubjectsActionsFilterEditor extends DefaultActionsEditor {
		public ListSubjectsActionsFilterEditor(Locale locale) {
			super(locale);
		}

		@Override
		public Object getValue() {
			HtmlBuilder html = new HtmlBuilder();
			String value;

			value = (String) super.getValue();
			html.append(value).div().style("width: 225px;").end().divEnd();
			return html.toString();
		}
	}

	@SuppressWarnings("unused")
	private class DisplayBean {

		private HashMap<String, Object> props = new HashMap<String, Object>();

		public HashMap<String, Object> getProps() {
			return props;
		}

		public void setProps(HashMap<String, Object> props) {
			this.props = props;
		}
	}

	public String eventDivBuilder(SubjectBean subject, Integer rowCount, List<StudyEventBean> studyEvents,
			int eventOccurrencesNumber, StudyEventDefinitionBean sed, StudySubjectBean studySubject) {
		return eventDivBuilder(subject, rowCount, studyEvents, eventOccurrencesNumber, sed, studySubject, null, null,
				false);
	}

	public String eventDivBuilder(SubjectBean subject, Integer rowCount, List<StudyEventBean> studyEvents,
			int eventOccurrencesNumber, StudyEventDefinitionBean sed, StudySubjectBean studySubject, String eventCRFId,
			String eventDefintionCRFId, boolean goingToReplaceHtmlContent) {
		String studySubjectLabel = SubjectLabelNormalizer.normalizeSubjectLabel(studySubject.getLabel());

		String divWidth = String.valueOf(POPUP_BASE_WIDTH + 8);

		HtmlBuilder eventDiv = new HtmlBuilder();

		if (goingToReplaceHtmlContent) {
			
			resword = ResourceBundleProvider.getWordsBundle();
			resformat = ResourceBundleProvider.getFormatBundle();

			// Event Div
			eventDiv.div().close();

		} else {

			eventDiv.table(0).border("0").cellpadding("0").cellspacing("0").close();

			eventDiv.tr(0).valign("top").close().td(0).close();
			// Event Div
			eventDiv.div()
					.id("Event_" + studySubjectLabel + "_"
							+ (eventDefintionCRFId == null ? sed.getId() : eventDefintionCRFId) + "_" + rowCount)
					.style("position: absolute; visibility: hidden; white-space: normal; z-index: 3;width:" + divWidth
							+ "px; top: 0px; float: left;").rel("" + studySubject.getId()).close();

		}

		eventDiv.div().styleClass("box_T").close().div().styleClass("box_L").close().div().styleClass("box_R").close()
				.div().styleClass("box_B").close().div().styleClass("box_TL").close().div().styleClass("box_TR")
				.close().div().styleClass("box_BL").close().div().styleClass("box_BR").close();

		eventDiv.div().styleClass("tablebox_center").close();
		eventDiv.div().styleClass("ViewSubjectsPopup").style("color: rgb(91, 91, 91);").close();

		eventDiv.table(0).border("0").cellpadding("0").cellspacing("0").style("width: 100%;").close();

		singleEventDivBuilder(eventDiv, subject, rowCount, studyEvents, eventOccurrencesNumber, sed, studySubject,
				eventCRFId, eventDefintionCRFId, goingToReplaceHtmlContent);

		return eventDiv.toString();
	}

	private void singleEventDivBuilder(HtmlBuilder eventDiv, SubjectBean subject, Integer rowCount,
			List<StudyEventBean> studyEvents, int eventOccurrencesNumber, StudyEventDefinitionBean sed,
			StudySubjectBean studySubject, String eventCRFId, String eventDefintionCRFId,
			boolean goingToReplaceHtmlContent) {

		String tableHeaderRowLeftStyleClass = "table_header_row_left";
		String add_another_occurrence = resword.getString("add_another_occurrence");
		String occurrence_x_of = resword.getString("ocurrence");
		String status = resword.getString("status");

		SubjectEventStatus eventStatus = studyEvents.size() == 0 ? SubjectEventStatus.NOT_SCHEDULED : studyEvents
				.get(0).getSubjectEventStatus();

		String studyEventId = studyEvents.size() == 0 ? "" : String.valueOf(studyEvents.get(0).getId());
		Status eventSysStatus = studySubject.getStatus();
		String studySubjectLabel = SubjectLabelNormalizer.normalizeSubjectLabel(studySubject.getLabel());

		if (sed.isRepeating()) {

			eventDiv.tr(0).valign("top").close();
			eventDiv.td(0).styleClass(tableHeaderRowLeftStyleClass).style("border-bottom: none").colspan("2").close();
			eventDiv.bold()
					.append(occurrence_x_of)
					.append(studyEvents.isEmpty() ? "#1 of 1" : "#" + studyEvents.get(0).getSampleOrdinal() + " of "
							+ eventOccurrencesNumber).br();
			if (studyEvents.size() > 0) {
				eventDiv.append(formatDate(studyEvents.get(0).getDateStarted())).br();

			} else {
				eventDiv.append(status + " : " + SubjectEventStatus.NOT_SCHEDULED.getName());
			}
			eventDiv.boldEnd().tdEnd().trEnd(0);
			if (eventStatus != SubjectEventStatus.NOT_SCHEDULED && eventSysStatus != Status.DELETED
					&& eventSysStatus != Status.AUTO_DELETED) {
				eventDiv.tr(0).close().td(0).styleClass("table_cell_left").close();
				eventDiv.ahref("CreateNewStudyEvent?studySubjectId=" + studySubject.getId() + "&studyEventDefinition="
						+ sed.getId(), add_another_occurrence);
				eventDiv.tdEnd().trEnd(0);
			}

		}

		eventDiv.tr(0)
				.id("Menu_on_" + studySubjectLabel + "_"
						+ (eventDefintionCRFId == null ? sed.getId() : eventDefintionCRFId) + "_" + rowCount)
				.style("display: all").close();
		eventDiv.td(0).colspan("2").close();
		eventDiv.table(0).border("0").cellpadding("0").cellspacing("0").width("100%").close();

		if (eventSysStatus.getId() == Status.AVAILABLE.getId() || eventSysStatus == Status.SIGNED
				|| eventSysStatus == Status.LOCKED) {

			if (eventStatus == SubjectEventStatus.NOT_SCHEDULED && canScheduleStudySubject(studySubject)
					&& currentRole.getRole() != Role.STUDY_MONITOR && studyBean.getStatus().isAvailable()) {
				eventDiv.tr(0).valign("top").close();
				eventDiv.td(0).styleClass("table_cell_left").close();
				String href1 = "PageToCreateNewStudyEvent?studySubjectId=" + studySubject.getId()
						+ "&studyEventDefinition=" + sed.getId();
				eventDiv.div()
						.id("eventScheduleWrapper_" + studySubjectLabel + "_"
								+ (eventDefintionCRFId == null ? sed.getId() : eventDefintionCRFId) + "_" + rowCount)
						.rel(href1).style(POPUP_BASE_WIDTH_PX).close().divEnd();
				eventDiv.tdEnd().trEnd(0);
			} else {
				eventDiv.tr(0).valign("top").close();
				eventDiv.td(0).styleClass("table_cell_left").close();
				eventDiv.div()
						.id("crfListWrapper_" + studySubjectLabel + "_"
								+ (eventDefintionCRFId == null ? sed.getId() : eventDefintionCRFId) + "_" + rowCount)
						.style(POPUP_BASE_WIDTH_PX).close().divEnd();
				eventDiv.tdEnd().trEnd(0);
			}
		}

		if (eventSysStatus == Status.DELETED || eventSysStatus == Status.AUTO_DELETED) {
			eventDiv.tr(0).valign("top").close();
			eventDiv.td(0).styleClass("table_cell_left").close();
			eventDiv.div()
					.id("crfListWrapper_" + studySubjectLabel + "_"
							+ (eventDefintionCRFId == null ? sed.getId() : eventDefintionCRFId) + "_" + rowCount)
					.style(POPUP_BASE_WIDTH_PX).close().divEnd();
			eventDiv.tdEnd().trEnd(0);
		}
		eventDiv.tableEnd(0).tdEnd().trEnd(0);

		eventDiv.tableEnd(0);
		eventDiv.divEnd().divEnd().divEnd().divEnd().divEnd().divEnd().divEnd().divEnd().divEnd().divEnd().divEnd();

		if (eventStatus != SubjectEventStatus.NOT_SCHEDULED
				|| (eventStatus == SubjectEventStatus.NOT_SCHEDULED && canScheduleStudySubject(studySubject)
						&& currentRole.getRole() != Role.STUDY_MONITOR && studyBean.getStatus().isAvailable())) {
			iconLinkBuilder(eventDiv, studySubjectLabel, rowCount, studyEvents, sed, studyEventId, eventCRFId,
					eventDefintionCRFId);
		}

	}

	private boolean canScheduleStudySubject(StudySubjectBean studySubject) {
		return studySubject.getStatus() != Status.INVALID && studySubject.getStatus() != Status.UNAVAILABLE
				&& studySubject.getStatus() != Status.LOCKED && studySubject.getStatus() != Status.DELETED
				&& studySubject.getStatus() != Status.AUTO_DELETED;
	}

	private void iconLinkBuilder(HtmlBuilder builder, String studySubjectLabel, Integer rowCount,
			List<StudyEventBean> studyEvents, StudyEventDefinitionBean sed, String studyEventId, String eventCRFId,
			String eventDefintionCRFId) {

		JSONObject params = new JSONObject();
		try {
			params.put("page", Page.LIST_EVENTS_FOR_SUBJECTS_SERVLET.getFileName()); // determines page name, popup
																						// content should be customized
																						// for
			params.put("studyEventId", studyEventId);
			params.put("eventCRFId", eventCRFId);
			params.put("eventDefintionCRFId", eventDefintionCRFId);
			params.put("statusBoxId", studySubjectLabel + "_"
					+ (eventDefintionCRFId == null ? sed.getId() : eventDefintionCRFId) + "_" + rowCount);
		} catch (JSONException e) {
			logger.error("Error has occurred.", e);
		}

		builder.a().style("cursor: pointer;");
		builder.append(" onmouseover = 'if (canShowPopup()) { showPopup(eval(" + params.toString() + "), event); }' ");
		builder.onmouseout("clearInterval(popupInterval);");
		builder.append(" onclick = 'justShowPopup(eval(" + params.toString() + "), event);' ");
		builder.close();
	}

	private int getCRFStatusId(StudySubjectBean studySubjectBean, StudyEventBean studyEvent,
			SubjectEventStatus subjectEventStatus, EventCRFBean eventCrf) {
		
		int eventCRFStatusId;

		if (studyEvent == null) {
			// if study event not scheduled yet

			if (studySubjectBean.getStatus().isDeleted()) {
				eventCRFStatusId = Status.DELETED.getId();
			} else if (studySubjectBean.getStatus().isLocked()) {
				eventCRFStatusId = Status.LOCKED.getId();
			} else {
				eventCRFStatusId = Status.NOT_STARTED.getId();
			}

		} else if (eventCrf == null) {
			// if study event already scheduled, but event CRF not started yet

			if (subjectEventStatus.isLocked()) {
				eventCRFStatusId = Status.LOCKED.getId();
			} else if (!studySubjectBean.getStatus().isDeleted()) {
				eventCRFStatusId = Status.NOT_STARTED.getId();
			} else {
				eventCRFStatusId = Status.DELETED.getId();
			}

		} else {
			// if study event already scheduled and event CRF already started

			if ((subjectEventStatus.isStopped() || subjectEventStatus.isSkipped()) && !eventCrf.isNotStarted()) {
				eventCRFStatusId = Status.LOCKED.getId();
			} else if (subjectEventStatus.isLocked()) {
				eventCRFStatusId = Status.LOCKED.getId();
			} else if (eventCrf.isNotStarted()) {
				eventCRFStatusId = Status.NOT_STARTED.getId();
			} else if (eventCrf.getStage().isInitialDE()) {
				eventCRFStatusId = Status.DATA_ENTRY_STARTED.getId();
			} else if (eventCrf.getStage().isInitialDE_Complete()) {
				eventCRFStatusId = Status.INITIAL_DATA_ENTRY_COMPLETED.getId();
			} else if (eventCrf.getStage().isDoubleDE()) {
				eventCRFStatusId = Status.DOUBLE_DATA_ENTRY.getId();
			} else if (eventCrf.getStage().isDoubleDE_Complete()) {

				if (subjectEventStatus.isSigned()) {
					eventCRFStatusId = Status.SIGNED.getId();
				} else if (eventCrf.isSdvStatus()) {
					eventCRFStatusId = Status.SOURCE_DATA_VERIFIED.getId();
				} else {
					eventCRFStatusId = Status.COMPLETED.getId();
				}

			} else {
				eventCRFStatusId = Status.DELETED.getId();
			}

		}

		return eventCRFStatusId;
	}

	private String formatDate(Date date) {
		String format = resformat.getString("date_format_string");
		SimpleDateFormat sdf = new SimpleDateFormat(format, locale);
		return sdf.format(date);
	}
	
	private String getDateFormat() {
		return resformat.getString("date_format_string");
	}
	
	private boolean isStudyEventMatchingFilters(DisplayBean eventDisplayBean, ListEventsForSubjectFilter eventsForSubjectFilter) {
		
		String filterValue;

		filterValue = eventsForSubjectFilter.getFilterValueByProperty("event.status");
		
		if (filterValue != null) {	
			if (((SubjectEventStatus) eventDisplayBean.getProps().get("event.status")).getId() 
					!= Integer.parseInt(filterValue)) {
				return false;
			}
		}
		
		for (CRFBean crfBean: getCrfs(selectedStudyEventDefinition)) {
			filterValue = eventsForSubjectFilter.getFilterValueByProperty("crf_" + crfBean.getId());
			
			if (filterValue != null) {
				if ((Integer) eventDisplayBean.getProps().get("crf_" + crfBean.getId())
						!= Status.getByName(filterValue).getId()) {
					return false;
				}
			}
		}
		
		return true;
	}
}
