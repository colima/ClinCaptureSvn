/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.model;

import java.util.*;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.*;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.control.AbstractTableFactory;
import org.akaza.openclinica.control.DefaultActionsEditor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.*;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.apache.commons.lang.StringUtils;
import org.jmesa.core.filter.FilterMatcher;
import org.jmesa.core.filter.MatcherKey;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Limit;
import org.jmesa.view.component.Row;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.editor.DroplistFilterEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Encapsulates all the functionality required to create tables for coded items depending on the handler called in the controller.
 *
 */
@SuppressWarnings("rawtypes")
public class CodedItemsTableFactory extends AbstractTableFactory {

	private int studyId = -1;
	private StudyDAO studyDAO;
	private DataSource datasource;

	private EventCRFDAO eventCRFDAO;
    private ItemDataDAO itemDataDAO;
	private List<CodedItem> codedItems;
    private List<Term> terms;
	private EventDefinitionCRFDAO eventDefCRFDAO;
	private StudySubjectDAO studySubjectDAO;
	private StudyEventDefinitionDAO studyEventDefDao;
    private CRFDAO crfDAO;
    private StudyEventDAO studyEventDAO;
    private String themeColor;

    private final String medicalCodingContextNeeded;
    private final String showMoreLink;
    private final String showContext;

    public CodedItemsTableFactory(String medicalCodingContextNeeded, String showMoreLink, String showContext) {

        this.medicalCodingContextNeeded = medicalCodingContextNeeded;
        this.showMoreLink = showMoreLink;
        this.showContext = showContext;
    }

    @Override
    protected String getTableName() {
        return "codedItemsId";
    }

    @Override
    protected void configureColumns(TableFacade tableFacade, Locale locale) {

        tableFacade.setColumnProperties("itemDataValue", "dictionary", "status", "subjectName", "eventName", "crfName", "codedColumn", "actionColumn");
        
        Row row = tableFacade.getTable().getRow();
        
        configureColumn(row.getColumn("itemDataValue"), "Verbatim Term", new ItemDataValueCellEditor(), null);
        configureColumn(row.getColumn("dictionary"), "Dictionary", new DictionaryCellEditor(), new DictionaryDroplistFilterEditor());
        configureColumn(row.getColumn("status"), "Status", new StatusCellEditor(), new StatusDroplistFilterEditor());
        configureColumn(row.getColumn("subjectName"), "Study Subject ID", new SubjectCellEditor(), null, true, true);
        configureColumn(row.getColumn("eventName"), "Study Event", new EventCellEditor(), null, true, true);
        configureColumn(row.getColumn("crfName"), "CRF", new CrfCellEditor(), null, true, true);
        configureColumn(row.getColumn("codedColumn"), "Medical Codes", new CodedCellEditor(), null, false, false);
        configureColumn(row.getColumn("actionColumn"), "Actions", new ActionCellEditor(), new DefaultActionsEditor(
                locale), true, false);
    }

    @Override
    public void setDataAndLimitVariables(TableFacade tableFacade) {

        Limit limit = tableFacade.getLimit();

        if (!limit.isComplete()) {
            tableFacade.setTotalRows(codedItems.size());
        }

        Collection<HashMap<Object, Object>> codedItemsResult = new ArrayList<HashMap<Object, Object>>();
        
        for (CodedItem codedItem : codedItems) {
        	
            HashMap<Object, Object> h = new HashMap<Object, Object>();
            h.put("codedItem", codedItem);
            h.put("codedItem.itemId", codedItem.getItemId());
            h.put("itemDataValue", getItemDataValue(codedItem.getItemId()));
            h.put("subjectName", getSubjectBean(codedItem.getSubjectId()).getLabel());
            h.put("eventName", getStudyEventDefinitionBean(codedItem.getEventCrfId(), codedItem.getCrfVersionId()).getName());
            h.put("status", getCodedItemStatus(codedItem));
            h.put("crfName", getCrfName(codedItem.getCrfVersionId()));
            h.put("dictionary", getCorrectDictionaryName(codedItem.getDictionary()));

            codedItemsResult.add(h);
        }

        tableFacade.setItems(codedItemsResult);
    }

