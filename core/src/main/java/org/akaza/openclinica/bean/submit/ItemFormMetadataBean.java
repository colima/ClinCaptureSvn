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
package org.akaza.openclinica.bean.submit;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.core.form.StringUtil;

import com.clinovo.util.DRUtil;

/**
 * ItemFormMetadataBean.
 */
@SuppressWarnings({"rawtypes", "serial"})
public class ItemFormMetadataBean extends EntityBean implements Comparable {
	//
	private int itemId;
	private int crfVersionId;
	private String header;
	private String subHeader;
	private int parentId;
	private String parentLabel;
	private int columnNumber;
	private String pageNumberLabel;
	private String codeRef = "";
	private String questionNumberLabel;
	private String leftItemText;
	private String rightItemText;
	private int sectionId;
	private int responseSetId;
	private String regexp;
	private String regexpErrorMsg;
	private int ordinal;
	private boolean required;
	private String defaultValue;
	private String widthDecimal;
	private boolean showItem;
	private boolean pseudoChild;
	private String groupLabel;
	private String responseLayout;
	private String crfVersionName; // not in the DB,only for display purpose
	private String crfName; // not in the DB
	private String sectionName; // not in the DB only for display
	private int repeatMax; // not in the DB,
	private boolean isHighlighted; // not in the db
	private ResponseSetBean responseSet; // not in the database
	private String conditionalDisplay; // simple_conditional_display

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + columnNumber;
		result = prime * result + ((conditionalDisplay == null) ? 0 : conditionalDisplay.hashCode());
		result = prime * result + ((crfName == null) ? 0 : crfName.hashCode());
		result = prime * result + crfVersionId;
		result = prime * result + ((crfVersionName == null) ? 0 : crfVersionName.hashCode());
		result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
		result = prime * result + ((groupLabel == null) ? 0 : groupLabel.hashCode());
		result = prime * result + ((header == null) ? 0 : header.hashCode());
		result = prime * result + (isHighlighted ? INT_1231 : INT_1237);
		result = prime * result + itemId;
		result = prime * result + ((leftItemText == null) ? 0 : leftItemText.hashCode());
		result = prime * result + ordinal;
		result = prime * result + ((pageNumberLabel == null) ? 0 : pageNumberLabel.hashCode());
		result = prime * result + parentId;
		result = prime * result + ((parentLabel == null) ? 0 : parentLabel.hashCode());
		result = prime * result + ((questionNumberLabel == null) ? 0 : questionNumberLabel.hashCode());
		result = prime * result + ((regexp == null) ? 0 : regexp.hashCode());
		result = prime * result + ((regexpErrorMsg == null) ? 0 : regexpErrorMsg.hashCode());
		result = prime * result + repeatMax;
		result = prime * result + (required ? INT_1231 : INT_1237);
		result = prime * result + ((responseLayout == null) ? 0 : responseLayout.hashCode());
		result = prime * result + ((responseSet == null) ? 0 : responseSet.hashCode());
		result = prime * result + responseSetId;
		result = prime * result + ((rightItemText == null) ? 0 : rightItemText.hashCode());
		result = prime * result + sectionId;
		result = prime * result + ((sectionName == null) ? 0 : sectionName.hashCode());
		result = prime * result + (showItem ? INT_1231 : INT_1237);
		result = prime * result + ((subHeader == null) ? 0 : subHeader.hashCode());
		result = prime * result + ((widthDecimal == null) ? 0 : widthDecimal.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ItemFormMetadataBean other = (ItemFormMetadataBean) obj;
		if (columnNumber != other.columnNumber) {
			return false;
		}
		if (conditionalDisplay == null) {
			if (other.conditionalDisplay != null) {
				return false;
			}
		} else if (!conditionalDisplay.equals(other.conditionalDisplay)) {
			return false;
		}
		if (crfName == null) {
			if (other.crfName != null) {
				return false;
			}
		} else if (!crfName.equals(other.crfName)) {
			return false;
		}
		if (crfVersionId != other.crfVersionId) {
			return false;
		}
		if (crfVersionName == null) {
			if (other.crfVersionName != null) {
				return false;
			}
		} else if (!crfVersionName.equals(other.crfVersionName)) {
			return false;
		}
		if (defaultValue == null) {
			if (other.defaultValue != null) {
				return false;
			}
		} else if (!defaultValue.equals(other.defaultValue)) {
			return false;
		}
		if (groupLabel == null) {
			if (other.groupLabel != null) {
				return false;
			}
		} else if (!groupLabel.equals(other.groupLabel)) {
			return false;
		}
		if (header == null) {
			if (other.header != null) {
				return false;
			}
		} else if (!header.equals(other.header)) {
			return false;
		}
		if (isHighlighted != other.isHighlighted) {
			return false;
		}
		if (itemId != other.itemId) {
			return false;
		}
		if (leftItemText == null) {
			if (other.leftItemText != null) {
				return false;
			}
		} else if (!leftItemText.equals(other.leftItemText)) {
			return false;
		}
		if (ordinal != other.ordinal) {
			return false;
		}
		if (pageNumberLabel == null) {
			if (other.pageNumberLabel != null) {
				return false;
			}
		} else if (!pageNumberLabel.equals(other.pageNumberLabel)) {
			return false;
		}
		if (parentId != other.parentId) {
			return false;
		}
		if (parentLabel == null) {
			if (other.parentLabel != null) {
				return false;
			}
		} else if (!parentLabel.equals(other.parentLabel)) {
			return false;
		}
		if (questionNumberLabel == null) {
			if (other.questionNumberLabel != null) {
				return false;
			}
		} else if (!questionNumberLabel.equals(other.questionNumberLabel)) {
			return false;
		}
		if (regexp == null) {
			if (other.regexp != null) {
				return false;
			}
		} else if (!regexp.equals(other.regexp)) {
			return false;
		}
		if (regexpErrorMsg == null) {
			if (other.regexpErrorMsg != null) {
				return false;
			}
		} else if (!regexpErrorMsg.equals(other.regexpErrorMsg)) {
			return false;
		}
		if (repeatMax != other.repeatMax) {
			return false;
		}
		if (required != other.required) {
			return false;
		}
		if (responseLayout == null) {
			if (other.responseLayout != null) {
				return false;
			}
		} else if (!responseLayout.equals(other.responseLayout)) {
			return false;
		}
		if (responseSet == null) {
			if (other.responseSet != null) {
				return false;
			}
		} else if (!responseSet.equals(other.responseSet)) {
			return false;
		}
		if (responseSetId != other.responseSetId) {
			return false;
		}
		if (rightItemText == null) {
			if (other.rightItemText != null) {
				return false;
			}
		} else if (!rightItemText.equals(other.rightItemText)) {
			return false;
		}
		if (sectionId != other.sectionId) {
			return false;
		}
		if (sectionName == null) {
			if (other.sectionName != null) {
				return false;
			}
		} else if (!sectionName.equals(other.sectionName)) {
			return false;
		}
		if (showItem != other.showItem) {
			return false;
		}
		if (subHeader == null) {
			if (other.subHeader != null) {
				return false;
			}
		} else if (!subHeader.equals(other.subHeader)) {
			return false;
		}
		if (widthDecimal == null) {
			if (other.widthDecimal != null) {
				return false;
			}
		} else if (!widthDecimal.equals(other.widthDecimal)) {
			return false;
		}
		if (pseudoChild != other.pseudoChild) {
			return false;
		}
		return true;
	}

