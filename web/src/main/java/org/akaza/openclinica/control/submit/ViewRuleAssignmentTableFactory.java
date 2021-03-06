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

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.control.AbstractTableFactory;
import org.akaza.openclinica.control.DefaultActionsEditor;
import org.akaza.openclinica.control.OCTableFacadeImpl;
import org.akaza.openclinica.dao.hibernate.ViewRuleAssignmentFilter;
import org.akaza.openclinica.dao.hibernate.ViewRuleAssignmentSort;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.domain.Status;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.action.ActionType;
import org.akaza.openclinica.domain.rule.action.HideActionBean;
import org.akaza.openclinica.domain.rule.action.InsertActionBean;
import org.akaza.openclinica.domain.rule.action.RuleActionBean;
import org.akaza.openclinica.domain.rule.action.RuleActionRunBean;
import org.akaza.openclinica.domain.rule.action.ShowActionBean;
import org.akaza.openclinica.domain.technicaladmin.LoginStatus;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.service.rule.RuleSetServiceInterface;
import org.akaza.openclinica.web.table.filter.CRFFilter;
import org.akaza.openclinica.web.table.filter.StudyEventTableRowFilter;
import org.jmesa.core.filter.DateFilterMatcher;
import org.jmesa.core.filter.FilterMatcher;
import org.jmesa.core.filter.MatcherKey;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.ExportType;
import org.jmesa.limit.Filter;
import org.jmesa.limit.FilterSet;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.view.component.Row;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlColumn;
import org.jmesa.view.html.component.HtmlRow;
import org.jmesa.view.html.editor.DroplistFilterEditor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Table factory for View Rules page.
 */
@SuppressWarnings("unchecked")
public class ViewRuleAssignmentTableFactory extends AbstractTableFactory {

	private RuleSetServiceInterface ruleSetService;

	private StudyBean currentStudy;
	private ResourceBundle resword;
	private final boolean showMoreLink;
	private final boolean isDesignerRequest;
	private ItemFormMetadataDAO itemFormMetadataDAO;

	private List<Integer> ruleSetRuleIds;
	private String[] columnNames = new String[] {};
	private UserAccountBean currentUser;

	private DataSource dataSource;

	public UserAccountBean getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(UserAccountBean currentUser) {
		this.currentUser = currentUser;
	}

	/**
	 * Constructor for factory.
	 * @param showMoreLink show more link
	 * @param isDesignerRequest is designer request
	 */
	public ViewRuleAssignmentTableFactory(boolean showMoreLink, boolean isDesignerRequest) {
		this.showMoreLink = showMoreLink;
		this.isDesignerRequest = isDesignerRequest;
	}

	@Override
	public TableFacade getTableFacadeImpl(HttpServletRequest request, HttpServletResponse response) {
		return new OCTableFacadeImpl(getTableName(), request, response, "rules" + currentStudy.getOid() + "-");
	}

	@Override
	protected String getTableName() {
		return "ruleAssignments";
	}

	@Override
	protected void configureColumns(TableFacade tableFacade, Locale locale) {

		logger.debug("Configuring table columns");

		tableFacade.setColumnProperties(columnNames);
		Row row = tableFacade.getTable().getRow();
		int index = 0;
		configureColumn(row.getColumn(columnNames[index++]), resword.getString("view_rule_assignment_target"), null,
				null);
		configureColumn(row.getColumn(columnNames[index++]), resword.getString("view_rule_assignment_study_event"),
				null, null);
		configureColumn(row.getColumn(columnNames[index++]), resword.getString("view_rule_assignment_crf")
				+ "&#160;&#160;&#160;&#160;&#160;", null, null);
		configureColumn(row.getColumn(columnNames[index++]), resword.getString("view_rule_assignment_version"), null,
				null);
		configureColumn(row.getColumn(columnNames[index++]), resword.getString("view_rule_assignment_group"), null,
				null);
		configureColumn(row.getColumn(columnNames[index++]), resword.getString("view_rule_assignment_item_name"),
				new ItemCellEditor(), null);
		configureColumn(row.getColumn(columnNames[index++]), resword.getString("view_rule_assignment_rule_name"), null,
				null);
		configureColumn(row.getColumn(columnNames[index++]), resword.getString("view_rule_assignment_rule_status"),
				new StatusCellEditor(), new StatusDroplistFilterEditor());
		configureColumn(row.getColumn(columnNames[index++]),
				resword.getString("view_rule_assignment_rule_description"), null, null);
		configureColumn(row.getColumn(columnNames[index++]), resword.getString("view_rule_assignment_expression"),
				null, null);
		configureColumn(row.getColumn(columnNames[index++]),
				resword.getString("view_rule_assignment_crf_br_validations"), new ValidationsValueCellEditor(false),
				null, false, false);
		configureColumn(row.getColumn(columnNames[index++]), resword.getString("view_rule_assignment_execute_on"),
				new ExecuteOnCellEditor(false), new ExpressionEvaluatesToDroplistFilterEditor(), true, false);
		configureColumn(row.getColumn(columnNames[index++]), resword.getString("view_rule_assignment_action_type"),
				new ActionTypeCellEditor(false), new ActionTypeDroplistFilterEditor(), true, false);
		configureColumn(row.getColumn(columnNames[index]), resword.getString("view_rule_assignment_action_summary"),
				new ActionSummaryCellEditor(false), null, true, false);

		// Configure the drop-down for the study event control
		CRFFilter crfFileter = new CRFFilter(dataSource, currentStudy, getCurrentUser());

		HtmlColumn crfNameColumn = ((HtmlRow) row).getColumn(2);
		crfNameColumn.getFilterRenderer().setFilterEditor(crfFileter);

		// Configure the drop-down for the study event control
		StudyEventTableRowFilter studyEventTableRowFilter = new StudyEventTableRowFilter(dataSource, currentStudy, currentUser);

		HtmlColumn studyEventColumn = ((HtmlRow) row).getColumn(1);
		studyEventColumn.getFilterRenderer().setFilterEditor(studyEventTableRowFilter);

		configureColumn(row.getColumn("actions"), resword.getString("actions"), new ActionsCellEditor(), new DefaultActionsEditor(
				locale), true, false);
	}

