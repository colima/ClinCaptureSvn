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

package org.akaza.openclinica.bean.submit;

import java.util.ArrayList;
import java.util.List;

/**
 * DisplayItemGroupBean.
 */
@SuppressWarnings("rawtypes")
public class DisplayItemGroupBean implements Comparable {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((editFlag == null) ? 0 : editFlag.hashCode());
		result = prime * result + formInputOrdinal;
		result = prime * result + ((groupMetaBean == null) ? 0 : groupMetaBean.hashCode());
		result = prime * result + index;
		result = prime * result + ((inputId == null) ? 0 : inputId.hashCode());
		result = prime * result + ((itemGroupBean == null) ? 0 : itemGroupBean.hashCode());
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		result = prime * result + ordinal;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DisplayItemGroupBean other = (DisplayItemGroupBean) obj;
		if (editFlag == null) {
			if (other.editFlag != null)
				return false;
		} else if (!editFlag.equals(other.editFlag))
			return false;
		if (formInputOrdinal != other.formInputOrdinal)
			return false;
		if (groupMetaBean == null) {
			if (other.groupMetaBean != null)
				return false;
		} else if (!groupMetaBean.equals(other.groupMetaBean))
			return false;
		if (index != other.index)
			return false;
		if (inputId == null) {
			if (other.inputId != null)
				return false;
		} else if (!inputId.equals(other.inputId))
			return false;
		if (itemGroupBean == null) {
			if (other.itemGroupBean != null)
				return false;
		} else if (!itemGroupBean.equals(other.itemGroupBean))
			return false;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		if (ordinal != other.ordinal)
			return false;
		return true;
	}

	private ItemGroupBean itemGroupBean;
	private ItemGroupMetadataBean groupMetaBean;
	private List<DisplayItemBean> items;
	private int ordinal;
	private String editFlag; // add, edit or remove
	private int formInputOrdinal;
	private String inputId; // @See loadFormValueForItemGroup()
	private int index;

	/**
	 * Default constructor.
	 */
	public DisplayItemGroupBean() {
		this.itemGroupBean = new ItemGroupBean();
		this.groupMetaBean = new ItemGroupMetadataBean();
		this.items = new ArrayList<DisplayItemBean>();
		ordinal = 0;
		editFlag = "";
		formInputOrdinal = 0;
	}

	public ItemGroupBean getItemGroupBean() {
		return itemGroupBean;
	}

	/**
	 * @return the groupMetaBean
	 */
	public ItemGroupMetadataBean getGroupMetaBean() {
		return groupMetaBean;
	}

	/**
	 * @param groupMetaBean
	 *            the groupMetaBean to set
	 */
	public void setGroupMetaBean(ItemGroupMetadataBean groupMetaBean) {
		this.groupMetaBean = groupMetaBean;
	}

	public void setItemGroupBean(ItemGroupBean formGroupBean) {
		this.itemGroupBean = formGroupBean;
	}

	public List<DisplayItemBean> getItems() {
		return items;
	}

	public void setItems(List<DisplayItemBean> items) {
		this.items = items;
	}

	/**
	 * @return the ordinal
	 */
	public int getOrdinal() {
		return ordinal;
	}

	/**
	 * @param ordinal
	 *            the ordinal to set
	 */
	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	/**
	 * @return the editFlag
	 */
	public String getEditFlag() {
		return editFlag;
	}

	/**
	 * @param editFlag
	 *            the editFlag to set
	 */
	public void setEditFlag(String editFlag) {
		this.editFlag = editFlag;
	}

	/**
	 * @return the formInputOrdinal
	 */
	public int getFormInputOrdinal() {
		return formInputOrdinal;
	}

	/**
	 * @param formInputOrdinal
	 *            the formInputOrdinal to set
	 */
	public void setFormInputOrdinal(int formInputOrdinal) {
		this.formInputOrdinal = formInputOrdinal;
	}

	public String getInputId() {
		return inputId;
	}

	public void setInputId(String inputId) {
		this.inputId = inputId;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(Object o) {
		if (!o.getClass().equals(this.getClass())) {
			return 0;
		}

		DisplayItemGroupBean arg = (DisplayItemGroupBean) o;
		return getOrdinal() - arg.getOrdinal();
	}
}
