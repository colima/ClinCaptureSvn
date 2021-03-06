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
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 *
 */

package org.akaza.openclinica.bean.odmbeans;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author ywang (May, 2008)
 * 
 */

public class CodeListBean extends ElementOIDBean {
	private String name;
	private String dataType;
	private String preSASFormatName;
	private List<CodeListItemBean> codeListItems;

	public CodeListBean() {
		this.codeListItems = new ArrayList<CodeListItemBean>();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setDataType(String datatype) {
		this.dataType = datatype;
	}

	public String getDataType() {
		return this.dataType;
	}

	public void setPreSASFormatName(String sasname) {
		this.preSASFormatName = sasname;
	}

	public String getPreSASFormatName() {
		return this.preSASFormatName;
	}

	public void setCodeListItems(List<CodeListItemBean> codeListItems) {
		this.codeListItems = codeListItems;
	}

	public List<CodeListItemBean> getCodeListItems() {
		return this.codeListItems;
	}
}