    private String getCodedItemStatus(CodedItem codedItem) {

        if (codedItem.getStatus().equals("NOT_CODED")) {

            return "Not Coded";
            
        } else if (codedItem.getStatus().equals("CODED")) {

            return "Coded";
            
        } else if ((codedItem.getStatus().equals("IN_PROCESS"))) {

            return "In Process";
            
        } else if (codedItem.getStatus().equals("CODE_NOT_FOUND")) {
        	
        	return "Code not Found";
        }

      return "Unknown";
    }

    @SuppressWarnings("unchecked")
    private class ItemDataValueCellEditor implements CellEditor {
		public Object getValue(Object item, String property, int rowcount) {

            HtmlBuilder builder = new HtmlBuilder();
            String itemDataValue = (String) ((HashMap<Object, Object>) item).get("itemDataValue");

            if (!itemDataValue.isEmpty()) {

                builder.div().name("itemDataValue").close().append(itemDataValue).divEnd()
                       .div().style("width:160px").close().divEnd();
            }

            return builder.toString();
        }
    }

    @SuppressWarnings("unchecked")
    private class DictionaryCellEditor implements CellEditor {
    	
        public Object getValue(Object item, String property, int rowcount) {

            HtmlBuilder builder = new HtmlBuilder();

            CodedItem codedItem = (CodedItem) ((HashMap<Object, Object>) item).get("codedItem");

            builder.div().name("termDictionary").close().append(getCorrectDictionaryName(codedItem.getDictionary())).divEnd()
                   .div().style("width:160px").close().divEnd();

            return builder.toString();
        }
    }

    private String getCorrectDictionaryName(String codedItemDictionary) {

        if(codedItemDictionary.equalsIgnoreCase("ICD_9CM")) {

            return "ICD 9CM";
        } else if(codedItemDictionary.equalsIgnoreCase("ICD_10")) {

            return "ICD 10";
        } else if (codedItemDictionary.equalsIgnoreCase("MEDDRA")) {

            return "MedDRA";
        } else if (codedItemDictionary.equalsIgnoreCase("WHOD")) {

            return "WHOD";
        }
        return "Dictionary Not Found";
    }
    
    @SuppressWarnings("unchecked")
    private class CodedCellEditor implements CellEditor {
    	
        public Object getValue(Object item, String property, int rowcount) {

            HtmlBuilder builder = new HtmlBuilder();
            CodedItem codedItem = (CodedItem) ((HashMap<Object, Object>) item).get("codedItem");

            if (codedItem != null) {
                
				if (isLoggedInUserMonitor()) {

                   builder.append(codedItem.getPreferredTerm()).div().style("width:250px").close().divEnd();

					return builder.toString();

				} else if (codedItem.getStatus().equals("CODED") || codedItem.getStatus().equals("IN_PROCESS")) {

                    builder.append("Search: ").input().style("border:1px solid #a6a6a6; margin-bottom: 2px; color:#4D4D4D").disabled().type("text").value(codedItem.getPreferredTerm()).close();
                } else {

                    builder.append("Search: ").input().style("border:1px solid #a6a6a6 ;margin-bottom: 2px; color:#4D4D4D").type("text").value(codedItem.getPreferredTerm()).close();
                }

                String codedItemContextBox = contextBoxBuilder(codedItem);

                builder.div().id(String.valueOf(codedItem.getItemId())).close().append(" " + codedItemContextBox + " ").divEnd();

                builder.div().style("width:420px").close().divEnd();
            }

            return builder.toString();
        }

