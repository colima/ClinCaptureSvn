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

package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * view Import File Server
 * 
 * @author thickerson, purpose is to be able to show an external file in a log to a user
 * 
 */
@SuppressWarnings({ "serial" })
@Component
public class ViewLogMessageServlet extends Controller {

	public static final String DEST_DIR = "Event_CRF_Data";
	public static final String IMPORT_DIR = SQLInitServlet.getField("filePath") + DEST_DIR + File.separator;

	private static final String LOG_MESSAGE = "logmsg";
	private static final String FILE_NAME = "filename";
	private static final String TRIGGER_NAME = "tname";
	private static final String GROUP_NAME = "gname";

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU,
				resexception.getString("not_allowed_access_extract_data_servlet"), "1");// TODO

	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			File destDirectory = new File(IMPORT_DIR);
			FormProcessor fp = new FormProcessor(request);
			String regex = "\\s+"; // all whitespace, one or more times
			String replacement = "_"; // replace with underscores
			String fileName = fp.getString("n");
			String triggerName = fp.getString("tn");
			String groupName = fp.getString("gn");
			System.out.println("found trigger name " + triggerName + " group name " + groupName);
			File logDestDirectory = new File(destDirectory + File.separator + fileName.replaceAll(regex, replacement)
					+ ".log.txt" + File.separator + "log.txt");
			String fileContents = readFromFile(logDestDirectory);
			request.setAttribute(ViewLogMessageServlet.LOG_MESSAGE, fileContents);
			request.setAttribute(ViewLogMessageServlet.FILE_NAME, fileName);
			request.setAttribute(ViewLogMessageServlet.TRIGGER_NAME, triggerName);
			request.setAttribute(ViewLogMessageServlet.GROUP_NAME, groupName);
			// need to also set the information back to the original view jobs
			// so we have to get back to this type of page:
			// http://localhost:8081/OpenClinica-3.0-SNAPSHOT/ViewSingleJob?tname=test%20job%2001&gname=1
			forwardPage(Page.VIEW_LOG_MESSAGE, request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			addPageMessage(
					respage.getString("no_have_correct_privilege_current_study")
							+ respage.getString("change_study_contact_sysadmin"), request);
			forwardPage(Page.MENU, request, response);
		}
	}

	public static String readFromFile(File filename) throws java.io.IOException {

		StringBuilder readBuffer = new StringBuilder();
		BufferedReader fileReader = new BufferedReader(new FileReader(filename));

		char[] readChars = new char[1024];
		int count;
		while ((count = fileReader.read(readChars)) >= 0) {
			readBuffer.append(readChars, 0, count);
		}
		fileReader.close();
		return readBuffer.toString();

	}

}
