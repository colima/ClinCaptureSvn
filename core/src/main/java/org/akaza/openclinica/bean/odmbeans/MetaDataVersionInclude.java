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

/**
 * 
 * @author ywang (May, 2008)
 * 
 */

public class MetaDataVersionInclude {
	private String studyOID;
	private String metaDataVersionOID;

	public void setStudyOID(String studyoid) {
		this.studyOID = studyoid;
	}

	public String getStudyOID() {
		return this.studyOID;
	}

	public void setMetaDataVersionOID(String metadataVersionOID) {
		this.metaDataVersionOID = metadataVersionOID;
	}

	public String getMetaDataVersionOID() {
		return this.metaDataVersionOID;
	}
}