        private String contextBoxBuilder(CodedItem codedItem) {

            HtmlBuilder builder = new HtmlBuilder();

            String showContextValue = "none";

            if(showContext.equals("true")) {

                showContextValue = "";
            }

            if (codedItem.isCoded()) {

                builder.table(1).id("tablepaging").styleClass("itemsTable").style("display:" + showContextValue + ";").close()
                        .tr(1).style(codedItem.getDictionary().equals("WHOD") ? "display:none;" : "").close()
                        .td(1).close().append("HTTP: ").tdEnd()
						.td(2).close().a().style("color:" + getThemeColor() + "").append(" target=\"_blank\" ").href("http://bioportal.bioontology.org/ontologies/"
						+ codedItem.getDictionary().replace("_", "") + "?p=classes&conceptid=" + codedItem.getHttpPath()).close().append(codedItem.getHttpPath()).aEnd().tdEnd()
						.td(3).width("360px").colspan("2").close().tdEnd()
                        .td(4).close().tdEnd().trEnd(1);


                for (CodedItemElement codedItemElement : codedItemElementsFilter(codedItem).getCodedItemElements()) {

                    builder.tr(1).close().td(1).close().append(" " + codedItemElement.getItemName() + ": ").tdEnd()
                            .td(2).close().append(codedItemElement.getItemCode()).tdEnd().tdEnd()
                            .td(3).width("360px").colspan("2").close().tdEnd()
                            .td(4).close().tdEnd().trEnd(1).trEnd(1);
                }

                builder.tableEnd(1);
            }

            return builder.toString();
        }

        private CodedItem codedItemElementsFilter(CodedItem codedItem) {

            CodedItem codedItemWithFilterFields = new CodedItem();

            for (CodedItemElement codedItemElement : codedItem.getCodedItemElements()) {

                for (CodedItemElement codedItemIteration : codedItem.getCodedItemElements()) {

					if ((codedItemElement.getItemName() + "C").equals(codedItemIteration.getItemName())) {
						if (!codedItemElement.getItemCode().isEmpty()) {
							codedItemWithFilterFields.addCodedItemElements(codedItemElement);
							break;
						}
					} else if (codedItemElement.getItemName().equals("CMP") || codedItemElement.getItemName().equals("CNTR")
							|| codedItemElement.getItemName().equals("MPNC")) {
						if (!codedItemElement.getItemCode().isEmpty()) {
							codedItemWithFilterFields.addCodedItemElements(codedItemElement);
							break;
						}
					}
                }
            }

            Collections.sort(codedItemWithFilterFields.getCodedItemElements(), new codedElementSortByItemDataId());

            return codedItemWithFilterFields;
        }

        private class codedElementSortByItemDataId implements Comparator {