	@Override
	protected void configureExportColumns(TableFacade tableFacade, Locale locale) {
		tableFacade.setColumnProperties(columnNames);
		Row row = tableFacade.getTable().getRow();
		int index = 0;
		configureColumn(row.getColumn(columnNames[index++]), resword.getString("view_rule_assignment_target"), null,
				null);
		configureColumn(row.getColumn(columnNames[index++]), resword.getString("view_rule_assignment_study_event"),
				null, null);
		configureColumn(row.getColumn(columnNames[index++]), resword.getString("view_rule_assignment_crf")
				+ "&#160;&#160;&#160;&#160;&#160;", null, null);
		configureColumn(row.getColumn(columnNames[index++]), resword.getString("view_rule_assignment_version"), null,
				null);
		configureColumn(row.getColumn(columnNames[index++]), resword.getString("view_rule_assignment_group"), null,
				null);
		configureColumn(row.getColumn(columnNames[index++]), resword.getString("view_rule_assignment_item_name"), null,
				null);
		configureColumn(row.getColumn(columnNames[index++]), resword.getString("view_rule_assignment_rule_name"), null,
				null);
		configureColumn(row.getColumn(columnNames[index++]), resword.getString("view_rule_assignment_rule_status"),
				new StatusCellEditor(), new StatusDroplistFilterEditor());
		configureColumn(row.getColumn(columnNames[index++]),
				resword.getString("view_rule_assignment_rule_description"), null, null);
		configureColumn(row.getColumn(columnNames[index++]), resword.getString("view_rule_assignment_expression"),
				null, null);
		configureColumn(row.getColumn(columnNames[index++]), resword.getString("view_rule_assignment_crf_validations"),
				new ValidationsValueCellEditor(true), null, false, false);
		configureColumn(row.getColumn(columnNames[index++]), resword.getString("view_rule_assignment_execute_on"),
				new ExecuteOnCellEditor(true), new ExpressionEvaluatesToDroplistFilterEditor(), true, false);
		configureColumn(row.getColumn(columnNames[index++]), resword.getString("view_rule_assignment_action_type"),
				new ActionTypeCellEditor(true), new ActionTypeDroplistFilterEditor(), true, false);
		configureColumn(row.getColumn(columnNames[index]), resword.getString("view_rule_assignment_action_summary"),
				new ActionSummaryCellEditor(true), null, true, false);
	}

	@Override
	protected ExportType[] getExportTypes() {
		if (isDesignerRequest) {
			return new ExportType[] {};
		}
		return new ExportType[] { ExportType.PDF, ExportType.JEXCEL };
	}

	@Override
	public void configureTableFacade(HttpServletResponse response, TableFacade tableFacade) {
		super.configureTableFacade(response, tableFacade);
		getColumnNamesMap();
		tableFacade.addFilterMatcher(new MatcherKey(Date.class, "loginAttemptDate"), new DateFilterMatcher(
				"yyyy-MM-dd HH:mm"));
		tableFacade.addFilterMatcher(new MatcherKey(LoginStatus.class, "loginStatus"), new AvailableFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(String.class, "actionExecuteOn"), new GenericFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(String.class, "actionType"), new GenericFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(String.class, "actionSummary"), new GenericFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(String.class, "ruleSetRuleStatus"), new GenericFilterMatcher());

	}

	/**
	 * Always return true.
	 */
	public class GenericFilterMatcher implements FilterMatcher {
		/**
		 * Matcher for filter - always return true.
		 * @param itemValue Object
		 * @param filterValue String
		 * @return true
		 */
		public boolean evaluate(Object itemValue, String filterValue) {
			// No need to evaluate itemValue and filterValue.
			return true;
		}
	}

	@Override
	public void configureTableFacadePostColumnConfiguration(TableFacade tableFacade) {

		tableFacade.setToolbar(new ViewRuleAssignmentTableToolbar(ruleSetRuleIds, showMoreLink, isDesignerRequest));
	}