	/**
	 * ItemFormMetadataBean clone method.
	 * 
	 * @return ItemFormMetadataBean
	 */
	public ItemFormMetadataBean copy() {
		ItemFormMetadataBean ifmb = new ItemFormMetadataBean();
		ifmb.setId(getId());
		ifmb.setItemId(getItemId());
		ifmb.setCrfVersionId(getCrfVersionId());
		ifmb.setHeader(getHeader());
		ifmb.setSubHeader(getSubHeader());
		ifmb.setParentId(getParentId());
		ifmb.setParentLabel(getParentLabel());
		ifmb.setColumnNumber(getColumnNumber());
		ifmb.setPageNumberLabel(getPageNumberLabel());
		ifmb.setQuestionNumberLabel(getQuestionNumberLabel());
		ifmb.setLeftItemText(getLeftItemText());
		ifmb.setRightItemText(getRightItemText());
		ifmb.setSectionId(getSectionId());
		ifmb.setResponseSetId(getResponseSetId());
		ifmb.setRegexp(getRegexp());
		ifmb.setRegexpErrorMsg(getRegexpErrorMsg());
		ifmb.setOrdinal(getOrdinal());
		ifmb.setRequired(isRequired());
		ifmb.setDefaultValue(getDefaultValue());
		ifmb.setResponseLayout(getResponseLayout());
		ifmb.setWidthDecimal(getWidthDecimal());
		ifmb.setShowItem(isShowItem());
		ifmb.setCodeRef(getCodeRef());
		ifmb.setCrfVersionName(getCrfVersionName());
		ifmb.setGroupLabel(getGroupLabel());
		ifmb.setSectionName(getSectionName());
		ifmb.setRepeatMax(getRepeatMax());
		ifmb.setConditionalDisplay(getConditionalDisplay());
		ifmb.setCrfName(getCrfName());
		ifmb.setHighlighted(isHighlighted());
		ifmb.setActive(isActive());
		ifmb.setName(getName());
		ResponseSetBean rsb = new ResponseSetBean();
		rsb.setId(getResponseSet().getId());
		rsb.setLabel(getResponseSet().getLabel());
		rsb.setResponseType(getResponseSet().getResponseType());
		rsb.setResponseTypeId(getResponseSet().getResponseTypeId());
		for (ResponseOptionBean ro : getResponseSet().getOptions()) {
			rsb.addOption(ro.copy());
		}
		ifmb.setResponseSet(rsb);
		ifmb.setPseudoChild(isPseudoChild());
		return ifmb;
	}