            public int compare(Object o1, Object o2) {
                CodedItemElement p1 = (CodedItemElement) o1;
                CodedItemElement p2 = (CodedItemElement) o2;
                return p1.getItemDataId() - p2.getItemDataId();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private class ActionCellEditor implements CellEditor {
    	
		public Object getValue(Object item, String property, int rowcount) {

            CodedItem codedItem = (CodedItem) ((HashMap<Object, Object>) item).get("codedItem");
            EventCRFBean eventCRFBean = (EventCRFBean) eventCRFDAO.findByPK(codedItem.getEventCrfId());
            StudyBean studyBean = (StudyBean) studyDAO.findByStudySubjectId(eventCRFBean.getStudySubjectId());
            EventDefinitionCRFBean eventDefCRFBean = (EventDefinitionCRFBean) eventDefCRFDAO.findByStudyEventIdAndCRFVersionId(studyBean, eventCRFBean.getStudyEventId(), codedItem.getCrfVersionId());

            HtmlBuilder builder = new HtmlBuilder();

            if (codedItem != null) {

                String codeItemIcon = codeItemIconBuilder(codedItem);
                String uncodeItemIcon = uncodeItemIconBuilder(codedItem);
                String goToCrfIcon = goToCrfIconBuilder(eventCRFBean, eventDefCRFBean, codedItem.getItemId());

                builder.append(codeItemIcon).nbsp().nbsp()
                        .append(uncodeItemIcon).nbsp().nbsp()
                        .append(goToCrfIcon);

                builder.div().style("width:150px").close().divEnd();
            }

            return builder.toString();
        }

        private String codeItemIconBuilder(CodedItem codedItem) {

            String codedItemButtonColor = codedItem.isCoded() ? "code_confirm.png" : "code_blue.png";
            HtmlBuilder builder = new HtmlBuilder();

            if (isLoggedInUserMonitor()) {

                builder.a().append("itemId=\"" + codedItem.getItemId() + "\"").close();

            } else {

                String disabled = (codedItem.getStatus().equals("CODED") || codedItem.getStatus().equals("IN_PROCESS")) ? " block='true' " : " block='false' ";

                builder.a().onclick("codeItem(this)").append(disabled).name("Code").append("itemId=\"" + codedItem.getItemId() + "\"").close();
            }

            builder.img().style("float:left; height:17px").border("0").title(ResourceBundleProvider.getResWord("code"))
                    .alt(ResourceBundleProvider.getResWord("code")).src("../images/" + codedItemButtonColor + "").close().aEnd();

            return builder.toString();
        }

        private String uncodeItemIconBuilder(CodedItem codedItem) {

            HtmlBuilder builder = new HtmlBuilder();

            builder.a().onclick("showMedicalCodingUncodeAlertBox(this)").name("unCode")
                    .append("itemId=\"" + codedItem.getItemId() + "\"");

            Term codedItemTerm = getCodedItemTerm(codedItem);

            if(!codedItemTerm.getHttpPath().isEmpty()) {

                builder.append("term=\"" + codedItemTerm.getLocalAlias().toLowerCase() + "\" ")
                       .append("pref=\"" + codedItemTerm.getPreferredName().toLowerCase() + "\" ");
            } else {

                builder.append("term=\"\" ");
                builder.append("pref=\"\" ");
            }

            if (codedItem.isCoded()) {

                builder.close();
            } else {

                builder.style("visibility:hidden").close();
            }

            builder.img().style("height:17px").border("0").title(ResourceBundleProvider.getResWord("deleteAlias"))
                    .alt(ResourceBundleProvider.getResWord("deleteAlias")).src("../images/bt_Delete.gif").name("codeBtn").close().aEnd();

            return builder.toString();
        }

        private String goToCrfIconBuilder(EventCRFBean eventCRFBean, EventDefinitionCRFBean eventDefCRFBean, int itemId) {

            HtmlBuilder builder = new HtmlBuilder();

            builder.a().name("goToEcrf").append("itemId=\"" + itemId + "\"").append(" onmouseup=\"javascript:setImage('Complete','../images/"+ getEventCrfStatusIcon(eventCRFBean) + "');\"")
                    .href("../ViewSectionDataEntry?eventCRFId=" + eventCRFBean.getId()
                            + "&eventDefinitionCRFId=" + eventDefCRFBean.getId()
                            + "&tabId=1&eventId=" + eventCRFBean.getStudyEventId() + "&amp;viewFull=yes").close()
                    .img().border("0").title(ResourceBundleProvider.getResWord("openCrf")).alt(ResourceBundleProvider.getResWord("openCrf"))
                    .style("height:17px").src("../images/"+ getEventCrfStatusIcon(eventCRFBean) + "").close().aEnd();

            return builder.toString();
        }

        private String getEventCrfStatusIcon(EventCRFBean eventCRFBean) {

            StudyEventBean studyEventBean = (StudyEventBean) studyEventDAO.findByPK(eventCRFBean.getStudyEventId());

            String goToEcrfIcon = "";

            if (studyEventBean.getSubjectEventStatus().isLocked() || studyEventBean.getSubjectEventStatus().isStopped() || studyEventBean.getSubjectEventStatus().isSkipped()) {

                goToEcrfIcon = "icon_Locked_long.gif";
            } else  if (eventCRFBean.getStage().equals(DataEntryStage.INITIAL_DATA_ENTRY)) {

                goToEcrfIcon = "icon_InitialDE_long.gif";
            } else if (eventCRFBean.getStage().equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE) ) {

                goToEcrfIcon = "icon_InitialDEcomplete.gif";
            } else if(eventCRFBean.getStage().equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {

                goToEcrfIcon = "icon_DDE.gif";
            } else if(eventCRFBean.getStage().isDoubleDE_Complete()) {

                if (studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.SIGNED)) {

                    goToEcrfIcon = "icon_Signed_long.gif";
                } else if (eventCRFBean.isSdvStatus()) {

                    goToEcrfIcon = "icon_DoubleCheck_long.gif";
                } else {

                    goToEcrfIcon = "icon_DEcomplete_long.gif";
                }
            }

            return goToEcrfIcon;
        }
    }