	@Override
	public void setDataAndLimitVariables(TableFacade tableFacade) {
		// initialize i18n
		resword = ResourceBundleProvider.getWordsBundle(getLocale());

		Limit limit = tableFacade.getLimit();
		ViewRuleAssignmentFilter viewRuleAssignmentFilter = getViewRuleAssignmentFilter(limit);
		ViewRuleAssignmentSort viewRuleAssignmentSort = getViewRuleAssignmentSort(limit);
		viewRuleAssignmentFilter.addFilter("studyId", currentStudy.getId());
		if (viewRuleAssignmentSort.getSorts().size() == 0) {
			viewRuleAssignmentSort.addSort("itemName", "asc");
		}

		viewRuleAssignmentFilter.addFilter("ignoreWrongRules", true);

		/*
		 * Because we are using the State feature (via stateAttr) we can do a check to see if we have a complete limit
		 * already. See the State feature for more details Very important to set the totalRow before trying to get the
		 * row start and row end variables.
		 */
		int newTotalRows = getRuleSetService().getCountWithFilter(viewRuleAssignmentFilter);
		if (!limit.isComplete()
				|| (limit.getRowSelect() != null && limit.getRowSelect().getTotalRows() != newTotalRows)) {
			tableFacade.setTotalRows(newTotalRows);
		}

		int rowStart = limit.getRowSelect() != null ? limit.getRowSelect().getRowStart() : 0;
		int rowEnd = limit.getRowSelect() != null ? limit.getRowSelect().getRowEnd() : 0;
		Collection<RuleSetRuleBean> items = getRuleSetService().getWithFilterAndSort(viewRuleAssignmentFilter,
				viewRuleAssignmentSort, rowStart, rowEnd);
		HashMap<Integer, RuleSetBean> ruleSets = new HashMap<Integer, RuleSetBean>();

		Collection<HashMap<Object, Object>> theItems = new ArrayList<HashMap<Object, Object>>();
		ruleSetRuleIds = new ArrayList<Integer>();
		for (RuleSetRuleBean ruleSetRuleBean : items) {

			RuleSetBean ruleSetBean;
			ruleSetRuleIds.add(ruleSetRuleBean.getId());
			if (ruleSets.containsKey(ruleSetRuleBean.getRuleSetBean().getId())) {
				ruleSetBean = ruleSets.get(ruleSetRuleBean.getRuleSetBean().getId());
			} else {
				ruleSetBean = ruleSetRuleBean.getRuleSetBean();
				getRuleSetService().getObjects(ruleSetBean);
				ruleSets.put(ruleSetBean.getId(), ruleSetBean);
			}

			HashMap<Object, Object> theItem = new HashMap<Object, Object>();
			theItem.put("ruleSetId", ruleSetBean.getId());
			theItem.put("ruleSetRuleId", ruleSetRuleBean.getId());
			theItem.put("ruleId", ruleSetRuleBean.getRuleBean().getId());
			theItem.put("ruleSetRule", ruleSetRuleBean);
			theItem.put("targetValue", ruleSetBean.getTarget().getValue());
			theItem.put("studyEventDefinitionName", ruleSetBean.getStudyEventDefinitionName());
			theItem.put("crf", ruleSetBean.getCrf());
			theItem.put("crfVersion", ruleSetBean.getCrfVersion());
			theItem.put("item", ruleSetBean.getItem());
			theItem.put("crfName", ruleSetBean.getCrfName());
			theItem.put("crfVersionName", ruleSetBean.getCrfVersionName());
			theItem.put("groupLabel", ruleSetBean.getGroupLabel());
			theItem.put("itemName", ruleSetBean.getItemName());
			theItem.put("ruleSetRules", ruleSetBean.getRuleSetRules());
			theItem.put("ruleName", ruleSetRuleBean.getRuleBean().getName());
			theItem.put("ruleExpressionValue", ruleSetRuleBean.getRuleBean().getExpression().getValue());
			theItem.put("ruleOid", ruleSetRuleBean.getRuleBean().getOid());
			theItem.put("ruleDescription", ruleSetRuleBean.getRuleBean().getDescription());
			theItem.put("theActions", ruleSetRuleBean.getActions());
			theItem.put("ruleSetRuleStatus", "");
			theItem.put("validations", "");
			theItem.put("actionExecuteOn", "");
			theItem.put("actionType", "XXXXXXXXX");
			theItem.put("actionSummary", "");
			theItems.add(theItem);
		}

		// Do not forget to set the items back on the tableFacade.
		tableFacade.setItems(theItems);

	}

	private void getColumnNamesMap() {
		ArrayList<String> columnNamesList = new ArrayList<String>();
		columnNamesList.add("targetValue");
		columnNamesList.add("studyEventDefinitionName");
		columnNamesList.add("crfName");
		columnNamesList.add("crfVersionName");
		columnNamesList.add("groupLabel");
		columnNamesList.add("itemName");
		columnNamesList.add("ruleName");
		columnNamesList.add("ruleSetRuleStatus");
		columnNamesList.add("ruleDescription");
		columnNamesList.add("ruleExpressionValue");
		columnNamesList.add("validations");
		columnNamesList.add("actionExecuteOn");
		columnNamesList.add("actionType");
		columnNamesList.add("actionSummary");
		columnNamesList.add("actions");
		columnNames = columnNamesList.toArray(columnNames);
	}

