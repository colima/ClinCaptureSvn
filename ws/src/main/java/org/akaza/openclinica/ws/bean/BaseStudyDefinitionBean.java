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

package org.akaza.openclinica.ws.bean;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;

public class BaseStudyDefinitionBean {

	private String studyUniqueId;
	private String siteUniqueId;
	private UserAccountBean user;
	private StudyBean study;

	public BaseStudyDefinitionBean(String studyUniqueId, String siteUniqueId, UserAccountBean user) {
		super();
		this.studyUniqueId = studyUniqueId;
		this.siteUniqueId = siteUniqueId;
		this.user = user;
	}

	public BaseStudyDefinitionBean(String studyUniqueId, UserAccountBean user) {
		super();
		this.studyUniqueId = studyUniqueId;

		this.user = user;
	}

	public String getStudyUniqueId() {
		return studyUniqueId;
	}

	public void setStudyUniqueId(String studyUniqueId) {
		this.studyUniqueId = studyUniqueId;
	}

	public String getSiteUniqueId() {
		return siteUniqueId;
	}

	public void setSiteUniqueId(String siteUniqueId) {
		this.siteUniqueId = siteUniqueId;
	}

	public UserAccountBean getUser() {
		return user;
	}

	public void setUser(UserAccountBean user) {
		this.user = user;
	}

	/**
	 * @param study
	 *            the study to set
	 */
	public void setStudy(StudyBean study) {
		this.study = study;
	}

	/**
	 * @return the study
	 */
	public StudyBean getStudy() {
		return study;
	}

}