    private Term getCodedItemTerm(CodedItem codedItem) {

        ItemDataDAO itemDataDAO = new ItemDataDAO(datasource);
        ItemDataBean data = (ItemDataBean) itemDataDAO.findByPK(codedItem.getItemId());

        for (Term term : terms) {

            if (data.getValue().equalsIgnoreCase(term.getLocalAlias())
                    && term.getExternalDictionaryName().equals(codedItem.getDictionary())) {

                return term;
            }
        }

        return new Term();
    }

    @SuppressWarnings("unchecked")
    private class StatusCellEditor implements CellEditor {
    	
        public Object getValue(Object item, String property, int cowcount) {

            String codedItemStatus = (String) ((HashMap<Object, Object>) item).get("status");
            HtmlBuilder builder = new HtmlBuilder();

            if (!codedItemStatus.isEmpty()) {
                builder.div().name("itemStatus").close()
                .append(codedItemStatus).divEnd().div().style("width:100px").close().divEnd();
            }
            return builder.toString();
        }
    }

    @SuppressWarnings("unchecked")
    private class SubjectCellEditor implements CellEditor {
        public Object getValue(Object item, String property, int cowcount) {

            HtmlBuilder builder = new HtmlBuilder();
            String subjectLabel = (String) ((HashMap<Object, Object>) item).get("subjectName");

            if (!subjectLabel.isEmpty()) {
                builder.div().name("subjectId").close()
                        .append(subjectLabel).divEnd().div().style("width:100px").close().divEnd();
            }

            return builder.toString();
        }
    }

    @SuppressWarnings("unchecked")
    private class EventCellEditor implements CellEditor {
        public Object getValue(Object item, String property, int cowcount) {

            HtmlBuilder builder = new HtmlBuilder();
            String eventName = (String) ((HashMap<Object, Object>) item).get("eventName");

            if (!eventName.isEmpty()) {

                builder.div().name("eventName").close()
                        .append(eventName).divEnd().div().style("width:100px").close().divEnd();
            }

            return builder.toString();
        }
    }

    @SuppressWarnings("unchecked")
    private class CrfCellEditor implements CellEditor {
        public Object getValue(Object item, String property, int cowcount) {

            String crfName = (String) ((HashMap<Object, Object>) item).get("crfName");
            HtmlBuilder builder = new HtmlBuilder();

            if (!crfName.isEmpty()) {

                builder.div().name("crfName").close()
                        .append(crfName).divEnd().div().style("width:120px").close().divEnd();
            }

            return builder.toString();
        }
    }

    private class StatusDroplistFilterEditor extends DroplistFilterEditor {
    	
        protected List<Option> getOptions() {
        	
        	StudyParameterValueBean mcApprovalNeeded = new StudyParameterValueDAO(datasource).findByHandleAndStudy(studyId, "medicalCodingApprovalNeeded");
        	
            List<Option> options = new ArrayList<Option>();
            options.add(new Option("All", "All"));
            options.add(new Option("Not Coded", "Not Coded"));
            
			if (mcApprovalNeeded.getValue().equals("yes")) {
				options.add(new Option("Not Approved", "Not Approved"));
			}

		    options.add(new Option("Code not Found", "Code not Found"));
            options.add(new Option("Coded", "Coded"));
            
            return options;
        }
    }

    private class DictionaryDroplistFilterEditor extends DroplistFilterEditor {

        protected List<Option> getOptions() {

            List<Option> options = new ArrayList<Option>();

            options.add(new Option("ICD 9CM", "ICD 9CM"));
            options.add(new Option("ICD 10", " ICD 10"));
            options.add(new Option("MedDRA", "MedDRA"));
            options.add(new Option("WHOD", "WHOD"));

            return options;
        }
    }

    public void setStudyDAO(StudyDAO studyDAO) {
        this.studyDAO = studyDAO;
    }
    
    public void setCodedItems(List<CodedItem> codedItems) {
        this.codedItems = codedItems;
    }

    public void setEventCRFDAO(EventCRFDAO eventCRFDAO) {
        this.eventCRFDAO = eventCRFDAO;
    }

    public void setEventDefinitionCRFDAO(EventDefinitionCRFDAO eventDefenitionCRFDAO) {
        this.eventDefCRFDAO = eventDefenitionCRFDAO;
    }