	/**
	 * ItemFormMetadataBean constructor.
	 */
	public ItemFormMetadataBean() {
		itemId = 0;
		crfVersionId = 0;
		header = "";
		responseLayout = "";
		groupLabel = "";
		subHeader = "";
		parentId = 0;
		parentLabel = "";
		columnNumber = 1;
		pageNumberLabel = "";
		questionNumberLabel = "";
		leftItemText = "";
		rightItemText = "";
		sectionId = 0;
		responseSetId = 0;
		regexp = "";
		regexpErrorMsg = "";
		ordinal = 0;
		required = false;
		showItem = true;
		defaultValue = "";
		responseSet = new ResponseSetBean();
		isHighlighted = false;
		conditionalDisplay = "";
		pseudoChild = false;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * If more than one default, will remove all the spaces between each default value.
	 *
	 * @param defaults
	 *            String
	 */
	public void setDefaultValue(String defaults) {

		if (!StringUtil.isBlank(defaults)) {
			String[] defaults2 = defaults.split(",", -1);

			for (int i = 0; i < defaults2.length; i++) {
				if (defaults2[i] == null) {
					continue;
				}
				String t = defaults2[i].trim();

				this.defaultValue = defaultValue + t;
				if (i < defaults2.length - 1) {
					// don't want to add comma at the end, only in between
					this.defaultValue = defaultValue + ",";
				}
			}
		} else {
			this.defaultValue = "";
		}

	}

	public String getGroupLabel() {
		return groupLabel;
	}

	public int getRepeatMax() {
		return repeatMax;
	}

	public void setRepeatMax(int repeatMax) {
		this.repeatMax = repeatMax;
	}

	public boolean isShowItem() {
		return showItem;
	}

	public void setShowItem(boolean showItem) {
		this.showItem = showItem;
	}

	public boolean isHighlighted() {
		return isHighlighted;
	}

	public void setHighlighted(boolean isHighlighted) {
		this.isHighlighted = isHighlighted;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public void setGroupLabel(String groupLabel) {
		this.groupLabel = groupLabel;
	}

	/**
	 * @return Returns the crfName.
	 */
	public String getCrfName() {
		return crfName;
	}

	/**
	 * @param crfName
	 *            The crfName to set.
	 */
	public void setCrfName(String crfName) {
		this.crfName = crfName;
	}

	/**
	 * @return Returns the crfVersionName.
	 */
	public String getCrfVersionName() {
		return crfVersionName;
	}

	/**
	 * @param crfVersionName
	 *            The crfVersionName to set.
	 */
	public void setCrfVersionName(String crfVersionName) {
		this.crfVersionName = crfVersionName;
	}

	/**
	 * @return Returns the columnNumber.
	 */
	public int getColumnNumber() {
		return columnNumber;
	}

	/**
	 * @param columnNumber
	 *            The columnNumber to set.
	 */
	public void setColumnNumber(int columnNumber) {
		if (columnNumber >= 1) {
			this.columnNumber = columnNumber;
		}
	}

	/**
	 * @return Returns the crfVersionId.
	 */
	public int getCrfVersionId() {
		return crfVersionId;
	}

	/**
	 * @param crfVersionId
	 *            The crfVersionId to set.
	 */
	public void setCrfVersionId(int crfVersionId) {
		this.crfVersionId = crfVersionId;
	}

	/**
	 * @return Returns the header.
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * @param header
	 *            The header to set.
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * @return Returns the itemId.
	 */
	public int getItemId() {
		return itemId;
	}

	/**
	 * @param itemId
	 *            The itemId to set.
	 */
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	/**
	 * @return Returns the leftItemText.
	 */
	public String getLeftItemText() {
		return leftItemText;
	}

	/**
	 * @param leftItemText
	 *            The leftItemText to set.
	 */
	public void setLeftItemText(String leftItemText) {
		this.leftItemText = leftItemText;
	}

	/**
	 * @return Returns the ordinal.
	 */
	public int getOrdinal() {
		return ordinal;
	}

	/**
	 * @param ordinal
	 *            The ordinal to set.
	 */
	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	/**
	 * @return Returns the pageNumberLabel.
	 */
	public String getPageNumberLabel() {
		return pageNumberLabel;
	}

	/**
	 * @param pageNumberLabel
	 *            The pageNumberLabel to set.
	 */
	public void setPageNumberLabel(String pageNumberLabel) {
		this.pageNumberLabel = pageNumberLabel;
	}

	/**
	 * @return Returns the parentId.
	 */
	public int getParentId() {
		return parentId;
	}

	/**
	 * @param parentId
	 *            The parentId to set.
	 */
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return Returns the parentLabel.
	 */
	public String getParentLabel() {
		return parentLabel;
	}

	/**
	 * @param parentLabel
	 *            The parentLabel to set.
	 */
	public void setParentLabel(String parentLabel) {
		this.parentLabel = parentLabel;
	}

	/**
	 * @return Returns the questionNumberLabel.
	 */
	public String getQuestionNumberLabel() {
		return questionNumberLabel;
	}

	/**
	 * @param questionNumberLabel
	 *            The questionNumberLabel to set.
	 */
	public void setQuestionNumberLabel(String questionNumberLabel) {
		this.questionNumberLabel = questionNumberLabel;
	}

	/**
	 * @return Returns the regexp.
	 */
	public String getRegexp() {
		return regexp;
	}

	/**
	 * @param regexp
	 *            The regexp to set.
	 */
	public void setRegexp(String regexp) {
		this.regexp = regexp;
	}

	/**
	 * @return Returns the regexpErrorMsg.
	 */
	public String getRegexpErrorMsg() {
		return regexpErrorMsg;
	}

	/**
	 * @param regexpErrorMsg
	 *            The regexpErrorMsg to set.
	 */
	public void setRegexpErrorMsg(String regexpErrorMsg) {
		this.regexpErrorMsg = regexpErrorMsg;
	}

	/**
	 * @return Returns the responseSetId.
	 */
	public int getResponseSetId() {
		return responseSetId;
	}

	/**
	 * @param responseSetId
	 *            The responseSetId to set.
	 */
	public void setResponseSetId(int responseSetId) {
		this.responseSetId = responseSetId;
	}

	/**
	 * @return Returns the rightItemText.
	 */
	public String getRightItemText() {
		return rightItemText;
	}

	/**
	 * @param rightItemText
	 *            The rightItemText to set.
	 */
	public void setRightItemText(String rightItemText) {
		this.rightItemText = rightItemText;
	}

	/**
	 * @return Returns the sectionId.
	 */
	public int getSectionId() {
		return sectionId;
	}

	/**
	 * @param sectionId
	 *            The sectionId to set.
	 */
	public void setSectionId(int sectionId) {
		this.sectionId = sectionId;
	}

	/**
	 * @return Returns the subHeader.
	 */
	public String getSubHeader() {
		return subHeader;
	}

	/**
	 * @param subHeader
	 *            The subHeader to set.
	 */
	public void setSubHeader(String subHeader) {
		this.subHeader = subHeader;
	}

	/**
	 * @return Returns the responseSet.
	 */
	public ResponseSetBean getResponseSet() {
		return responseSet;
	}

	/**
	 * @param responseSet
	 *            The responseSet to set.
	 */
	public void setResponseSet(ResponseSetBean responseSet) {
		this.responseSet = responseSet;
	}

	/**
	 * @return Returns the required.
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * @param required
	 *            The required to set.
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getResponseLayout() {
		return responseLayout;
	}

	public void setResponseLayout(String responseLayout) {
		this.responseLayout = responseLayout;
	}

	/**
	 * Method that compares objects.
	 * 
	 * @param o
	 *            Object
	 * @return int
	 */
	public int compareTo(Object o) {
		if (o == null || !(o instanceof ItemFormMetadataBean)) {
			return 1;
		}
		int ordinal = ((ItemFormMetadataBean) o).getOrdinal();
		return Integer.valueOf(this.getOrdinal()).compareTo(ordinal);
	}

	public String getWidthDecimal() {
		return widthDecimal;
	}

	public void setWidthDecimal(String widthDecimal) {
		this.widthDecimal = widthDecimal;
	}

	public String getConditionalDisplay() {
		return conditionalDisplay;
	}

	public void setConditionalDisplay(String conditionalDisplay) {
		this.conditionalDisplay = conditionalDisplay;
	}

	/**
	 * Return true if conditionalDisplay String length > 0.
	 * 
	 * @return boolean
	 */
	public boolean isConditionalDisplayItem() {
		return this.conditionalDisplay != null && this.conditionalDisplay.length() > 0;
	}

	public String getCodeRef() {
		return this.codeRef;
	}

	public void setCodeRef(String codeRef) {
		this.codeRef = codeRef;
	}

	/**
	 * @return Returns text without break spaces from the leftItemText.
	 */
	public String getTextFromLeftItemText() {
		return DRUtil.getTextFromHTML(this.leftItemText.replaceAll("&nbsp;", ""));
	}

	/**
	 * Method returns text without break spaces from the rightItemText.
	 * 
	 * @param leftItemText
	 *            String
	 * @return String
	 */
	public String getTextFromRightItemText(String leftItemText) {
		return DRUtil.getTextFromHTML(this.rightItemText.replaceAll("&nbsp;", ""));
	}

	public boolean isPseudoChild() {
		return pseudoChild;
	}

	public void setPseudoChild(boolean pseudoChild) {
		this.pseudoChild = pseudoChild;
	}
}
