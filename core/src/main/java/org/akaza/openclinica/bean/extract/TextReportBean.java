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
package org.akaza.openclinica.bean.extract;

import java.util.ArrayList;

@SuppressWarnings({"rawtypes"})
public class TextReportBean extends ReportBean {
	protected String end;
	protected String sep;

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < metadata.size(); i++) {
			ArrayList row = (ArrayList) metadata.get(i);
			for (int j = 0; j < row.size(); j++) {
				buffer.append(row.get(j));
				buffer.append(sep);
			}
			buffer.append(end);
		}

		for (int i = 0; i < data.size(); i++) {
			ArrayList row = (ArrayList) data.get(i);
			for (int j = 0; j < row.size(); j++) {
				String s = ((String) row.get(j)).replaceAll("\\s", " ");
				buffer.append(s);
				buffer.append(sep);
			}
			buffer.append(end);
		}
		return buffer.toString();

	}
}