	/**
	 * A very custom way to filter the items. The AuditUserLoginFilter acts as a command for the Hibernate criteria
	 * object. Take the Limit information and filter the rows.
	 * 
	 * @param limit
	 *            The Limit to use.
	 */
	protected ViewRuleAssignmentFilter getViewRuleAssignmentFilter(Limit limit) {
		ViewRuleAssignmentFilter viewRuleAssignmentFilter = new ViewRuleAssignmentFilter();
		FilterSet filterSet = limit.getFilterSet();
		Collection<Filter> filters = filterSet.getFilters();
		for (Filter filter : filters) {
			String property = filter.getProperty();
			String value = filter.getValue();
			if ("ruleSetRuleStatus".equals(property)) {
				Status s = Status.getByI18nDescription(value, locale);
				int code = s != null ? s.getCode() : -1;
				value = code > 0 ? Status.getByCode(code).getCode() + "" : "0";
			} else if ("actionType".equals(property)) {
				ActionType a = ActionType.getByDescription(value);
				value = a != null ? a.getCode() + "" : value;
			}
			viewRuleAssignmentFilter.addFilter(property, value);
		}

		return viewRuleAssignmentFilter;
	}

	/**
	 * A very custom way to sort the items. The AuditUserLoginSort acts as a command for the Hibernate criteria object.
	 * Take the Limit information and sort the rows.
	 * 
	 * @param limit
	 *            The Limit to use.
	 */
	protected ViewRuleAssignmentSort getViewRuleAssignmentSort(Limit limit) {
		ViewRuleAssignmentSort viewRuleAssignmentSort = new ViewRuleAssignmentSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			viewRuleAssignmentSort.addSort(property, order);
		}