    public void setTerms(List<Term> terms) {
        this.terms = terms;
    }

    public void setThemeColor(String themeColor) { this.themeColor = themeColor; }

    public void setStudyId(int studyId) { this.studyId = studyId; }

	public void setDataSource(DataSource datasource) {
		this.datasource = datasource;
	}
	
	public boolean isLoggedInUserMonitor() {

		UserAccountDAO userDAO = new UserAccountDAO(datasource);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		UserAccountBean loggedInUser = (UserAccountBean) userDAO.findByUserName(authentication.getName());

		return loggedInUser.getRoleByStudy(studyId).getName().equalsIgnoreCase("study monitor");
	}
	
	public void setStudySubjectDAO(StudySubjectDAO studySubjectDAO) { this.studySubjectDAO = studySubjectDAO; }

    public void setCrfDAO(CRFDAO crfDao) { this.crfDAO = crfDao; }

	public void setStudyEventDefinitionDAO(StudyEventDefinitionDAO studyEventDefDao) { this.studyEventDefDao = studyEventDefDao; }

    public void setItemDataDAO(ItemDataDAO itemDataDAO) { this.itemDataDAO = itemDataDAO; }

    public void setStudyEventDAO(StudyEventDAO studyEventDAO) { this.studyEventDAO = studyEventDAO; }

    private StudySubjectBean getSubjectBean(int subjectId) {
    	
        return  (StudySubjectBean) studySubjectDAO.findByPK(subjectId);
    }

    private StudyEventDefinitionBean getStudyEventDefinitionBean(int eventCrfId, int crfVersionId) {
    	
        EventCRFBean eventCRFBean = (EventCRFBean) eventCRFDAO.findByPK(eventCrfId);
        StudyBean studyBean = (StudyBean) studyDAO.findByStudySubjectId(eventCRFBean.getStudySubjectId());
        EventDefinitionCRFBean eventDefCRFBean = (EventDefinitionCRFBean) eventDefCRFDAO.findByStudyEventIdAndCRFVersionId(studyBean, eventCRFBean.getStudyEventId(), crfVersionId);
        
        return studyEventDefDao.findByEventDefinitionCRFId(eventDefCRFBean.getId());
    }

    private String getItemDataValue(int itemId) {
        ItemDataBean itemDataBean = (ItemDataBean) itemDataDAO.findByPK(itemId);
        return itemDataBean.getValue();
    }

    private String getCrfName(int eventCrfVersionId) {
            CRFBean crfBean = crfDAO.findByVersionId(eventCrfVersionId);
           return crfBean.getName();
    }

    @Override
    public void configureTableFacadePostColumnConfiguration(TableFacade tableFacade) {

        boolean showMore = false;
        boolean showCodedItemsContext = false;
        boolean contextNeeded = false;

        if (showMoreLink.equalsIgnoreCase("true")) {
            showMore = true;
        }

        if (showContext.equalsIgnoreCase("true")) {
            showCodedItemsContext = true;
        }

        if (medicalCodingContextNeeded.equalsIgnoreCase("yes")) {
            contextNeeded = true;
        }

        CodedItemsTableToolbar toolbar = new CodedItemsTableToolbar(showMore, contextNeeded, showCodedItemsContext);
        tableFacade.setToolbar(toolbar);
    }

    @Override
    public void configureTableFacade(HttpServletResponse response, TableFacade tableFacade) {

        super.configureTableFacade(response, tableFacade);

        tableFacade.addFilterMatcher(new MatcherKey(String.class, "status"), new StatusFilterMatcher());
    }

    public class StatusFilterMatcher implements FilterMatcher {
        public boolean evaluate(Object itemValue, String filterValue) {

            String item = StringUtils.lowerCase(String.valueOf(itemValue));
            String filter = StringUtils.lowerCase(String.valueOf(filterValue));

            if (filter.equals(item) || filter.equals("all")) {
                return true;
            }
            return false;
        }
    }

    public String getThemeColor() {

        if (themeColor.equalsIgnoreCase("violet")) {

            return "#aa62c6";

        } else if (themeColor.equalsIgnoreCase("green")) {

            return "#75b894";
        }

        return "#729fcf";
    }
}
