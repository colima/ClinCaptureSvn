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

package org.akaza.openclinica.control.submit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.AbstractTableFactory;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.ListDiscNotesSubjectFilter;
import org.akaza.openclinica.dao.managestudy.ListDiscNotesSubjectSort;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Filter;
import org.jmesa.limit.FilterSet;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.view.component.Row;

@SuppressWarnings({"rawtypes", "unchecked"})
public class DiscNotesSubjectStatisticsFactory extends AbstractTableFactory {
	private String[] columnNames = new String[] {};
	private StudyEventDefinitionDAO studyEventDefinitionDao;
	private StudySubjectDAO studySubjectDAO;
	private SubjectDAO subjectDAO;
	private StudyEventDAO studyEventDAO;
	private StudyGroupClassDAO studyGroupClassDAO;
	private SubjectGroupMapDAO subjectGroupMapDAO;
	private StudyGroupDAO studyGroupDAO;
	private StudyDAO studyDAO;
	private EventCRFDAO eventCRFDAO;
	private EventDefinitionCRFDAO eventDefintionCRFDAO;
	private DiscrepancyNoteDAO discrepancyNoteDAO;
	private StudyBean studyBean;

	private StudyUserRoleBean currentRole;
	private UserAccountBean currentUser;
	private ResourceBundle resword;
	private ResourceBundle resterm;
	private String module;
	private Integer resolutionStatus;
	private Integer discNoteType;
	private Boolean studyHasDiscNotes;
	private Map<Object, Map> discrepancyMap;

	@Override
	protected String getTableName() {
		return "discNotesSummary";
	}

	@Override
	protected void configureColumns(TableFacade tableFacade, Locale locale) {
		tableFacade.setColumnProperties(columnNames);
		Row row = tableFacade.getTable().getRow();
		HashMap<Object, Map> items = (HashMap<Object, Map>) getDiscrepancyMap();
		Set theKeys = items.keySet();
		Iterator theKeysItr = theKeys.iterator();
		configureColumn(row.getColumn(columnNames[0]), "_", null, null);
		configureColumn(row.getColumn(columnNames[1]), theKeysItr.next().toString(), null, null);
		configureColumn(row.getColumn(columnNames[2]), theKeysItr.next().toString(), null, null);
		configureColumn(row.getColumn(columnNames[3]), theKeysItr.next().toString(), null, null);
		configureColumn(row.getColumn(columnNames[4]), theKeysItr.next().toString(), null, null);
		configureColumn(row.getColumn(columnNames[5]), "Totals", null, null);
	}

	@Override
	public void configureTableFacade(HttpServletResponse response, TableFacade tableFacade) {
		super.configureTableFacade(response, tableFacade);

		getColumnNamesMap();
	}

	@Override
	public void setDataAndLimitVariables(TableFacade tableFacade) {
		Limit limit = tableFacade.getLimit();

		if (!limit.isComplete()) {
			tableFacade.setTotalRows(6);
		}

		HashMap<Object, Map> items = (HashMap<Object, Map>) getDiscrepancyMap();

		Collection<HashMap<Object, Object>> theItems = new ArrayList<HashMap<Object, Object>>();
		Collection<HashMap<Object, Object>> theItemsVals = new ArrayList<HashMap<Object, Object>>();
		Iterator keyIt = null;
		if (items.values().iterator().hasNext())
			keyIt = items.values().iterator().next().keySet().iterator();
		HashMap<Object, Object> theItem = new HashMap();

		Set theKeys = items.keySet();

		Iterator theKeysItr = theKeys.iterator();
		while (keyIt.hasNext()) {
			String key = "";

			key = keyIt.next().toString();

			for (Map<String, String[]> firstVals : items.values()) {

				theItem = new HashMap();
				Iterator it = firstVals.values().iterator();
				String label = (String) theKeysItr.next();

				while (it.hasNext()) {

					theItem.put("_", key);
					theItem.put(label, it.next());

				}

				theItems.add(theItem);

			}

			theItemsVals.addAll(theItems);

			tableFacade.setItems(theItemsVals);
		}
	}

	private void getColumnNamesMap() {
		ArrayList<String> columnNamesList = new ArrayList<String>();
		HashMap<Object, Map> items = (HashMap<Object, Map>) getDiscrepancyMap();
		Set theKeys = items.keySet();
		Iterator theKeysItr = theKeys.iterator();
		columnNamesList.add("_");
		columnNamesList.add(theKeysItr.next().toString());
		columnNamesList.add(theKeysItr.next().toString());

		columnNamesList.add(theKeysItr.next().toString());
		columnNamesList.add(theKeysItr.next().toString());

		columnNamesList.add("Totals");
		columnNames = columnNamesList.toArray(columnNames);
	}

	protected ListDiscNotesSubjectSort getSubjectSort(Limit limit) {
		ListDiscNotesSubjectSort listDiscNotesSubjectSort = new ListDiscNotesSubjectSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			listDiscNotesSubjectSort.addSort(property, order);
		}

		return listDiscNotesSubjectSort;
	}

	protected ListDiscNotesSubjectFilter getSubjectFilter(Limit limit) {
		ListDiscNotesSubjectFilter listDiscNotesSubjectFilter = new ListDiscNotesSubjectFilter();
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();
			listDiscNotesSubjectFilter.addFilter(property, value);
		}

		return listDiscNotesSubjectFilter;
	}

	public Map<Object, Map> getDiscrepancyMap() {
		return discrepancyMap;
	}

	public void setDiscrepancyMap(Map<Object, Map> discrepancyMap) {
		this.discrepancyMap = discrepancyMap;
	}

	public StudyEventDefinitionDAO getStudyEventDefinitionDAO() {
		return studyEventDefinitionDao;
	}

	public void setStudyEventDefinitionDao(StudyEventDefinitionDAO studyEventDefinitionDao) {
		this.studyEventDefinitionDao = studyEventDefinitionDao;
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

	public StudyGroupDAO getStudyGroupDAO() {
		return studyGroupDAO;
	}

	public void setStudyGroupDAO(StudyGroupDAO studyGroupDAO) {
		this.studyGroupDAO = studyGroupDAO;
	}

	public DiscrepancyNoteDAO getDiscrepancyNoteDAO() {
		return discrepancyNoteDAO;
	}

	public void setDiscrepancyNoteDAO(DiscrepancyNoteDAO discrepancyNoteDAO) {
		this.discrepancyNoteDAO = discrepancyNoteDAO;
	}

	public ResourceBundle getResword() {
		return resword;
	}

	public void setResword(ResourceBundle resword) {
		this.resword = resword;
	}

	public ResourceBundle getResterm() {
		return resterm;
	}

	public void setResterm(ResourceBundle resterm) {
		this.resterm = resterm;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public UserAccountBean getCurrentUser() {
		return currentUser;
	}

	public Integer getResolutionStatus() {
		return resolutionStatus;
	}

	public void setResolutionStatus(Integer resolutionStatus) {
		this.resolutionStatus = resolutionStatus;
	}

	public Integer getDiscNoteType() {
		return discNoteType;
	}

	public void setDiscNoteType(Integer discNoteType) {
		this.discNoteType = discNoteType;
	}

	public Boolean isStudyHasDiscNotes() {
		return studyHasDiscNotes;
	}

	public void setStudyHasDiscNotes(Boolean studyHasDiscNotes) {
		this.studyHasDiscNotes = studyHasDiscNotes;
	}

	public void setCurrentUser(UserAccountBean currentUser) {
		this.currentUser = currentUser;
	}

}