		return viewRuleAssignmentSort;
	}

	public RuleSetServiceInterface getRuleSetService() {
		return ruleSetService;
	}

	public void setRuleSetService(RuleSetServiceInterface ruleSetService) {
		this.ruleSetService = ruleSetService;
	}

	public StudyBean getCurrentStudy() {
		return currentStudy;
	}

	public void setCurrentStudy(StudyBean currentStudy) {
		this.currentStudy = currentStudy;
	}

	public ItemFormMetadataDAO getItemFormMetadataDAO() {
		return itemFormMetadataDAO;
	}

	public void setItemFormMetadataDAO(ItemFormMetadataDAO itemFormMetadataDAO) {
		this.itemFormMetadataDAO = itemFormMetadataDAO;
	}

	private class AvailableFilterMatcher implements FilterMatcher {
		public boolean evaluate(Object itemValue, String filterValue) {
			Status filter = Status.getByCode(Integer.valueOf(filterValue));
			Status item = (Status) itemValue;
			return item.equals(filter);
		}
	}

	private class ItemCellEditor implements CellEditor {

		/**
		 * Get item value.
		 * @param item Object
		 * @param property String
		 * @param rowcount int
		 * @return Object
		 */
		public Object getValue(Object item, String property, int rowcount) {

			String value;
			HtmlBuilder builder = new HtmlBuilder();
			String mouseOver = "this.style.textDecoration='underline';";
			String mouseOut = "this.style.textDecoration='none';";
			ItemBean theItem = (ItemBean) ((HashMap<Object, Object>) item).get("item");

			value = builder.a().href("javascript: openDocWindow('ViewItemDetail?itemId=" + theItem.getId() + "')")
					.style("color: #789EC5;text-decoration: none;").onmouseover(mouseOver).onmouseout(mouseOut).close()
					.append(theItem.getName()).aEnd().toString();

			return value;
		}
	}

	private class ValidationsValueCellEditor implements CellEditor {
		private ItemBean theItem;
		private CRFBean crf;
		private CRFVersionBean crfVersion;
		public static final String YES = "yes";
		public static final String NO = "no";
		private Boolean isExport;

		public ValidationsValueCellEditor(Boolean isExport) {
			this.isExport = isExport;
		}

		public Object getValue(Object item, String property, int rowcount) {
			return isExport ? renderExportValue(item) : renderHtmlValue(item);
		}

		public Object renderExportValue(Object item) {

			String value;
			HtmlBuilder builder = new HtmlBuilder();
			theItem = (ItemBean) ((HashMap<Object, Object>) item).get("item");
			crf = (CRFBean) ((HashMap<Object, Object>) item).get("crf");
			crfVersion = (CRFVersionBean) ((HashMap<Object, Object>) item).get("crfVersion");

			if (crfVersion != null) {
				ItemFormMetadataBean ifm = getItemFormMetadataDAO().findByItemIdAndCRFVersionId(theItem.getId(),
						crfVersion.getId());
				if (ifm.getId() != 0 && ifm.getRegexp() != null && !ifm.getRegexp().equals("")) {
					value = YES;
				} else {
					value = NO;
				}
			} else if (crf != null) {
				ArrayList<ItemFormMetadataBean> itemFormMetadatas = getItemFormMetadataDAO()
						.findAllByCRFIdItemIdAndHasValidations(crf.getId(), theItem.getId());
				if (itemFormMetadatas.size() > 0) {
					value = builder.a()
							.href("javascript: openDocWindow('ViewItemDetail?itemId=" + theItem.getId() + "')").close()
							.append(YES).aEnd().toString();
				} else {
					value = NO;
				}
			} else {
				ArrayList<ItemFormMetadataBean> itemFormMetadatas = getItemFormMetadataDAO()
						.findAllByItemIdAndHasValidations(theItem.getId());
				if (itemFormMetadatas.size() > 0) {
					value = builder.a()
							.href("javascript: openDocWindow('ViewItemDetail?itemId=" + theItem.getId() + "')").close()
							.append(YES).aEnd().toString();
				} else {
					value = NO;
				}

			}

			return value;
		}

		public Object renderHtmlValue(Object item) {

			String value;
			HtmlBuilder builder = new HtmlBuilder();
			theItem = (ItemBean) ((HashMap<Object, Object>) item).get("item");
			crf = (CRFBean) ((HashMap<Object, Object>) item).get("crf");
			crfVersion = (CRFVersionBean) ((HashMap<Object, Object>) item).get("crfVersion");

			if (crfVersion != null) {
				ItemFormMetadataBean ifm = getItemFormMetadataDAO().findByItemIdAndCRFVersionId(theItem.getId(),
						crfVersion.getId());
				if (ifm.getId() != 0 && ifm.getRegexp() != null && !ifm.getRegexp().equals("")) {
					value = builder.a()
							.href("javascript: openDocWindow('ViewItemDetail?itemId=" + theItem.getId() + "')").close()
							.append(YES).aEnd().toString();
				} else {
					value = NO;
				}
			} else if (crf != null) {
				ArrayList<ItemFormMetadataBean> itemFormMetadatas = getItemFormMetadataDAO()
						.findAllByCRFIdItemIdAndHasValidations(crf.getId(), theItem.getId());
				if (itemFormMetadatas.size() > 0) {
					value = builder.a()
							.href("javascript: openDocWindow('ViewItemDetail?itemId=" + theItem.getId() + "')").close()
							.append(YES).aEnd().toString();
				} else {
					value = NO;
				}
			} else {
				ArrayList<ItemFormMetadataBean> itemFormMetadatas = getItemFormMetadataDAO()
						.findAllByItemIdAndHasValidations(theItem.getId());
				if (itemFormMetadatas.size() > 0) {
					value = builder.a()
							.href("javascript: openDocWindow('ViewItemDetail?itemId=" + theItem.getId() + "')").close()
							.append(YES).aEnd().toString();
				} else {
					value = NO;
				}

			}

			return value;
		}
	}

	private class ExecuteOnCellEditor implements CellEditor {
		private List<RuleActionBean> actions;
		private Boolean isExport;

		public ExecuteOnCellEditor(Boolean isExport) {
			this.isExport = isExport;
		}

		public Object getValue(Object item, String property, int rowcount) {

			if (isExport) {
				return renderExportValue(item);
			} else {
				return renderHtmlValue(item);
			}
		}

		public Object renderHtmlValue(Object item) {

			HtmlBuilder builder = new HtmlBuilder();
			actions = (List<RuleActionBean>) ((HashMap<Object, Object>) item).get("theActions");

			for (RuleActionBean ruleAction : actions) {
				builder.append(ruleAction.getExpressionEvaluatesTo() + "<br/>");
			}

			return builder.toString();
		}

		public Object renderExportValue(Object item) {

			actions = (List<RuleActionBean>) ((HashMap<Object, Object>) item).get("theActions");
			String expressionEvaluatesTo = actions.size() > 0 ? String.valueOf(actions.get(0)
					.getExpressionEvaluatesTo()) : "";
			for (int i = 1; i < actions.size(); i++) {
				expressionEvaluatesTo += " - " + actions.get(i).getExpressionEvaluatesTo();
			}
			return expressionEvaluatesTo;
		}

	}

	private class ActionTypeCellEditor implements CellEditor {
		private List<RuleActionBean> actions;
		private Boolean isExport;

		public ActionTypeCellEditor(Boolean isExport) {
			this.isExport = isExport;
		}

		public Object getValue(Object item, String property, int rowcount) {
			if (isExport) {
				return renderExportValue(item);
			} else {
				return renderHtmlValue(item);
			}
		}

		public Object renderHtmlValue(Object item) {
			HtmlBuilder builder = new HtmlBuilder();
			actions = (List<RuleActionBean>) ((HashMap<Object, Object>) item).get("theActions");

			for (RuleActionBean ruleAction : actions) {
				builder.append(ruleAction.getActionType().getDescription() + "<br/>");
			}
			return builder.toString();
		}

		public Object renderExportValue(Object item) {

			actions = (List<RuleActionBean>) ((HashMap<Object, Object>) item).get("theActions");
			String expressionEvaluatesTo = actions.size() > 0 ? String.valueOf(actions.get(0).getActionType()
					.getDescription()) : "";
			for (int i = 1; i < actions.size(); i++) {
				expressionEvaluatesTo += " ; " + actions.get(i).getActionType().getDescription();
			}
			return expressionEvaluatesTo;
		}
	}

	private class ActionSummaryCellEditor implements CellEditor {
		private List<RuleActionBean> actions;
		private Boolean isExport;

		public ActionSummaryCellEditor(Boolean isExport) {
			this.isExport = isExport;
		}

		public Object getValue(Object item, String property, int rowcount) {
			if (isExport) {
				return renderExportValue(item);
			} else {
				return renderHtmlValue(item);
			}
		}

		public Object renderHtmlValue(Object item) {

			HtmlBuilder builder = new HtmlBuilder();
			actions = (List<RuleActionBean>) ((HashMap<Object, Object>) item).get("theActions");

			builder.table(1).close();
			for (RuleActionBean ruleAction : actions) {
				for (Map.Entry<String, Object> entry : ruleAction.getPropertiesForDisplay().entrySet()) {
					builder.tr(1).close().td(1).close().append("<i>" + resword.getString(entry.getKey()) + "</i>")
							.tdEnd().td(1).close().append(entry.getValue()).tdEnd().trEnd(1);
				}
				appendRunOn(builder, ruleAction);
				appendDest(builder, ruleAction);
			}
			builder.tableEnd(1);

			return builder.toString();
		}

		public Object renderExportValue(Object item) {

			actions = (List<RuleActionBean>) ((HashMap<Object, Object>) item).get("theActions");
			String expressionEvaluatesTo = actions.size() > 0 ? String.valueOf(actions.get(0).getSummary()) : "";
			for (int i = 1; i < actions.size(); i++) {
				expressionEvaluatesTo += " ; " + actions.get(i).getSummary();
			}
			return expressionEvaluatesTo;
		}

		public void appendRunOn(HtmlBuilder builder, RuleActionBean ruleAction) {
			String s = "";
			RuleActionRunBean ruleActionRun = ruleAction.getRuleActionRun();
			if (ruleActionRun.getInitialDataEntry()) {
				s += resword.getString("IDE_comma") + " ";
			}
			if (ruleActionRun.getDoubleDataEntry()) {
				s += resword.getString("DDE_comma") + " ";
			}
			if (ruleActionRun.getAdministrativeDataEntry()) {
				s += resword.getString("ADE_comma") + " ";
			}
			if (ruleActionRun.getImportDataEntry()) {
				s += resword.getString("import_comma") + " ";
			}
			if (ruleActionRun.getBatch()) {
				s += resword.getString("batch_comma") + " ";
			}
			s = s.trim();
			s = s.substring(0, s.length() - 1);
			if (s.length() > 0) {
				builder.tr(1).close().td(1).close().append("<i>" + resword.getString("run_on_colon") + "</i>").tdEnd()
						.td(1).close().append(s).tdEnd().trEnd(1);
			}
		}

		public void appendDest(HtmlBuilder builder, RuleActionBean ruleAction) {
			ActionType actionType = ruleAction.getActionType();
			if (actionType == ActionType.INSERT) {
				InsertActionBean a = (InsertActionBean) ruleAction;
				appendDestProps(builder, a.getProperties());
				appendInsertValues(builder, a.getProperties());
			}
			if (actionType == ActionType.SHOW) {
				ShowActionBean a = (ShowActionBean) ruleAction;
				appendDestProps(builder, a.getProperties());
			}
			if (actionType == ActionType.HIDE) {
				HideActionBean a = (HideActionBean) ruleAction;
				appendDestProps(builder, a.getProperties());
			}
		}

		private void appendDestProps(HtmlBuilder builder,
				List<org.akaza.openclinica.domain.rule.action.PropertyBean> propertyBeans) {
			if (propertyBeans != null && propertyBeans.size() > 0) {
				String s = "";
				for (org.akaza.openclinica.domain.rule.action.PropertyBean p : propertyBeans) {
					s += p.getOid().trim() + ", ";
				}
				s = s.trim();
				s = s.substring(0, s.length() - 1);
				builder.tr(1).close().td(1).close().append("<i>" + resword.getString("dest_prop_colon") + "</i>")
						.tdEnd().td(1).close().append(s).tdEnd().td(1).close().tdEnd();
				builder.trEnd(1);
			}
		}

		private void appendInsertValues(HtmlBuilder builder,
									 List<org.akaza.openclinica.domain.rule.action.PropertyBean> propertyBeans) {
			if (propertyBeans != null && propertyBeans.size() > 0) {
				String value = "";
				for (org.akaza.openclinica.domain.rule.action.PropertyBean p : propertyBeans) {
					String propertyValue = p.getValue() == null ? (p.getValueExpression() == null ? "" : p.getValueExpression().getValue()) : p.getValue();
					value += value.isEmpty() ? propertyValue.trim() : (", " + propertyValue.trim());
				}
				builder.tr(1).close().td(1).close().append("<i>" + resword.getString("insert_values_colon") + "</i>")
						.tdEnd().td(1).close().append(value).tdEnd().td(1).close().tdEnd().trEnd(1);
			}
		}
	}

	private class ActionTypeDroplistFilterEditor extends DroplistFilterEditor {
		@Override
		protected List<Option> getOptions() {
			List<Option> options = new ArrayList<Option>();
			for (ActionType actionTypes : ActionType.values()) {
				options.add(new Option(actionTypes.getDescription(), actionTypes.getDescription()));
			}
			return options;
		}
	}

	private class ExpressionEvaluatesToDroplistFilterEditor extends DroplistFilterEditor {
		@Override
		protected List<Option> getOptions() {
			List<Option> options = new ArrayList<Option>();
			options.add(new Option(String.valueOf(Boolean.TRUE), String.valueOf(Boolean.TRUE)));
			options.add(new Option(String.valueOf(Boolean.FALSE), String.valueOf(Boolean.FALSE)));
			return options;
		}
	}

	private class ActionsCellEditor implements CellEditor {
		public Object getValue(Object item, String property, int rowcount) {
	
			Integer ruleSetId = (Integer) ((HashMap<Object, Object>) item).get("ruleSetId");
			Integer ruleSetRuleId = (Integer) ((HashMap<Object, Object>) item).get("ruleSetRuleId");
			Integer ruleId = (Integer) ((HashMap<Object, Object>) item).get("ruleId");
			RuleSetRuleBean ruleSetRule = (RuleSetRuleBean) ((HashMap<Object, Object>) item).get("ruleSetRule");
			HtmlBuilder actionTable = new HtmlBuilder();

			actionTable.table(0).border("0").end().tr(0).append(" class=\"innerTable\"").end();
			actionTable.td(0).end().append(buildEditRuleLink(ruleId, ruleSetRule.getId(), ruleSetRule.getRuleBean().getStudyId())).tdEnd();
			actionTable.td(0).end().append(viewLinkBuilder(ruleSetId)).tdEnd();
			
			if (ruleSetRule.getStatus() != Status.DELETED) {
				
				actionTable.td(0).end().append(executeLinkBuilder(ruleSetId, ruleId)).tdEnd();
				actionTable.td(0).end().append(removeLinkBuilder(ruleSetRuleId, ruleSetId)).tdEnd();
				actionTable.td(0).end().append(deleteLinkBuilder(ruleSetRuleId, ruleSetId)).tdEnd();
			} else {
				
				actionTable.td(0).end().append(restoreLinkBuilder(ruleSetRuleId, ruleSetId)).tdEnd();
			}
			
			actionTable.td(0).end().append(extractXmlLinkBuilder(ruleSetRuleId)).tdEnd();
			actionTable.td(0).end().append(testLinkBuilder(ruleSetRuleId)).tdEnd();
			actionTable.trEnd(0).tableEnd(0);
			
			return actionTable.toString();
		}
	}

	private class StatusCellEditor implements CellEditor {
		public Object getValue(Object item, String property, int rowcount) {
			RuleSetRuleBean ruleSetRule = (RuleSetRuleBean) new BasicCellEditor().getValue(item, "ruleSetRule",
					rowcount);
			Status status = ruleSetRule.getStatus();
			
			HtmlBuilder builder = new HtmlBuilder();

			if (status != null) {
				builder.span();
				if (status == Status.AVAILABLE) {
					builder.styleClass("aka_green_highlight");
				} else if (status == Status.DELETED || status == Status.LOCKED) {
					builder.styleClass("aka_red_highlight");
				}

				builder.close().append(status.getI18nDescription(locale)).spanEnd();
			}
			
			return builder.toString();
		}
	}

	private class StatusDroplistFilterEditor extends DroplistFilterEditor {
		@Override
		protected List<Option> getOptions() {
			List<Option> options = new ArrayList<Option>();
			options.add(new Option(Status.AVAILABLE.getI18nDescription(locale), Status.AVAILABLE
					.getI18nDescription(locale)));
			options.add(new Option(Status.DELETED.getI18nDescription(locale), Status.DELETED.getI18nDescription(locale)));
			return options;
		}
	}

	private String viewLinkBuilder(Integer ruleSetId) {
		HtmlBuilder actionLink = new HtmlBuilder();
		actionLink.a().href("ViewRuleSet?ruleSetId=" + ruleSetId);
		actionLink.append("onMouseDown=\"javascript:setImage('bt_View1','images/bt_View_d.gif');\"");
		actionLink.append("onMouseUp=\"javascript:setImage('bt_View1','images/bt_View.gif');\"");
		actionLink.append("onclick=\"setAccessedObjected(this)\"").close();
		actionLink.img().name("bt_View1").src("images/bt_View.gif").border("0").alt("View").title("View")
				.append("hspace=\"2px\"").end().aEnd();
		return actionLink.toString();

	}

	private String executeLinkBuilder(Integer ruleSetId, Integer ruleId) {
		HtmlBuilder actionLink = new HtmlBuilder();
		actionLink.a().href("RunRuleSet?ruleSetId=" + ruleSetId + "&ruleId=" + ruleId);
		actionLink.append("onMouseDown=\"javascript:setImage('bt_Run1','images/bt_ExexuteRules.gif');\"");
		actionLink.append("onMouseUp=\"javascript:setImage('bt_Run1','images/bt_ExexuteRules.gif');\"");
		actionLink.append("onclick=\"setAccessedObjected(this)\"").close();
		actionLink.img().name("bt_Run1").src("images/bt_ExexuteRules.gif").border("0").alt("Run").title("Run")
				.append("hspace=\"2px\"").end().aEnd();
		return actionLink.toString();
	}

	private String deleteLinkBuilder(Integer ruleSetRuleId, Integer ruleSetId) {
		HtmlBuilder actionLink = new HtmlBuilder();
		actionLink.a().href("UpdateRuleSetRule?action=delete&ruleSetRuleId=" + ruleSetRuleId + "&ruleSetId=" + ruleSetId);
		actionLink.append("onClick=\"return confirmDialog({ message:'"
				+ resword.getString("are_you_sure_to_delete_this_rule") + "', height:150, width:500, aLink:this, highlightRow:true });\"");
		actionLink.append("onMouseDown=\"javascript:setImage('bt_Delete1','images/bt_Delete_d.gif');\"");
		actionLink.append("onMouseUp=\"javascript:setImage('bt_Delete1','images/bt_Delete.gif');\"").close();
		actionLink.img().name("bt_Delete1").src("images/bt_Delete.gif").border("0").alt(resword.getString("delete"))
				.title(resword.getString("delete")).append("hspace=\"2px\"").end().aEnd();
		return actionLink.toString();

	}

	private String removeLinkBuilder(Integer ruleSetRuleId, Integer ruleSetId) {
		HtmlBuilder actionLink = new HtmlBuilder();
		actionLink.a().href("UpdateRuleSetRule?action=remove&ruleSetRuleId=" + ruleSetRuleId + "&ruleSetId=" + ruleSetId);
		actionLink.append("onClick=\"return confirmDialog({ message:'" + resword.getString("rule_if_you_remove_this")
				+ "', height:150, width:500, aLink:this, highlightRow:true });\"");
		actionLink.append("onMouseDown=\"javascript:setImage('bt_Remove1','images/bt_Remove.gif');\"");
		actionLink.append("onMouseUp=\"javascript:setImage('bt_Remove1','images/bt_Remove.gif');\"").close();
		actionLink.img().name("bt_Remove1").src("images/bt_Remove.gif").border("0").alt(resword.getString("remove"))
				.title(resword.getString("remove")).append("hspace=\"2px\"").end().aEnd();
		return actionLink.toString();

	}

	private String restoreLinkBuilder(Integer ruleSetRuleId, Integer ruleSetId) {
		HtmlBuilder actionLink = new HtmlBuilder();
		actionLink.a().href("UpdateRuleSetRule?action=restore&ruleSetRuleId=" + ruleSetRuleId + "&ruleSetId=" + ruleSetId);
		actionLink.append("onClick=\"return confirmDialog({ message:'" + resword.getString("rule_if_you_restore_this")
				+ "', height:150, width:500, aLink:this, highlightRow:true }); \"");
		actionLink.append("onMouseDown=\"javascript:setImage('bt_Restore3','images/bt_Restore_d.gif');\"");
		actionLink.append("onMouseUp=\"javascript:setImage('bt_Restore3','images/bt_Restore.gif');\"").close();
		actionLink.img().name("bt_Restore3").src("images/bt_Restore.gif").border("0").alt("Restore").title("Restore")
				.append("hspace=\"2px\"").end().aEnd();
		return actionLink.toString();

	}

	private String extractXmlLinkBuilder(Integer ruleSetRuleId) {
		HtmlBuilder actionLink = new HtmlBuilder();
		actionLink.a().href("DownloadRuleSetXml?ruleSetRuleIds=" + ruleSetRuleId);
		actionLink.append("onMouseDown=\"javascript:setImage('bt_Download','images/bt_Download_d.gif');\"");
		actionLink.append("onMouseUp=\"javascript:setImage('bt_Download','images/bt_Download.gif');\"");
		actionLink.append("onclick=\"setAccessedObjected(this)\"").close();
		actionLink.img().name("bt_Download").src("images/bt_Download.gif").border("0").alt("Download XML")
				.title("Download XML").append("hspace=\"2px\"").end().aEnd();
		return actionLink.toString();

	}

	private String testLinkBuilder(Integer ruleSetRuleId) {
		HtmlBuilder actionLink = new HtmlBuilder();
		actionLink.a().href("TestRule?ruleSetRuleId=" + ruleSetRuleId);
		actionLink.append("onMouseDown=\"javascript:setImage('bt_test','images/bt_EnterData_d.gif');\"");
		actionLink.append("onMouseUp=\"javascript:setImage('bt_test','images/bt_Reassign_d.gif');\"");
		actionLink.append("onclick=\"setAccessedObjected(this)\"").close();
		actionLink.img().name("bt_test").src("images/bt_Reassign_d.gif").border("0").alt("Test").title("Test")
				.append("hspace=\"2px\"").end().aEnd();
		return actionLink.toString();

	}

	private String buildEditRuleLink(Integer ruleId, Integer ruleSetRuleId, Integer study) {
		HtmlBuilder actionLink = new HtmlBuilder();
		actionLink.a().href("designer/rule.jsp?action=edit&id=" + ruleId + "&rId=" + ruleSetRuleId + "&study=" + study);
		actionLink.append("onMouseDown=\"javascript:setImage('bt_Edit1','images/bt_Edit.gif');\"");
		actionLink.append("onMouseUp=\"javascript:setImage('bt_Edit1','images/bt_Edit.gif');\"");
		actionLink.append("data-cc-ruleId=\"" + ruleId + "\" onclick=\"setAccessedObjected(this)\"").close();
		actionLink.img().name("bt_Edit1").src("images/bt_Edit.gif").border("0").alt("Edit").title("Edit")
				.append("hspace=\"2px\"").end().aEnd();
		return actionLink.toString();

	}

	/**
	 * Enable the setting of the dataSource object for the current session.
	 * 
	 * @param dataSource
	 *            Valid dataSource object.
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
